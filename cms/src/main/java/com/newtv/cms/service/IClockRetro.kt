package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         11:37
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IClockRetro {

    @Headers("host_type: " + BootGuide.SERVER_TIME)
    @GET("panda/service/current/time")
    abstract fun getClockData(): Observable<ResponseBody>
}