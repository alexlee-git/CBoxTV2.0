package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         10:08
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IUpVersionRetro {
    @Headers("host_type: " + BootGuide.VERSION_UP)
    @GET("bradypod/api/apps")
    abstract fun getUpVersion(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.IS_ORIENTED)
    @GET("bradypod/api/apps/isOriented")
    abstract fun getIsOriented(@QueryMap map: Map<String, String>): Observable<ResponseBody>
}