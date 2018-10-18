package com.newtv.cms.models

import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IDefault

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         16:49
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
internal class DefaultModel : BaseModel(), IDefault {

    override fun getType(): String {
        return Model.MODEL_SEARCH
    }

    override fun getJson(url: String, observer: DataObserver<String>) {
        BuildExecuter<String>(Request.default.getJson(url), null)
                .observer(observer)
                .execute()
    }
}