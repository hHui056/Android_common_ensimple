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

    var sTopActivityWeakRef: WeakReference<Activity>? = null

    var sActivityList: MutableList<Activity> = LinkedList()

    val mCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) = Unit

        override fun onActivityResumed(activity: Activity?) = setTopActivityWeakRef(activity!!)

        override fun onActivityStarted(activity: Activity?) = setTopActivityWeakRef(activity!!)

        override fun onActivityDestroyed(activity: Activity?) {
            sActivityList.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit

        override fun onActivityStopped(activity: Activity?) = Unit

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            sActivityList.add(activity!!)
            setTopActivityWeakRef(activity!!)
        }

    }

    /**
     * 初始化工具类

     * @param app 应用
     */
    @JvmStatic
    fun init(@NonNull app: Application) {
        Utils.sApplication = app
        app.registerActivityLifecycleCallbacks(mCallbacks)
    }


    /**
     * 获取Application

     * @return Application
     */
    @JvmStatic
    fun getApp(): Application {
        if (sApplication != null) return sApplication
        throw NullPointerException("u should init first")
    }

    private fun setTopActivityWeakRef(activity: Activity) {
        if (sTopActivityWeakRef == null || activity != sTopActivityWeakRef!!.get()) {
            sTopActivityWeakRef = WeakReference(activity)
        }
    }
}