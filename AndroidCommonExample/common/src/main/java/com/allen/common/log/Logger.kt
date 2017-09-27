package com.allen.common.log

import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by hHui on 2017/8/8.
 */
class Logger private constructor() {

    enum class Type {
        Logcat, File
    }

    private lateinit var printer: ILog

    private var level: Int = VERBOSE

    constructor(type: Type, level: Int, file: File) : this() {
        when (type) {
            Type.Logcat -> printer = CatLog()
            Type.File -> printer = FileLog(file)
        }
        this.level = level
    }

    companion object {
        const val VERBOSE: Int = 0
        const val DEBUG: Int = 1
        const val INFO: Int = 2
        const val WARN: Int = 3
        const val ERROR: Int = 4
        private var logger: AtomicReference<Logger> = AtomicReference()
        /**
         * 初始化
         */
        @JvmStatic fun init(type: Type, level: Int, file: File): Unit {
            if (this.logger.get() != null) {
                throw  Exception("Logger has been initialized!")
            }
            val logger = Logger(type, level, file)
            this.logger = AtomicReference(logger)
        }

        @JvmStatic fun dft(): Logger {
            if (logger.get() == null) {
                throw  Exception("Logger has not initialized, please invoke init method on application created.")
            } else {
                return logger.get()
            }
        }

        @JvmStatic fun of(type: Type, level: Int, file: File): Logger {
            return Logger(type, level, file)
        }
    }

    @JvmOverloads fun v(tag: String, msg: String, t: Throwable? = null) {
        if (level <= VERBOSE) printer.v(tag, msg, t)
    }

    @JvmOverloads fun d(tag: String, msg: String, t: Throwable? = null) {
        if (level <= DEBUG) printer.d(tag, msg, t)
    }

    @JvmOverloads fun i(tag: String, msg: String, t: Throwable? = null) {
        if (level <= INFO) printer.i(tag, msg, t)
    }

    @JvmOverloads fun w(tag: String, msg: String, t: Throwable? = null) {
        if (level <= WARN) printer.w(tag, msg, t)
    }

    @JvmOverloads fun e(tag: String, msg: String, t: Throwable? = null) {
        if (level <= ERROR) printer.e(tag, msg, t)
    }

    fun dispose() {
        printer.dispose()
    }

}