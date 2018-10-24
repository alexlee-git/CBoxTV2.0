package com.newtv.cms.models

import com.newtv.cms.*
import com.newtv.cms.api.IActiveAuth
import com.newtv.libs.bean.ActivateBean
import com.newtv.libs.bean.AuthBean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         12:56
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
internal class AutiveAuthModel : BaseModel(), IActiveAuth {
    override fun active(bean: ActivateBean, observer: DataObserver<String>): Long {
        val executor: Executor<String> = buildExecutor<String>(Request.activeAuth.activate(bean),
                null)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun auth(bean: AuthBean, observer: DataObserver<String>): Long {
        val executor: Executor<String> = buildExecutor<String>(Request.activeAuth.auth(bean), null)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getType(): String {
        return Model.MODEL_ACTIVE_AUTH
    }
}