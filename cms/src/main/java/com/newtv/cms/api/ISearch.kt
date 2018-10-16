package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:07
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
interface ISearch : IService {
    fun search(
            appKey: String,
            channelid: String,
            categoryId: String? = "",
            contentType: String? = "",
            videoType: String? = "",
            videoClass: String? = "",
            area: String? = "",
            year: String? = "",
            keyword: String? = "",
            page: String? = "",
            rows: String? = "",
            keywordType: String? = "",
            observer: DataObserver<ModelResult<List<SubContent>>>
    )
}