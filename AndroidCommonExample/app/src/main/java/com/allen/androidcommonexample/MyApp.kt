package com.allen.androidcommonexample

import android.app.Application
import com.allen.common.log.Logger
import com.allen.common.utils.SPUtils
import com.allen.common.utils.ScreenUtils
import com.allen.common.utils.Utils

/**
 * Created by hHui on 2017/9/9.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        /**
         * 初始化Log 模块
         */
        Logger.init(Logger.Type.Logcat, Logger.VERBOSE, getExternalFilesDir(null))
        /**
         * 工具模块
         */
        Utils.init(this)
        ScreenUtils.isTablet
        val util =SPUtils.instance
    }
}