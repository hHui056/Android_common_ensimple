package com.allen.common.log

import com.allen.common.async.RxBus
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * 如果文件不存在，新建文件，如果文件存在，追加。
 *
 * Created by hHui on 2017/8/8.
 */
class AsyncFileWriter(private val file: File) {
    private var rxBus: RxBus

    init {
        if (!file.exists()) {
            val fileParent = File(file.parent)
            if (!fileParent.exists()) {//如果目录结构不存在先创建目录结构
                fileParent.mkdirs()
            }
            file.createNewFile()
        }

        rxBus = RxBus.newInstance
        rxBus.register(this, Data::class.java).observeOn(Schedulers.newThread()).subscribe({
            when (it.type) {
                Type.Append -> file.appendText(it.content)
                Type.AppendLine -> file.appendText("${it.content}\n")
            }
        }, {
            Logger.dft().e(LogTag.Utils, it.toString())
        })
    }

    fun append(content: String) {
        rxBus.post(Data(content, Type.Append))
    }

    fun appendLine(content: String) {
        rxBus.post(Data(content, Type.AppendLine))
    }

    fun dispose() {
        rxBus.unregister(this, Data::class.java)
    }

    private data class Data(val content: String, val type: Type) : RxBus.EventType()

    private enum class Type {
        Append, AppendLine
    }
}