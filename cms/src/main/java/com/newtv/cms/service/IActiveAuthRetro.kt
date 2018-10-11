package com.newtv.cms.service

import com.newtv.libs.HeadersInterceptor
import com.newtv.libs.bean.ActivateBean
import com.newtv.libs.bean.AuthBean
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         12:56
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IActiveAuthRetro {
    @Headers("host_type: " + HeadersInterceptor.ACTIVATE)
    @POST("monkeyking/service/apps/activate")
    abstract fun activate(@Body activateBean: ActivateBean): Observable<ResponseBody>

    @Headers("host_type: " + HeadersInterceptor.ACTIVATE)
    @POST("monkeyking/service/apps/auth")
    abstract fun auth(@Body authBean: AuthBean): Observable<ResponseBody>
}