package com.newtv.cms.models

import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
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
    override fun getUpVersion(map: Map<String, String>, observer: DataObserver<UpVersion>) {
        execute<UpVersion>(Request.upVersion.getUpVersion(map), object : TypeToken<UpVersion>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getIsOriented(map: Map<String, String>, observer: DataObserver<Oriented>) {
        execute<Oriented>(Request.upVersion.getIsOriented(map), object : TypeToken<Oriented>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getType(): String {
        return Model.MODEL_UP_VERSTION
    }


}