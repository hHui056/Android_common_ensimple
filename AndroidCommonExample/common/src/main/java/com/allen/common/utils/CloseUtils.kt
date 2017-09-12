package com.allen.common.utils

import java.io.Closeable
import java.io.IOException

/**
 * Created by hHui on 2017/9/11.
 */
object CloseUtils {
    /**
     * 关闭IO

     * @param closeables closeables
     */
    @JvmStatic fun closeIO(vararg closeables: Closeable) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 安静关闭IO

     * @param closeables closeables
     */
    @JvmStatic fun closeIOQuietly(vararg closeables: Closeable) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable!!.close()
                } catch (ignored: IOException) {
                }

            }
        }
    }
}