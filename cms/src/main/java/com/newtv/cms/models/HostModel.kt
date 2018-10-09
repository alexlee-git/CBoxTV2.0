package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IHost
import com.newtv.cms.bean.HostListItem
import com.newtv.cms.bean.HostProgram
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         16:24
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class HostModel : BaseModel(), IHost {

    override fun getProgramList(appkey: String, channelId: String, contentId: String,
                                observer: DataObserver<ModelResult<List<HostProgram>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(contentId) || contentId.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        execute<ModelResult<List<HostProgram>>>(Request.tv.getProgramList(appkey, channelId, left, right,
                contentId), object : TypeToken<ModelResult<List<HostProgram>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getFigureList(appkey: String, channelId: String, contentId: String,
                               observer: DataObserver<ModelResult<List<HostProgram>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(contentId) || contentId.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
//        val left: String = getLeft(contentId)
//        val right: String = getRight(contentId)
//        execute<ModelResult<List<HostListItem>>>(Request.tv.getFigureList(appkey, channelId, left, right,
//                contentId), object : TypeToken<ModelResult<List<HostListItem>>>() {}.type)
//                .observer(observer)
//                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_HOST
    }

    override fun getTvList(appkey: String, channelId: String, contentId: String,
                           observer: DataObserver<ModelResult<List<HostListItem>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(contentId) || contentId.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        execute<ModelResult<List<HostListItem>>>(Request.tv.getTvList(appkey, channelId, left, right,
                contentId), object : TypeToken<ModelResult<List<HostListItem>>>() {}.type)
                .observer(observer)
                .execute()
    }

}