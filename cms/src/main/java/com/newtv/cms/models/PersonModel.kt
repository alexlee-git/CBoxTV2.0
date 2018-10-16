package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IPerson
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.cms.bean.TvFigure

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         10:46
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
internal class PersonModel : BaseModel(), IPerson {
    override fun getType(): String {
        return Model.MODEL_PERSON
    }

    override fun getPersonTvList(appkey: String, channelId: String, UUID: String, observer: DataObserver<ModelResult<List<SubContent>>>) {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if (TextUtils.isEmpty(UUID) || UUID.length < 2) {
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(UUID)
        val right: String = getRight(UUID)
        BuildExecuter<ModelResult<List<SubContent>>>(Request.person.getPersonTvList(appkey, channelId,
                left, right, UUID), object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getPersonProgramList(appkey: String, channelId: String, UUID: String, observer: DataObserver<ModelResult<List<SubContent>>>) {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if (TextUtils.isEmpty(UUID) || UUID.length < 2) {
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(UUID)
        val right: String = getRight(UUID)
        BuildExecuter<ModelResult<List<SubContent>>>(Request.person.getPersonProgramList(appkey, channelId,
                left, right, UUID), object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getPersonFigureList(appkey: String, channelId: String, UUID: String, observer: DataObserver<ModelResult<List<TvFigure>>>) {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if (TextUtils.isEmpty(UUID) || UUID.length < 2) {
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(UUID)
        val right: String = getRight(UUID)
        BuildExecuter<ModelResult<List<TvFigure>>>(Request.person.getPersonFigureList(appkey, channelId,
                left, right, UUID), object : TypeToken<ModelResult<List<TvFigure>>>() {}.type)
                .observer(observer)
                .execute()
    }
}