package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Splash

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         16:02
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface ISplash : IService {
    /**
     * 获取开机图片列表
     */
    fun getList(appkey:String,channelId:String,observer: DataObserver<ModelResult<List<Splash>>>):Long
}