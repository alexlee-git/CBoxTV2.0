package com.newtv.cms.models

import com.newtv.cms.*
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

    override fun getJson(url: String, observer: DataObserver<String>): Long {
        val executor: Executor<String> = buildExecutor<String>(Request.default.getJson(url), null)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }
}