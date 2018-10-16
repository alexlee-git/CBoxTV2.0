package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IPage
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Page

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:23
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class PageModel : BaseModel(), IPage {
    override fun getType(): String {
        return Model.MODEL_PAGE
    }

    override fun getPage(appkey: String, channelId: String, pageId: String,
                         observer: DataObserver<ModelResult<List<Page>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(pageId) ){
            observer.onError("PageId is Empty")
            return
        }
        BuildExecuter<ModelResult<List<Page>>>(Request.page.getPageData(appkey, channelId, pageId),
                object : TypeToken<ModelResult<List<Page>>>() {}.type)
                .observer(observer)
                .execute()
    }
}