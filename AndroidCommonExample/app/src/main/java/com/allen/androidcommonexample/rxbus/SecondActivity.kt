package com.allen.androidcommonexample.rxbus

import android.app.Activity
import android.os.Bundle
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.test.bean.Player
import com.allen.androidcommonexample.test.bean.TestEventType
import com.allen.androidcommonexample.test.bean.TestEventTypeOne
import com.allen.common.async.RxBus
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : Activity() {

    val TAG = "SecondActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        btn_post_msg.setOnClickListener {
            RxBus.default.post(TestEventType("这是来自 " + TAG + " 的消息"))
        }

        btn_post_msg_one.setOnClickListener {
            RxBus.default.post(TestEventTypeOne(Player("Ray Allen", 40, "Basketball")))
        }
    }
}
