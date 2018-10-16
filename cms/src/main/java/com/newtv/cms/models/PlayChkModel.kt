package com.newtv.cms.models

import com.google.gson.Gson
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.IPlayChk
import com.newtv.cms.bean.ChkRequest
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:15
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
internal class PlayChkModel : BaseModel(), IPlayChk {

    override fun check(request: ChkRequest, observer: DataObserver<String>) {
        val gson = Gson()
        val requestJson = gson.toJson(request)
        val requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                requestJson)
        BuildExecuter<String>(Request.playChk.getCheckResult(requestBody), null)
                .observer(observer)
                .execute()

    }

    override fun getType(): String {
        return Model.MODEL_CHK_PLAY
    }
}