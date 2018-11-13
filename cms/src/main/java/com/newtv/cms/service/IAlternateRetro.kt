@file:Suppress("SpellCheckingInspection")

package com.newtv.cms.service

import com.newtv.libs.HeadersInterceptor
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         17:50
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
interface IAlternateRetro {
    @Headers("host_type: " + HeadersInterceptor.NEW_CMS)
    @GET("api/v31/{appkey}/{channelCode}/alternatelist/{contentID}.json")
    fun getInfo(@Path("appkey") appkey: String,
                @Path("channelCode") channelId: String,
                @Path("contentID") contentUUID: String): Observable<ResponseBody>
}