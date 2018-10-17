package com.allen.common.network

import com.google.gson.GsonBuilder
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.util.*

/**
 *
 * Created by hHui on 2017/8/3.
 */
object HttpManager {

    const val DefaultUrl = "http://www.google.com"

    private val retrofitCache: Hashtable<String, Retrofit> = Hashtable()

    private val managedDisposables: MutableList<WeakReference<Disposable>> = mutableListOf()

    private fun getRetrofit(url: String): Retrofit {
        if (retrofitCache.containsKey(url)) {
            return retrofitCache.getValue(url)
        }

        val converter = GsonConverterFactory.create(GsonBuilder().setLenient().create())
        val adapter = RxJava2CallAdapterFactory.create()
        val builder = Retrofit.Builder()
        val retrofit = builder.baseUrl(url).addConverterFactory(converter).addCallAdapterFactory(adapter).build()
        retrofitCache[url] = retrofit

        return retrofit
    }

    /**
     * 创建一个Rest Api接口。
     * @param url Http服务器地址
     * @param iCls Http Rest api 接口定义
     * @return Http Rest Api接口实例，可直接调用方法
     */
    fun <T> createService(url: String = DefaultUrl, iCls: Class<T>): T = getRetrofit(url).create(iCls)

    /**
     * 托管所有已创建的http observable
     */
    fun manage(disposable: Disposable) {
        managedDisposables.add(WeakReference(disposable))
    }

    /**
     * 销毁所有Http服务。
     */
    fun dispose() {
        retrofitCache.clear()
        managedDisposables.forEach {
            with(it.get()) {
                if (this != null && !isDisposed) {
                    dispose()
                }
            }
        }
        managedDisposables.clear()
    }

}