package com.newtv.cms.models

import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.IUpVersion
import com.newtv.cms.bean.Oriented
import com.newtv.cms.bean.UpVersion

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         10:09
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
internal class UpVersionModel : BaseModel(), IUpVersion {
    override fun getUpVersion(map: Map<String, String>, observer: DataObserver<UpVersion>): Long {
        val executor: Executor<UpVersion> = buildExecutor<UpVersion>(Request.upVersion.getUpVersion
        (map), object :
                TypeToken<UpVersion>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getIsOriented(map: Map<String, String>, observer: DataObserver<Oriented>): Long {
        val executor: Executor<Oriented> =
                buildExecutor<Oriented>(Request.upVersion.getIsOriented(map),
                        object : TypeToken<Oriented>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getType(): String {
        return Model.MODEL_UP_VERSTION
    }


}