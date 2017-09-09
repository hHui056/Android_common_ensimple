package com.allen.common.log

/**
 * Created by hHui on 2017/8/8.
 */
internal interface ILog {
    fun d(tag: String, msg: String, t: Throwable? = null)
    fun i(tag: String, msg: String, t: Throwable? = null)
    fun w(tag: String, msg: String, t: Throwable? = null)
    fun e(tag: String, msg: String, t: Throwable? = null)
    fun v(tag: String, msg: String, t: Throwable? = null)
    fun dispose()
}