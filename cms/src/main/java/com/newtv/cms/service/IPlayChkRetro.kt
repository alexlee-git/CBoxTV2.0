package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         15:15
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
internal interface IPlayChkRetro {
    @Headers("Content-Type: application/json", "Accept: application/json", "host_type: " + BootGuide
            .PERMISSTION_CHECK)//需要添加头
    @POST("goldenpheasant/api/orders/check")
    abstract fun getCheckResult(@Body requestBody: RequestBody, @Header("Authorization")authorization : String): Observable<ResponseBody>
}