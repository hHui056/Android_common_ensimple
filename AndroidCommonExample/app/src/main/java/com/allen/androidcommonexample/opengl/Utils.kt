package com.allen.androidcommonexample.opengl

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by hHui on 2017/11/17.
 */
object Utils {
    @JvmStatic
    fun readTextFileFromResource(context: Context, id: Int): String {
        val inputStream = context.resources.openRawResource(id)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferReader = BufferedReader(inputStreamReader)
        return bufferReader.readText()
    }
}