package com.newtv.cms.service

import com.newtv.libs.Constant
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         12:36
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IBootGuideRetro {

    @GET(Constant.BOOT_GUIDE_HOST + "auth/bootGuide")
    abstract fun getServerAddresses(@Query("platformid") platformId: String): Observable<ResponseBody>
}