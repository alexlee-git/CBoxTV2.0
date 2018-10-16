package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         10:39
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
interface IPersonRetro {

    /**
     * 获取主持人主持的电视栏目列表
     */
    @GET("api/v31/{appkey}/{channelCode}/tvlist/{left}/{right}/{contentID}.json")
    fun getPersonTvList(@Path("appkey") appkey: String,
                        @Path("channelCode") channel: String,
                        @Path("left") left: String,
                        @Path("right") right: String,
                        @Path("contentID") contentId: String): Observable<ResponseBody>

    /**
     * 获取主持人相关的节目
     */
    @GET("api/v31/{appkey}/{channelCode}/programlist/{left}/{right}/{contentID}.json")
    fun getPersonProgramList(@Path("appkey") appkey: String,
                        @Path("channelCode") channel: String,
                        @Path("left") left: String,
                        @Path("right") right: String,
                        @Path("contentID") contentId: String): Observable<ResponseBody>


    /**
     * 获取主持人相关的主持人
     */
    @GET("api/v31/{appkey}/{channelCode}/figurelist/{left}/{right}/{contentID}.json")
    fun getPersonFigureList(@Path("appkey") appkey: String,
                             @Path("channelCode") channel: String,
                             @Path("left") left: String,
                             @Path("right") right: String,
                             @Path("contentID") contentId: String): Observable<ResponseBody>

}