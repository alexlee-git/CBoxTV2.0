package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.ITvProgram
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.TvFigure
import com.newtv.cms.bean.TvProgram

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         16:47
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class TvProgramModel : BaseModel(), ITvProgram {
    override fun getCurrentList(appKey: String, channelid: String, pageuuid: String,
                                observer: DataObserver<ModelResult<List<TvProgram>>>) {
        if(TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(pageuuid) || pageuuid.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(pageuuid)
        val right: String = getRight(pageuuid)
        execute<ModelResult<List<TvProgram>>>(Request.program.getCurrentList(appKey, channelid,
                left, right, pageuuid), object : TypeToken<ModelResult<List<TvProgram>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getHistoryList(appKey: String, channelid: String, pageuuid: String,
                                observer: DataObserver<ModelResult<List<TvProgram>>>) {
        if(TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(pageuuid) || pageuuid.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(pageuuid)
        val right: String = getRight(pageuuid)
        execute<ModelResult<List<TvProgram>>>(Request.program.getHistoryList(appKey, channelid,
                left, right, pageuuid), object : TypeToken<ModelResult<List<TvProgram>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getTvFigureList(appKey: String, channelid: String, pageuuid: String,
                                 observer: DataObserver<ModelResult<List<TvFigure>>>) {
        if(TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(pageuuid) || pageuuid.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(pageuuid)
        val right: String = getRight(pageuuid)
        execute<ModelResult<List<TvFigure>>>(Request.program.getTvFigureList(appKey, channelid,
                left, right, pageuuid), object : TypeToken<ModelResult<List<TvFigure>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getTvFigureTvList(appKey: String, channelid: String, pageuuid: String,
                                   observer: DataObserver<ModelResult<List<TvProgram>>>) {
        if(TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        if(TextUtils.isEmpty(pageuuid) || pageuuid.length < 2){
            observer.onError("ContentId size is to short")
            return
        }
        val left: String = getLeft(pageuuid)
        val right: String = getRight(pageuuid)
        execute<ModelResult<List<TvProgram>>>(Request.program.getTvFigureList(appKey, channelid,
                left, right, pageuuid), object : TypeToken<ModelResult<List<TvProgram>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_TV_PROGRAM
    }

}