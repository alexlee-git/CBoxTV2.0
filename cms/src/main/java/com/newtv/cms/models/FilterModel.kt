package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IFilter
import com.newtv.cms.bean.FilterItem
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         17:03
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class FilterModel : BaseModel(), IFilter {
    override fun getType(): String {
        return Model.MODEL_FILTER
    }

    override fun getFilterKeyWords(appkey: String, channelId: String, categoryId: String,
                                   observer: DataObserver<ModelResult<List<FilterItem>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(categoryId)){
            observer.onError("CategoryId is Empty")
            return
        }
        BuildExecuter<ModelResult<List<FilterItem>>>(Request.filter.getFilterKeyWords(appkey, channelId,
                categoryId), object : TypeToken<ModelResult<List<FilterItem>>>() {}.type)
                .observer(observer)
                .execute()
    }

}