package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.ITvProgram
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import java.util.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         16:47
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class TvProgramModel : BaseModel(), ITvProgram {
    override fun getCurrentList(appKey: String, channelid: String, pageuuid: String,
                                observer: DataObserver<ModelResult<ArrayList<SubContent>>>): Long {
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(pageuuid) || pageuuid.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val executor: Executor<ModelResult<ArrayList<SubContent>>> =
                buildExecutor(Request.program
                        .getCurrentList
                        (appKey, channelid, pageuuid), object : TypeToken<ModelResult<ArrayList<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getHistoryList(appKey: String, channelid: String, pageuuid: String,
                                observer: DataObserver<ModelResult<ArrayList<SubContent>>>): Long {
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(pageuuid) || pageuuid.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val executor: Executor<ModelResult<ArrayList<SubContent>>> =
                buildExecutor(Request.program
                        .getHistoryList
                        (appKey, channelid, pageuuid), object : TypeToken<ModelResult<ArrayList<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getTvFigureList(appKey: String, channelid: String, pageuuid: String,
                                 observer: DataObserver<ModelResult<ArrayList<SubContent>>>): Long {
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(pageuuid) || pageuuid.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val executor: Executor<ModelResult<ArrayList<SubContent>>> =
                buildExecutor(Request.program
                        .getTvFigureList
                        (appKey, channelid, pageuuid), object : TypeToken<ModelResult<ArrayList<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getTvFigureTvList(appKey: String, channelid: String, pageuuid: String,
                                   observer: DataObserver<ModelResult<ArrayList<SubContent>>>): Long {
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(channelid)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(pageuuid) || pageuuid.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val executor: Executor<ModelResult<ArrayList<SubContent>>> =
                buildExecutor(Request.program
                        .getTvFigureTvList
                        (appKey, channelid, pageuuid), object : TypeToken<ModelResult<ArrayList<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getType(): String {
        return Model.MODEL_TV_PROGRAM
    }

}