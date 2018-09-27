package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.IContent
import com.newtv.cms.api.INav
import com.newtv.cms.bean.Content
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:06
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class ContentModel : BaseModel(), IContent {

    override fun getInfo(appkey: String, channelId: String, contentId: String,
                         observer: DataObserver<ModelResult<Content>>) {
        if(TextUtils.isEmpty(contentId) || contentId.length<2){
            observer.onError("contentId is Empty")
            return
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        execute<ModelResult<Content>>(Request.content.getInfo(appkey, channelId, left, right,
                contentId), object : TypeToken<ModelResult<Content>>() {}.type)
                .observer(observer)
                .execute()
    }


    override fun getType(): String {
        return Model.MODEL_CONTENT
    }
}