package com.newtv.cms.service

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         18:32
 * 创建人:           weihaichao
 * 创建日期:          2018/9/21
 */
interface INavRetro  {
    @GET("api/v31/{appkey}/{channelCode}/navigation/index.json")
    fun getNavInfo(@Path("appkey") appkey: String, @Path("channelCode") channelid: String):
            Observable<ResponseBody>
}