package com.allen.common.log

/**
 * Created by hHui on 2017/8/8.
 */
internal class CatLog : ILog {
    override fun dispose() {
        //do nothing
    }

    override fun d(tag: String, msg: String, t: Throwable?) {
        android.util.Log.d(tag, msg, t)
    }

    override fun i(tag: String, msg: String, t: Throwable?) {
        android.util.Log.i(tag, msg, t)
    }

    override fun w(tag: String, msg: String, t: Throwable?) {
        android.util.Log.w(tag, msg, t)
    }

    override fun e(tag: String, msg: String, t: Throwable?) {
        android.util.Log.e(tag, msg, t)
    }

    override fun v(tag: String, msg: String, t: Throwable?) {
        android.util.Log.v(tag, msg, t)
    }

}