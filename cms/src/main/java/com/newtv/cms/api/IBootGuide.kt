package com.newtv.cms.api

import com.newtv.cms.DataObserver

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         12:36
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IBootGuide : IService {
    fun getBootGuide(platform: String, observer: DataObserver<String>):Long
}