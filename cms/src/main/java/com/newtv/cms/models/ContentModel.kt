package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IContent
import com.newtv.cms.bean.Content
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:06
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class ContentModel : BaseModel(), IContent {

    override fun getSubContent(
            appkey: String,
            channelId: String,
            contentId: String,
            observer: DataObserver<ModelResult<List<SubContent>>>) {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }

        if (TextUtils.isEmpty(contentId) || contentId.length < 2) {
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        BuildExecuter<ModelResult<List<SubContent>>>(Request.content.getSubInfo(appkey, channelId, left, right,
                contentId), object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
                .observer(observer)
                .execute()
    }


    override fun getContentInfo(appkey: String,
                                channelId: String,
                                contentId: String,
                                observer: DataObserver<ModelResult<Content>>) {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if (TextUtils.isEmpty(contentId) || contentId.length < 2) {
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        BuildExecuter<ModelResult<Content>>(Request.content.getInfo(appkey, channelId, left, right,
                contentId), object : TypeToken<ModelResult<Content>>() {}.type)
                .observer(observer)
                .execute()
    }


    override fun getType(): String {
        return Model.MODEL_CONTENT
    }
}