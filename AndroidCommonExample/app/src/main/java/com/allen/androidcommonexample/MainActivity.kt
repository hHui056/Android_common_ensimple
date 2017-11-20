package com.allen.androidcommonexample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import com.allen.androidcommonexample.animator.AnimatorActivity
import com.allen.androidcommonexample.test.ListWithSpinnerActivity
import com.allen.androidcommonexample.rxbus.SecondActivity
import com.allen.androidcommonexample.socket.SocketActivity
import com.allen.androidcommonexample.bean.TestEventType
import com.allen.androidcommonexample.bean.TestEventTypeOne
import com.allen.androidcommonexample.opengl.OpenGLActivity
import com.allen.common.async.RxBus
import com.allen.common.log.Logger
import com.allen.common.utils.ToastUtils
import com.qihoo360.replugin.RePlugin
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author hHui
 */
class MainActivity : Activity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v) {
            btn_to_second -> jumpToOtherActivity(SecondActivity())
            btn_to_animator -> jumpToOtherActivity(AnimatorActivity())
            btn_to_socket -> jumpToOtherActivity(SocketActivity())
            btn_install_plugin -> {  // - 安装插件
                val filePath = Environment.getExternalStorageDirectory().absolutePath
                Logger.dft().d(TAG, "根目录路径:  " + filePath)
                val info = RePlugin.install(filePath + "/hahaha.apk")
                Logger.dft().d(TAG, "安装结果： " + info)
            }
            btn_plugin -> {
                RePlugin.startActivity(this, RePlugin.createIntent("com.allen.plugin1", "com.allen.plugin1.MainActivity"))
            }
            btn_to_sp -> {
                jumpToOtherActivity(ListWithSpinnerActivity())
            }
            btn_opengl -> jumpToOtherActivity(OpenGLActivity())
        }
    }

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RxBus.default.register(this, RxBus.EventType::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it is TestEventType) {
                Logger.dft().d(TAG, it.data)
                ToastUtils.setMessageColor(Color.RED)
                ToastUtils.setGravity(Gravity.CENTER, 0, 0)
                ToastUtils.showCustomShort(R.layout.test_layout)
            } else if (it is TestEventTypeOne) {
                Logger.dft().d(TAG, "He's name is " + it.player.name + "\nAge is " + it.player.age + "\nProfession " + it.player.like)
            }
        }

        btn_to_second.setOnClickListener(this)
        btn_to_animator.setOnClickListener(this)
        btn_to_socket.setOnClickListener(this)
        btn_plugin.setOnClickListener(this)
        btn_install_plugin.setOnClickListener(this)
        btn_to_sp.setOnClickListener(this)
        btn_opengl.setOnClickListener(this)
    }

    private fun jumpToOtherActivity(activity: Activity) {
        startActivity(Intent(this, activity::class.java))
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logger.dft().d(TAG, "onTrimMemory " + level)
    }
}
