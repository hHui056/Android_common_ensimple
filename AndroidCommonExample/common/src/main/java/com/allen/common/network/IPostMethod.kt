package com.allen.common.network

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * retrofit post 接口。
 *
 * Created by hHui on 2017/8/3.
 */
internal interface IPostMethod {

    @POST
    fun postJson(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @FormUrlEncoded
    @POST
    fun postForm(@Url url: String, @FieldMap() fileds: MutableMap<String, String>): Observable<ResponseBody>

    @Multipart
    @POST
    fun postMultiPart(@Url url: String, @PartMap() params: MutableMap<String, RequestBody>,
                      @Part files: List<MultipartBody.Part>): Observable<ResponseBody>

}