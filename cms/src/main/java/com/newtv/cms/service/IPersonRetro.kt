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
 * 创建事件:         10:39
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
interface IPersonRetro {

    /**
     * 获取主持人主持的电视栏目列表
     */
    @Headers("host_type: " + BootGuide.NEW_CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=tvlist")
    fun getPersonTvList(@Path("appkey") appkey: String,
                        @Path("channelCode") channel: String,
                        @Path("contentID") contentId: String): Observable<ResponseBody>

    /**
     * 获取主持人相关的节目
     */
    @Headers("host_type: " + BootGuide.NEW_CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=programlist")
    fun getPersonProgramList(@Path("appkey") appkey: String,
                        @Path("channelCode") channel: String,
                        @Path("contentID") contentId: String): Observable<ResponseBody>


    /**
     * 获取主持人相关的主持人
     */
    @Headers("host_type: " + BootGuide.NEW_CMS)
    @GET("api/v31/{appkey}/{channelCode}/detailsubcontents/{contentID}.json?subcontenttype=figurelist")
    fun getPersonFigureList(@Path("appkey") appkey: String,
                             @Path("channelCode") channel: String,
                             @Path("contentID") contentId: String): Observable<ResponseBody>

}