package com.allen.androidcommonexample.test.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.bean.TestSignal
import com.allen.androidcommonexample.test.util.SignalInsert
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by hHui on 2017/9/29.
 */
class DegreeView : TextureView {

    val TAG = "DegreeView"

    lateinit var canvas: Canvas

    var context1: Context

    lateinit var mDrawingSurface: Surface

    var isThreadExit = false

    val mSignalQueue = LinkedBlockingQueue<TestSignal>()

    var lastSignal = TestSignal(0.0, 0.0, 0.0)

    var lastReceiveSignalTime: Long = 0

    var startDrawTime: Long = 0

    var endDrawTime: Long = 0

    val drawingRunnable: DrawingRunnable by lazy {

        DrawingRunnable()
    }

    lateinit var degressBackPaint: Paint
    lateinit var textPaint: Paint
    lateinit var signalPaint: Paint

    val mSurfaceRect: Rect by lazy {
        Rect(0, 0, 180, 180)
    }

    constructor(context: Context) : super(context) {
        this.context1 = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context1 = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.context1 = context
        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        degressBackPaint = Paint()
        degressBackPaint.isAntiAlias = true
        degressBackPaint.color = resources.getColor(R.color.degressBackground)

        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 20F

        signalPaint = Paint()
        signalPaint.color = resources.getColor(R.color.signalColor)
        signalPaint.isAntiAlias = true

        this.surfaceTextureListener = MySurfaceTextureListener()
    }


    private inner class MySurfaceTextureListener : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            mDrawingSurface = Surface(surface)
            isThreadExit = false
            // - 开始绘图
            Thread(drawingRunnable).start()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            isThreadExit = true
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }
    }

    /**
     * 绘图线程
     */
    inner class DrawingRunnable : Runnable {
        override fun run() {
            while (!isThreadExit) {
                try {
                    val drawTime = endDrawTime - startDrawTime
                    if (drawTime < 1000 / 30 && drawTime.toInt() != 0) {
                        Thread.sleep(1000 / 30 - drawTime)
                    }
                    val signal = mSignalQueue.take()
                    handlerSignal(signal)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 处理信号，画线
     */
    private fun handlerSignal(signal: TestSignal) {
        startDrawTime = System.currentTimeMillis()
        try {
            canvas = mDrawingSurface.lockCanvas(mSurfaceRect)
            canvas.drawColor(Color.BLACK)
            drawBackground()
            drawSingal(signal)

            mDrawingSurface.unlockCanvasAndPost(canvas)  // - 解锁 Canvas，并渲染当前的图像
            endDrawTime = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 刷新信号值
     */
    fun refreshSignal(signal: TestSignal) {
        val leadTime = System.currentTimeMillis() - lastReceiveSignalTime
        lastReceiveSignalTime = System.currentTimeMillis()

        val signals = SignalInsert.insertData(lastSignal, signal, leadTime)

        for (item in signals) {
            mSignalQueue.add(item)
        }
        lastSignal = signal
    }

    private fun drawBackground() {
        canvas.drawRect(20f, 40f, 40f, 140f, degressBackPaint)
        canvas.drawRect(80f, 40f, 100f, 140f, degressBackPaint)
        canvas.drawRect(140f, 40f, 160f, 140f, degressBackPaint)

        canvas.drawText("离 合", 10f, 170f, textPaint)
        canvas.drawText("刹 车", 70f, 170f, textPaint)
        canvas.drawText("油 门", 130f, 170f, textPaint)
    }

    private fun drawSingal(signal: TestSignal) {
        canvas.drawRect(20f, (140 - signal.lihe * 100).toFloat(), 40f, 140f, signalPaint) // 离合信号变化
        canvas.drawRect(80f, (140 - signal.shache * 100).toFloat(), 100f, 140f, signalPaint) // 刹车信号变化
        canvas.drawRect(140f, (140 - signal.youmen * 100).toFloat(), 160f, 140f, signalPaint) // 油门信号变化

    }
}