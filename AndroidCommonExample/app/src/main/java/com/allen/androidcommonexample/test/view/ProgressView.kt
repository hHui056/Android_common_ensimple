package com.allen.androidcommonexample.test.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *
 * 用来测试[ObjectAnimator] 的View
 *
 * Created by hHui on 2017/9/19.
 */

class ProgressView : View {

    var progress = 0f
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        // paint.color = Color.RED
        // canvas.drawCircle(300f, 300f, progress, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(200F, progress, 20F, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)


    }
}
