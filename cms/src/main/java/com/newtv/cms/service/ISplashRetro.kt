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
 * 创建事件:         16:04
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal interface ISplashRetro {
    @Headers("host_type: " + HeadersInterceptor.NEW_CMS)
    @GET("api/v31/{appkey}/{channelCode}/logo/startuplogo.json")
    fun getList(@Path("appkey") appKey: String,
                    @Path("channelCode") channelid: String): Observable<ResponseBody>
}