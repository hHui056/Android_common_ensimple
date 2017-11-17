package com.allen.common.network;


import com.allen.common.log.LogTag;
import com.allen.common.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hHui on 2017/8/2.
 */
public class TcpClient {

    //统一的数据处理线程Executor
    private static final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
    //统一的数据处理线程Scheduler
    private static final Scheduler singleScheduler = Schedulers.from(singleExecutor);
    private String ip;
    private int port;
    private int reconnectCount = 20;
    private int reconnectInterval = 200;
    //接收Tcp数据缓冲区大小（字节数）
    private int readBufferSize = 256;

    private AtomicReference<ConnectState> connectState = new AtomicReference(ConnectState.Idle);
    private Socket socket;
    private OutputStream outputStream;
    private WeakReference<Disposable> disposable = null;

    private TcpClient() {
    }

    /**
     * 获取统一的数据处理线程Scheduler
     *
     * @return
     */
    public static Scheduler getSingleScheduler() {
        return singleScheduler;
    }

    public Observable<byte[]> connect() throws IllegalStateException {
        if (connectState.get() != ConnectState.Idle) {
            throw new IllegalStateException("TcpClient has connected or disconnected. Create a new instance if want to connect again.");
        }
        connectState.getAndSet(ConnectState.Connecting);
        ConnectableObservable<byte[]> observable = createTcpConnectObservable().subscribeOn(Schedulers.single())
                .retryWhen(new TcpClient.RetryWithDelay(reconnectCount, reconnectInterval))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        TcpClient.this.disposable = new WeakReference<>(disposable);
                    }
                })
                .publish();
        observable.connect();
        return observable.ofType(byte[].class);
    }

    /**
     * 以Observable的方式对外提供TCP 数据流
     */
    private Observable<byte[]> createTcpConnectObservable() {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<byte[]> emitter) throws Exception {
                InputStream in = null;
                try {
                    socket = new Socket();
                    //TODO: 4000 端口不应该设置读超时，使用心跳机制
                    socket.setSoTimeout(5 * 1000);
                    socket.connect(new InetSocketAddress(ip, port), 5 * 1000);

                    if (isServerClosed(socket)) {
                        throw new ReconnectException();
                    }
                    connectState.getAndSet(ConnectState.Connected);
                    outputStream = socket.getOutputStream();

                    in = socket.getInputStream();
                    //接收数据这不需要新开线程，由observable的subscribeOn来指定线程
                    while (isConnected()) {
                        byte[] buf = new byte[readBufferSize];
                        int readCount = in.read(buf, 0, readBufferSize);
                        if (readCount > 0) {
                            emitter.onNext(Arrays.copyOf(buf, readCount));
                        }
                    }
                } catch (Exception ex) {
                    throw new ReconnectException();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                }
                emitter.onComplete();
            }
        });
    }

    public void sendData(final byte[] data) {
        sendData(data, null);
    }

    public void sendData(final byte[] data, final OnSendListener callback) {
        singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream.write(data);
                    outputStream.flush();
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (IOException e) {
                    Logger.dft().w(LogTag.TCP, "数据发送失败" + e.toString());
                    if (callback != null) {
                        callback.onFile();
                    }
                }
            }
        });
    }

    public boolean isConnected() {
        return connectState.get() == ConnectState.Connected;
    }

    public void disconnect() throws IOException {
        if (disposable == null) {
            throw new IllegalStateException("Not connected to the server!");
        }
        if (connectState.get() == ConnectState.Disconnecting) {
            return;
        }

        if (disposable.get() != null) {
            disposable.get().dispose();
            disposable.clear();
        }

        if (socket != null && socket.isConnected()) {
            outputStream.close();
            socket.close();
            socket = null;
        }

        connectState.getAndSet(ConnectState.Disconnected);
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    private Boolean isServerClosed(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    //region 断线重连
    enum ConnectState {
        Idle, Connecting, Connected, Disconnecting, Disconnected
    }
    //endregion

    public interface OnSendListener {
        void onSuccess();

        void onFile();
    }

    private static class ReconnectException extends Exception {
    }

    public static class Builder {
        private String ip;
        private int port;

        private int reconnectCount = Integer.MAX_VALUE;
        private int reconnectDelayMillis = 200;
        private int readBufferSize = 1024;

        public Builder setTargetIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setTargetPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * @param count 0 不重连，{@link Integer#MAX_VALUE}无限重连
         * @return
         */
        public Builder setReconnectCount(int count) {
            this.reconnectCount = count;
            return this;
        }

        /**
         * 每次重连的间隔时间。
         * 内部默认200ms。
         *
         * @param delayMilliseconds 毫秒
         * @return
         */
        public Builder setReconnectDelay(int delayMilliseconds) {
            this.reconnectDelayMillis = delayMilliseconds;
            return this;
        }

        public Builder setReadBufferSize(int size) {
            this.readBufferSize = size;
            return this;
        }

        public TcpClient create() {
            TcpClient tcpClient = new TcpClient();
            tcpClient.reconnectCount = this.reconnectCount;
            tcpClient.reconnectInterval = this.reconnectDelayMillis;
            tcpClient.ip = this.ip;
            tcpClient.port = this.port;
            tcpClient.readBufferSize = this.readBufferSize;
            return tcpClient;
        }
    }

    /**
     * 出现连接异常后的重连规则
     */
    private class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        public RetryWithDelay(int maxRetries, int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
        }

        @Override
        public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
            return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                    if (throwable instanceof ReconnectException && (maxRetries == Integer.MAX_VALUE || retryCount++ < maxRetries)) {
                      //  Logger.dft().d(LogTag.Utils, "连接异常，将在" + retryDelayMillis + "毫秒后重连！原因：" + throwable.toString());
                        return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                    }
                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                }
            });
        }
    }
}
