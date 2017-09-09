package com.allen.common.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * retrofit get 接口。
 *
 * Created by hHui on 2017/8/3.
 */
internal interface IGetMethod {

    @GET fun getResponse(@Url url: String): Observable<ResponseBody>

    @Streaming @GET fun getFile(@Url url: String): Observable<ResponseBody>

}