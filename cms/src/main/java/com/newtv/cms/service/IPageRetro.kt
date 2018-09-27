package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api.v31
 * 创建事件:         11:09
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */
internal interface IPageRetro {
    @GET("api/v31/{appkey}/{channelCode}/page/{pageID}.json")
    fun getPageData(@Path("appkey") appKey: String,
                    @Path("channelCode") channelid: String,
                    @Path("pageID") pageuuid: String): Observable<ResponseBody>
}