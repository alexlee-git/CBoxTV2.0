package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
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
                                   observer: DataObserver<ModelResult<List<FilterItem>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(categoryId)) {
            observer.onError("CategoryId is Empty")
            return 0
        }
        val executor: Executor<ModelResult<List<FilterItem>>> =
                buildExecutor<ModelResult<List<FilterItem>>>(Request.filter.getFilterKeyWords
                (appkey, channelId,
                        categoryId), object : TypeToken<ModelResult<List<FilterItem>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

}