package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         17:02
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal interface IFilterRetro {

    @GET("api/v31/{appkey}/{channelCode}/filterkeywords/{categoryID}.json")
    fun getFilterKeyWords(
            @Path("appkey") appkey: String,
            @Path("channelCode") channelid: String,
            @Path("categoryID") categoryId: String
    ): Observable<ResponseBody>
}