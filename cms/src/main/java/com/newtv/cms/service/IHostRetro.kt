package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         16:06
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal interface IHostRetro {
    @GET("api/v31/{appkey}/{channelCode}/tvlist/{left}/{right}/{contentID}.json")
    fun getTvList(@Path("appkey") appKey: String,
                  @Path("channelCode") channelid: String,
                  @Path("left") left: String,
                  @Path("right") right: String,
                  @Path("contentID") pageuuid: String): Observable<ResponseBody>

    @GET("api/v31/{appkey}/{channelCode}/programlist/{left}/{right}/{contentID}.json")
    fun getProgramList(@Path("appkey") appKey: String,
                       @Path("channelCode") channelid: String,
                       @Path("left") left: String,
                       @Path("right") right: String,
                       @Path("contentID") pageuuid: String): Observable<ResponseBody>

    @GET("api/v31/{appkey}/{channelCode}/figurelist/{left}/{right}/{contentID}.json")
    fun getFigureList(@Path("appkey") appKey: String,
                      @Path("channelCode") channelid: String,
                      @Path("left") left: String,
                      @Path("right") right: String,
                      @Path("contentID") pageuuid: String): Observable<ResponseBody>

}