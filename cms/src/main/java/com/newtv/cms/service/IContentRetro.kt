package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api.v31
 * 创建事件:         16:32
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */
internal interface IContentRetro {
    @GET("api/v31/{appkey}/{channelCode}/content/{left}/{right}/{contentID}.json")
    fun getInfo(@Path("appkey") appkey: String,
                         @Path("channelCode") channelId: String,
                         @Path("left") left: String,
                         @Path("right") right: String,
                         @Path("contentID") contentUUID: String): Observable<ResponseBody>


    @GET("api/v31/{appkey}/{channelCode}/subcontents/{left}/{right}/{contentID}.json")
    fun getSubInfo(@Path("appkey") appkey: String,
                @Path("channelCode") channelId: String,
                @Path("left") left: String,
                @Path("right") right: String,
                @Path("contentID") contentUUID: String): Observable<ResponseBody>
}