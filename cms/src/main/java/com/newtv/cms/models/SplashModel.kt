package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
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

    override fun getList(appkey: String, channelId: String, observer: DataObserver<ModelResult<List<Splash>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }

        BuildExecuter<ModelResult<List<Splash>>>(Request.splash.getList(appkey, channelId),
                object : TypeToken<ModelResult<List<Splash>>>() {}.type)
                .observer(observer)
                .execute()
    }
}