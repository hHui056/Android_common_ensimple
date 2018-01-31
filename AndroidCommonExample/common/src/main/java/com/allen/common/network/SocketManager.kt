package com.allen.common.network

import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by hHui on 2018/1/26.
 */
object SocketManager {
    private val listeners: MutableMap<Any, (String) -> Unit> = Hashtable()

    private lateinit var tcpClient: TcpClient


    private fun notifyMessage(data: String) = listeners.values.forEach {
        it(data)
    }

    fun start(server: Server) {
        TcpClient.Builder().setTargetIp(server.ip).setTargetPort(server.port).setReconnectCount(Int.MAX_VALUE).setReconnectDelay(200).create()
        tcpClient.connect().observeOn(AndroidSchedulers.mainThread()).subscribe({
            //在Android主线程处理收到的数据
            notifyMessage(String(it))
        }, {

        })
    }

    fun send(data: String) = tcpClient.sendData(data.toByteArray())


    fun addMessageListener(token: Any, listener: (String) -> Unit) {
        listeners.put(token, listener)
    }

    fun stop() {
        tcpClient.disconnect()
    }
}