package com.newtv.cms.service

import com.newtv.libs.HeadersInterceptor
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         16:47
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
interface IDefaultRetro {
    @GET
    fun getJson(@Url url: String):Observable<ResponseBody>
}