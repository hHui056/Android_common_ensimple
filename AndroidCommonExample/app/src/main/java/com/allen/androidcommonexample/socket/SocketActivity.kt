package com.allen.androidcommonexample.socket

import android.app.Activity
import android.os.Bundle
import com.allen.androidcommonexample.R
import com.allen.common.log.Logger
import com.allen.common.network.TcpClient
import com.allen.common.utils.ToastUtils
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_socket.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SocketActivity : Activity() {
    val TAG = "SocketActivity"

    val IP = "192.168.150.100"

    val PORT = 4001

    private val singleExecutor: ExecutorService = Executors.newSingleThreadExecutor() // - 单线程执行

    private val singleScheduler: Scheduler = Schedulers.from(singleExecutor)

    private val tcpClient: TcpClient by lazy {
        TcpClient.Builder().setTargetIp(IP).setTargetPort(PORT).setReconnectCount(Int.MAX_VALUE).setReconnectDelay(200).create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)

        btn_connect.setOnClickListener {
            try {
                tcpClient.connect().observeOn(singleScheduler).subscribe {
                    Logger.dft().d(TAG, String(it))
                }
            } catch (e: Exception) {
                Logger.dft().d(TAG, e.message!!)
            }
        }

        btn_diconnect.setOnClickListener {
            try {
                tcpClient.disconnect()
            } catch (e: Exception) {
                ToastUtils.showShortText(e.message!!)
            }
        }
    }
}
