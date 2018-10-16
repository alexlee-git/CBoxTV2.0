package com.newtv.cms.models

import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.ISearch
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:08
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
internal class SearchModel : BaseModel(), ISearch {
    
    override fun search(
            appKey: String,
            channelid: String,
            categoryId: String?,
            contentType: String?,
            videoType: String?,
            videoClass: String?,
            area: String?,
            year: String?,
            keyword: String?,
            page: String?,
            rows: String?,
            keywordType: String?,
            observer: DataObserver<ModelResult<List<SubContent>>>) {
        BuildExecuter<ModelResult<List<SubContent>>>(Request.search.search(appKey, channelid,
                categoryId, contentType, videoType, videoClass, area, year, keyword, page, rows,
                keywordType), object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_SEARCH
    }


}