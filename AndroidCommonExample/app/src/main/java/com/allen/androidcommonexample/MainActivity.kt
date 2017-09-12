package com.allen.androidcommonexample

import android.app.Activity
import android.os.Bundle
import com.allen.androidcommonexample.bean.TestEventType
import com.allen.common.async.RxBus
import com.allen.common.log.Logger
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author hHui
 */
class MainActivity : Activity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_rxbus.setOnClickListener {
            RxBus.default.post(TestEventType("HelloWrold"))
        }

        RxBus.default.register(this, TestEventType::class.java).subscribe {
            Logger.dft().d(TAG, it.data)
        }
    }

}
