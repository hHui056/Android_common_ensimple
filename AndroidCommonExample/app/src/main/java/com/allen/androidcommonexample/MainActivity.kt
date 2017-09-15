package com.allen.androidcommonexample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import com.allen.androidcommonexample.activity.SecondActivity
import com.allen.androidcommonexample.bean.TestEventType
import com.allen.androidcommonexample.bean.TestEventTypeOne
import com.allen.common.async.RxBus
import com.allen.common.log.Logger
import com.allen.common.utils.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author hHui
 */
class MainActivity : Activity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxBus.default.register(this, RxBus.EventType::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it is TestEventType) {
                Logger.dft().d(TAG, it.data)
                ToastUtils.setMsgColor(Color.RED)
                ToastUtils.setGravity(Gravity.CENTER, 0, 0)
                ToastUtils.showCustomShort(R.layout.test_layout)
            } else if (it is TestEventTypeOne) {
                Logger.dft().d(TAG, "He's name is " + it.player.name + "\nAge is " + it.player.age + "\nLike " + it.player.like)
            }
        }

        btn_to_second.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}
