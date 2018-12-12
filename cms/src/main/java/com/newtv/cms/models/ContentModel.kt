package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
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
            observer: DataObserver<ModelResult<List<SubContent>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }

        if (TextUtils.isEmpty(contentId) || contentId.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val executor: Executor<ModelResult<List<SubContent>>> =
                buildExecutor(Request.content.getSubInfo
                (appkey,
                        channelId,
                        contentId), object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }


    override fun getContentInfo(appkey: String,
                                channelId: String,
                                contentId: String,
                                lock:Boolean,
                                observer: DataObserver<ModelResult<Content>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(contentId) || contentId.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        val executor: Executor<ModelResult<Content>> = buildExecutor(Request
                .content.getInfo(appkey,
                channelId, left,
                right,
                contentId), object : TypeToken<ModelResult<Content>>() {}.type,lock)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }


    override fun getType(): String {
        return Model.MODEL_CONTENT
    }
}