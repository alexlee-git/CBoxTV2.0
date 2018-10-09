package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.ICorner
import com.newtv.cms.bean.Corner
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:57
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class CornerModel : BaseModel(), ICorner {
    override fun getCorner(
            appkey: String,
            channelCode: String,
            observer: DataObserver<ModelResult<List<Corner>>>) {
        if(TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelCode)){
            observer.onError("AppKey or ChannelCode is Empty")
            return
        }
        execute<ModelResult<List<Corner>>>(Request.corner.getCorner(appkey, channelCode),
                object : TypeToken<ModelResult<List<Corner>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_CORNER
    }


}