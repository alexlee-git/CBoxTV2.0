package com.newtv.cms.models

import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IClock
import com.newtv.cms.bean.Time

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         11:37
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
internal class ClockModel : BaseModel(), IClock {

    override fun sync(observer: DataObserver<Time>) {
        BuildExecuter<Time>(Request.clock.getClockData(), object : TypeToken<Time>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_CLOCK
    }

}