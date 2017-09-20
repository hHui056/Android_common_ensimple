package com.allen.common.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Surface
import android.view.Window
import android.view.WindowManager

/**
 * Create By hHui on 2017/09/15.
 *
 * 屏幕相关工具类
 */
class ScreenUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 获取屏幕的宽度（单位：px）

         * @return 屏幕宽
         */
        val screenWidth: Int
            get() = Utils.getApp().resources.displayMetrics.widthPixels
        /**
         * 获取屏幕的高度（单位：px）

         * @return 屏幕高
         */
        val screenHeight: Int
            get() = Utils.getApp().resources.displayMetrics.heightPixels

        /**
         * 设置屏幕为全屏
         *
         * 需在 `setContentView` 之前调用

         * @param activity activity
         */
        fun setFullScreen(activity: Activity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * 设置屏幕为横屏
         *
         * 还有一种就是在Activity中加属性android:screenOrientation="landscape"
         *
         * 不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次
         *
         * 设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次
         *
         * 设置Activity的android:configChanges="orientation|keyboardHidden|screenSize"（4.0以上必须带最后一个参数）时
         * 切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方法

         * @param activity activity
         */
        fun setLandscape(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        /**
         * 设置屏幕为竖屏

         * @param activity activity
         */
        fun setPortrait(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        /**
         * 判断是否横屏

         * @return `true`: 是<br></br>`false`: 否
         */
        val isLandscape: Boolean
            get() = Utils.getApp().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        /**
         * 判断是否竖屏

         * @return `true`: 是<br></br>`false`: 否
         */
        val isPortrait: Boolean
            get() = Utils.getApp().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        /**
         * 获取屏幕旋转角度

         * @param activity activity
         * *
         * @return 屏幕旋转角度
         */
        fun getScreenRotation(activity: Activity): Int {
            when (activity.windowManager.defaultDisplay.rotation) {

                Surface.ROTATION_0 -> return 0

                Surface.ROTATION_90 -> return 90

                Surface.ROTATION_180 -> return 180

                Surface.ROTATION_270 -> return 270

                else -> return 0
            }
        }

        /**
         * 截屏

         * @param activity activity
         * @param isDeleteStatusBar 是否删除状态栏
         * *
         * @return Bitmap
         */
        @JvmOverloads fun screenShot(activity: Activity, isDeleteStatusBar: Boolean = false): Bitmap {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.buildDrawingCache()
            val bmp = decorView.drawingCache
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret: Bitmap
            if (isDeleteStatusBar) {
                val resources = activity.resources
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                val statusBarHeight = resources.getDimensionPixelSize(resourceId)
                ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight)
            } else {
                ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            }
            decorView.destroyDrawingCache()
            return ret
        }

        /**
         * 判断是否锁屏

         * @return `true`: 是<br></br>`false`: 否
         */
        val isScreenLock: Boolean
            get() {
                val km = Utils.getApp().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                return km.inKeyguardRestrictedInputMode()
            }
        /**
         * 获取进入休眠时长

         * @return 进入休眠时长，报错返回-123
         */
        /**
         * 设置进入休眠时长
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`

         * @param duration 时长
         */
        var sleepDuration: Int
            get() {
                try {
                    return Settings.System.getInt(Utils.getApp().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                    return -123
                }

            }
            set(duration) {
                Settings.System.putInt(Utils.getApp().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, duration)
            }

        /**
         * 判断是否是平板

         * @return `true`: 是<br></br>`false`: 否
         */
        val isTablet: Boolean
            get() = Utils.getApp().resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}

