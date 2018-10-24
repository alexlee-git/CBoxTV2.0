package com.newtv.cms.models

import com.newtv.cms.*
import com.newtv.cms.api.IBootGuide

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         12:37
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
internal class BootGuideModel : BaseModel(), IBootGuide {

    override fun getType(): String {
        return Model.MODEL_BOOTGUIDE;
    }

    override fun getBootGuide(platform: String, observer: DataObserver<String>): Long {
        val executor: Executor<String> = buildExecutor<String>(Request.bootGuide.getServerAddresses
        (platform), null)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

}