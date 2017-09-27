package com.allen.androidcommonexample

import com.allen.common.log.Logger
import com.allen.common.utils.Utils
import com.qihoo360.replugin.RePluginApplication

/**
 * Created by hHui on 2017/9/9.
 */
class MyApp : RePluginApplication() {

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

    }
}