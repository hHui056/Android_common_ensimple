package com.allen.androidcommonexample.socket

import android.app.Activity
import android.os.Bundle
import com.allen.androidcommonexample.R
import com.allen.common.log.Logger
import com.allen.common.network.TcpClient
import com.allen.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_socket.*

class SocketActivity : Activity() {

    private val TAG = "SocketActivity"

    private val IP = "192.168.150.101"

    private val PORT = 4001

    private val tcpClient: TcpClient by lazy {
        TcpClient.Builder().setTargetIp(IP).setTargetPort(PORT).setReconnectCount(Int.MAX_VALUE).setReconnectDelay(200).create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        btn_connect.setOnClickListener {
            try {
                tcpClient.connect().observeOn(TcpClient.getSingleScheduler()).subscribe {
                    Logger.dft().v(TAG, String(it))
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

        btn_send.setOnClickListener {
            val content = edit_content.text.toString().toByteArray()
            tcpClient.sendData(content)
        }
    }
}
