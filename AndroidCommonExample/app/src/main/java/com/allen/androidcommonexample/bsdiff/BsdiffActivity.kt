package com.allen.androidcommonexample.bsdiff

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.allen.androidcommonexample.R
import kotlinx.android.synthetic.main.activity_bsdiff.*
import android.widget.Toast
import com.allen.androidcommonexample.bsdiff.utils.ApkUtils
import com.allen.androidcommonexample.bsdiff.utils.PatchUtils
import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException


class BsdiffActivity : Activity() {
    // 成功
    private val WHAT_SUCCESS = 0
    // 合成失败
    private val WHAT_FAIL_PATCH = 1

    val sdCardDir = Environment.getExternalStorageDirectory().absolutePath + File.separator

    val newApkPath = "${sdCardDir}new.apk"

    val patchPath = "${sdCardDir}update.patch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsdiff)

        patch.setOnClickListener {
            Toast.makeText(this@BsdiffActivity, "开始合成差分包", Toast.LENGTH_SHORT).show()
            //开始合成差分包
            PatchTask().execute()
        }
    }

    internal inner class PatchTask : AsyncTask<String, Void, Int>() {

        override fun doInBackground(vararg strings: String): Int? {
            val oldApkSource = ApkUtils.getSourceApkPath(this@BsdiffActivity, "com.allen.androidcommonexample")
            return PatchUtils.getInstance().patch(oldApkSource, newApkPath, patchPath)
        }

        override fun onPostExecute(integer: Int?) {
            super.onPostExecute(integer)
            when (integer) {
                WHAT_SUCCESS -> {
                    val text = "新apk已合成成功"
                    runOnUiThread {
                        Toast.makeText(this@BsdiffActivity, text, Toast.LENGTH_SHORT).show()
                    }
                    System.out.println("新apk已合成成功")
                    // ApkUtils.installApk(this@MainActivity, NEW_APK_PATH)
                }
                WHAT_FAIL_PATCH -> {
                    val text = "新apk已合成失败！"
                    System.out.println("新apk已合成失败")
                    runOnUiThread {
                        Toast.makeText(this@BsdiffActivity, text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
