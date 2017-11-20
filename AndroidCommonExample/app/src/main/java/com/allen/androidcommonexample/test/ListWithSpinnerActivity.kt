package com.allen.androidcommonexample.test

import android.app.Activity
import android.os.Bundle

import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.rxbus.TestBean
import kotlinx.android.synthetic.main.activity_list_with_spinner.*

class ListWithSpinnerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_with_spinner)
        init()
    }

    fun init() {
        val list = ArrayList<TestBean>()
        for (i in 0..20) {
            list.add(TestBean(i.toString(), arrayOf("aaa", "bbb", "ccc")))
        }

        val ada = SpinnerListAdapter(this@ListWithSpinnerActivity, list)
        my_list.adapter = ada
    }
}
