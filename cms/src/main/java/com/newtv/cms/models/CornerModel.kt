package com.newtv.cms.models

import android.text.TextUtils
import com.newtv.cms.*
import com.newtv.cms.api.ICorner

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
            observer: DataObserver<String>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelCode)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, CmsErrorCode.getErrorMessage(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY))
            return 0
        }
        val executor: Executor<String> =
                buildExecutor(Request.corner.getCorner(appkey, channelCode), null)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getType(): String {
        return Model.MODEL_CORNER
    }


}