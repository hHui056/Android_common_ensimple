package com.allen.androidcommonexample.test.util

import com.allen.androidcommonexample.test.bean.TestSignal

/**
 * Created by hHui on 2017/9/29.
 */
object SignalInsert {

    val FRAMERATE = 30
    /**
     * @param lastSignal 上一个信号
     * @param nowSignal  此时的信号
     * @param leadTime  间隔时间
     */
    @JvmStatic fun insertData(lastSignal: TestSignal, nowSignal: TestSignal, leadTime: Long): List<TestSignal> {
        val list = ArrayList<TestSignal>()
        if (leadTime > 1000) {
            list.add(nowSignal)
            return list
        }
        val needInsertSignalNum = (FRAMERATE * leadTime / 1000).toInt()
        val changeShaChe = (nowSignal.shache - lastSignal.shache) / needInsertSignalNum
        val changeYouMen = (nowSignal.youmen - lastSignal.youmen) / needInsertSignalNum
        val changeLiHe = (nowSignal.lihe - lastSignal.lihe) / needInsertSignalNum
        for (i in 1..needInsertSignalNum) {
            list.add(TestSignal(lastSignal.shache + changeShaChe * i, lastSignal.youmen + changeYouMen * i, lastSignal.lihe + changeLiHe))
        }
        return list

    }
}