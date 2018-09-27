package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.Corner
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:56
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface ICorner : IService {

    /**
     * 获取角标列表
     */
    fun getCorner(appkey:String,channelCode:String,observer:
    DataObserver<ModelResult<List<Corner>>>)
}