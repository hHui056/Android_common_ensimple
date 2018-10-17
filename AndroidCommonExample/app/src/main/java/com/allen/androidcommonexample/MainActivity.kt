package com.allen.androidcommonexample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.allen.androidcommonexample.animator.AnimatorActivity
import com.allen.androidcommonexample.bsdiff.BsdiffActivity
import com.allen.androidcommonexample.bsdiff.utils.ApkUtils
import com.allen.androidcommonexample.opengl.OpenGLActivity
import com.allen.androidcommonexample.rxbus.SecondActivity
import com.allen.androidcommonexample.socket.SocketActivity
import com.allen.androidcommonexample.test.ListWithSpinnerActivity
import com.allen.androidcommonexample.test.bean.TestEventType
import com.allen.androidcommonexample.test.bean.TestEventTypeOne
import com.allen.common.async.RxBus
import com.allen.common.log.Logger
import com.allen.common.utils.ToastUtils
import com.qihoo360.replugin.RePlugin
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * @author hHui
 */
class MainActivity : Activity() {
    var viewHolder: MainItemViewHolder? = null
    val sdCardDir = Environment.getExternalStorageDirectory().absolutePath + File.separator
    val newApkPath = "${sdCardDir}new.apk"
    val activitys = arrayOf("RxBus", "Animator", "Socket", "Plugin", "ListUseSpinner", "OpenGL", "这是新的", "安装新的apk")
    private val TAG = "MainActivity"
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
        list.adapter = MainListAdapter()
    }

    private fun jumpToOtherActivity(activity: Activity) =
            startActivity(Intent(this, activity::class.java))

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logger.dft().d(TAG, "onTrimMemory $level")
    }

    inner class MainListAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = convertView
            if (convertView == null) {
                viewHolder = MainItemViewHolder()
                val inflater = LayoutInflater.from(this@MainActivity)
                convertView = inflater.inflate(R.layout.item_layout, parent, false)
                viewHolder?.button = convertView.findViewById<Button>(R.id.btn_item)
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as MainItemViewHolder
            }
            viewHolder!!.button!!.text = activitys[position] + ""
            viewHolder!!.button!!.setOnClickListener {
                when (position) {
                    0 -> jumpToOtherActivity(SecondActivity())
                    1 -> jumpToOtherActivity(AnimatorActivity())
                    2 -> jumpToOtherActivity(SocketActivity())
                    3 -> RePlugin.startActivity(this@MainActivity, RePlugin.createIntent("com.allen.plugin1", "com.allen.plugin1.MainActivity"))
                    4 -> jumpToOtherActivity(ListWithSpinnerActivity())
                    5 -> jumpToOtherActivity(OpenGLActivity())
                    6 -> {//差分包
                        jumpToOtherActivity(BsdiffActivity())
                    }
                    7 -> {
                        ApkUtils.installApk(applicationContext, newApkPath)
                    }
                    else -> {

                    }
                }
            }
            return convertView!!
        }

        override fun getItem(position: Int): Any {
            return activitys[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return activitys.size
        }
    }

    inner class MainItemViewHolder {
        internal var button: Button? = null
    }
}
