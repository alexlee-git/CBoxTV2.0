package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         16:42
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal interface ITvProgramRetro {

    /**
     * 最新一期电视栏目
     */
    @Headers("host_type: " + BootGuide.CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=currentlist")
    fun getCurrentList(@Path("appkey") appKey: String,
                       @Path("channelCode") channelid: String,
                       @Path("contentID") pageuuid: String): Observable<ResponseBody>


    /**
     * 电视栏目往期内容列表
     */
    @Headers("host_type: " + BootGuide.CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=historylist")
    fun getHistoryList(@Path("appkey") appKey: String,
                       @Path("channelCode") channelid: String,
                       @Path("contentID") pageuuid: String): Observable<ResponseBody>

    /**
     * 电视栏目相关人物列表
     */
    @Headers("host_type: " + BootGuide.CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=tvfigurelist")
    fun getTvFigureList(@Path("appkey") appKey: String,
                        @Path("channelCode") channelid: String,
                        @Path("contentID") pageuuid: String): Observable<ResponseBody>

    /**
     * 同栏目下的电视栏目列表
     */
    @Headers("host_type: " + BootGuide.CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=tvfigureoftvlist")
    fun getTvFigureTvList(@Path("appkey") appKey: String,
                          @Path("channelCode") channelid: String,
                          @Path("contentID") pageuuid: String): Observable<ResponseBody>

}