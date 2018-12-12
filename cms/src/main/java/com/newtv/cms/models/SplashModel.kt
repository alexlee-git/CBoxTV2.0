package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.ISplash
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Splash

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         16:03
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class SplashModel : BaseModel(), ISplash {
    override fun getType(): String {
        return Model.MODEL_SPLASH
    }

    override fun getList(appkey: String, channelId: String, observer:
    DataObserver<ModelResult<List<Splash>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }

        val executor: Executor<ModelResult<List<Splash>>> =
                buildExecutor(Request.splash.getList(appkey,
                        channelId),
                        object : TypeToken<ModelResult<List<Splash>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }
}