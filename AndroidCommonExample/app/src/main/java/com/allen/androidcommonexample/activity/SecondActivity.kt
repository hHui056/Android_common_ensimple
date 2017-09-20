package com.allen.androidcommonexample.activity

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.view.ViewPropertyAnimator
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.bean.Player
import com.allen.androidcommonexample.bean.TestEventType
import com.allen.androidcommonexample.bean.TestEventTypeOne
import com.allen.common.async.RxBus
import com.allen.common.log.Logger
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
        val viewPropertyAnimator: ViewPropertyAnimator = img_test.animate()
        img_test.translationX = 100F
        viewPropertyAnimator.duration = 10000
        viewPropertyAnimator.translationX(200F)  // - 基于当前位置的X 变化到200
        //viewPropertyAnimator.translationXBy(200F)   // - 基于当前位置 向右平移200个像素


        viewPropertyAnimator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                Logger.dft().d(TAG, "animation end")
                Logger.dft().d(TAG, "x is " + img_test.translationX)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

                Logger.dft().d(TAG, "animation start")
                Logger.dft().d(TAG, "x is " + img_test.translationX)
            }
        })

    }
}
