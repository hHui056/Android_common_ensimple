package com.allen.androidcommonexample.opengl

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle

/**
 *  OpenGL ES 2.0
 */
class OpenGLActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView

    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGLSurfaceView()
    }

    private fun initGLSurfaceView() {
        //glSurfaceView = GLSurfaceView(this@OpenGLActivity)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val supportEs2 = activityManager.deviceConfigurationInfo.reqGlEsVersion >= 0x20000  //是否支持es2.0

        if (supportEs2) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(AirHockeyRendererPlus(this@OpenGLActivity))
            rendererSet = true
        }
        this.setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) glSurfaceView.onResume()
    }

}
