package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         15:02
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
interface ISearchRetro {
    @Headers("host_type: " + BootGuide.SEARCH)
    @GET(value = "api/v31/{appkey}/{channelCode}/search.json")
    fun search(
            @Path(value = "appkey", encoded = false) appKey: String,
            @Path(value = "channelCode", encoded = false) channelid: String,
            @Query(value = "categoryId", encoded = false) categoryId: String?,
            @Query(value = "contentType", encoded = false) contentType: String?,
            @Query(value = "videoType", encoded = false) videoType: String?,
            @Query(value = "videoClass", encoded = false) videoClass: String?,
            @Query(value = "area", encoded = false) area: String?,
            @Query(value = "year", encoded = false) year: String?,
            @Query(value = "keyword", encoded = false) keyword: String?,
            @Query(value = "page", encoded = false) page: String?,
            @Query(value = "rows", encoded = false) rows: String?,
            @Query(value = "keywordType", encoded = false) keywordType: String?
    ): Observable<ResponseBody>
}