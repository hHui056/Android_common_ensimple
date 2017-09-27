package com.allen.androidcommonexample.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.view.animation.BounceInterpolator
import com.allen.androidcommonexample.R
import com.allen.common.log.Logger
import com.allen.common.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_animator.*

class AnimatorActivity : Activity() {
    val TAG = "AnimatorActivity"

    val animator: ViewPropertyAnimator by lazy {
        img_icon.animate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtils.setFullScreen(this)  //设置全屏
        setContentView(R.layout.activity_animator)

        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                // - 动画重复播放
                Logger.dft().d(TAG, "动画重复播放")
            }

            override fun onAnimationEnd(animation: Animator?) {
                // - 动画结束
                Logger.dft().d(TAG, "动画结束")
            }

            override fun onAnimationCancel(animation: Animator?) {
                // - 动画取消
                Logger.dft().d(TAG, "动画取消")

            }

            override fun onAnimationStart(animation: Animator?) {
                // - 动画开始
                Logger.dft().d(TAG, "动画开始")
            }

        })

        //  animator1()
        animator2()
        // animator3()
        animator4()
    }

    // - 平移
    fun animator1() {

        animator.duration = 10000   // - 持续时间
        animator.translationXBy(400F)
        animator.translationYBy(400F)
    }

    // - 旋转
    fun animator2() {
        animator.duration = 10000
        // animator.rotationBy(90F)  // - 参数为°()
        animator.rotationXBy(180F)
        // animator.rotationYBy(90F)
    }

    // - 缩放
    fun animator3() {
        animator.duration = 10000
        animator.scaleXBy(2F)
        animator.scaleYBy(2F)
    }

    /**
     * [ObjectAnimator] 简单使用
     */
    fun animator4() {
        val objAnimator: ObjectAnimator = ObjectAnimator.ofFloat(my_progress, "progress", 0F, ScreenUtils.screenHeight.toFloat() - 30)
        objAnimator.duration = 15000
        objAnimator.interpolator = BounceInterpolator()  // - 设置速度模型
        objAnimator.start()
    }


}
