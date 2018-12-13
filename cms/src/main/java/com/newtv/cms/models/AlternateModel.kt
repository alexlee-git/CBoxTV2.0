package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.IAlternate
import com.newtv.cms.bean.Alternate
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         17:54
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
internal class AlternateModel : BaseModel(), IAlternate {
    override fun getType(): String {
        return Model.MODEL_ALTERNATE
    }

    override fun getTodayAlternate(appkey: String, channelId: String, contentId: String,
                                   observer: DataObserver<ModelResult<List<Alternate>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }

        if (TextUtils.isEmpty(contentId)) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is invalid")
            return 0
        }

        val executor: Executor<ModelResult<List<Alternate>>> =
                buildExecutor(Request.alternate.getInfo
                (appkey, channelId, contentId), object :
                        TypeToken<ModelResult<List<Alternate>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()


    }
}