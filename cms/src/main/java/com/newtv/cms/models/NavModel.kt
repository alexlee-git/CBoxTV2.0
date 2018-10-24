package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.INav
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Nav

@Suppress("unused")
internal
/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         14:16
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
class NavModel : BaseModel(), INav {
    override fun getNav(appkey: String, channelId: String,
                        observer: DataObserver<ModelResult<List<Nav>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            observer.onError("AppKey or ChannelCode is Empty")
            return 0
        }

        val executor: Executor<ModelResult<List<Nav>>> =
                buildExecutor<ModelResult<List<Nav>>>(Request.nav.getNavInfo(appkey,
                        channelId),
                        object : TypeToken<ModelResult<List<Nav>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getType(): String {
        return Model.MODEL_NAV
    }
}