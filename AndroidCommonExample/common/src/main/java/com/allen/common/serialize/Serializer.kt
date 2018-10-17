package com.allen.common.serialize

import com.google.gson.Gson
import okio.Okio
import java.io.File

/**
 * 序列化/反序列化xml和json
 *
 * Created by hHui on 2017/8/1.
 */
class Serializer {

    companion object {
        //region Java api
        @JvmStatic
        fun <T> fromJsonFile(jsonFilePath: String, type: Class<T>): T {
            val file = File(jsonFilePath)
            val jsonString: String = Okio.buffer(Okio.source(file)).readString(Charsets.UTF_8)
            return fromJsonString(jsonString, type)
        }

        @JvmStatic
        fun <T> fromJsonString(jsonStr: String, type: Class<T>): T {
            return Gson().fromJson(jsonStr, type)
        }

        @JvmStatic
        fun <T> fromXmlFile(xmlFilePath: String, type: Class<T>): T {
            val file = File(xmlFilePath)
            val xmlString: String = Okio.buffer(Okio.source(file)).readString(Charsets.UTF_8)
            return fromXmlString(xmlString, type)
        }

        @JvmStatic
        fun <T> fromXmlString(xmlString: String, type: Class<T>): T {
            val jsonString = Xml2Json.convert(xmlString)
            return fromJsonString(jsonString, type)
        }
        //endregion

        //region Kotlin api

        @JvmStatic
        fun <T> toJsonFile(obj: T, targetFilePath: String) {
            File(targetFilePath).writeText(toJsonString(obj))
        }

        @JvmStatic
        fun <T> toJsonString(obj: T): String {
            return Gson().toJson(obj)
        }

        inline fun <reified T> fromJsonFile(jsonFilePath: String): T {
            return fromJsonString(File(jsonFilePath).readText())
        }

        inline fun <reified T> fromJsonString(jsonStr: String): T {
            return Gson().fromJson(jsonStr, T::class.java)
        }

        inline fun <reified T> fromXmlFile(xmlFilePath: String): T {
            return fromXmlString(File(xmlFilePath).readText(Charsets.UTF_8))
        }

        inline fun <reified T> fromXmlString(xmlString: String): T {
            val jsonString = Xml2Json.convert(xmlString)
            return fromJsonString(jsonString)
        }
    }
}

fun <T> T.toJsonString(): String {
    return Gson().toJson(this)
}

fun <T> String.toModel(type: Class<T>): T {
    return Gson().fromJson(this, type)
}