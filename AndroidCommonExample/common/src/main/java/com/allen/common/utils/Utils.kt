package com.allen.common.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.annotation.NonNull
import java.lang.ref.WeakReference
import java.util.*


/**
 * Created by hHui on 2017/9/11.
 *
 * Utils 初始化相关
 */
object Utils {
    private lateinit var sApplication: Application

    @JvmStatic var sTopActivityWeakRef: WeakReference<Activity>? = null
    @JvmStatic var sActivityList: MutableList<Activity> = LinkedList()

    private val mCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle) {
            sActivityList.add(activity)
            setTopActivityWeakRef(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            setTopActivityWeakRef(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            setTopActivityWeakRef(activity)
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            sActivityList.remove(activity)
        }
    }


    /**
     * 初始化工具类

     * @param app 应用
     */
    @JvmStatic fun init(@NonNull app: Application) {
        Utils.sApplication = app
        app.registerActivityLifecycleCallbacks(mCallbacks)
    }

    /**
     * 获取Application

     * @return Application
     */
    @JvmStatic fun getApp(): Application {
        if (sApplication != null) return sApplication as Application
        throw NullPointerException("u should init first")
    }

    private fun setTopActivityWeakRef(activity: Activity) {
        if (sTopActivityWeakRef == null || activity != sTopActivityWeakRef!!.get()) {
            sTopActivityWeakRef = WeakReference(activity)
        }
    }
}