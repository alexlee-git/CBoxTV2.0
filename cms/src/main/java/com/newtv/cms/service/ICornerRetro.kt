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
 * 创建事件:         15:54
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal interface ICornerRetro {
    @Headers("host_type: " + BootGuide.CMS)
    @GET("api/v31/{appkey}/{channelCode}/corner/corner.json")
    fun getCorner(@Path("appkey") appkey: String, @Path("channelCode") channelid: String):
            Observable<ResponseBody>
}