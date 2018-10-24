package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.libs.bean.ActivateBean
import com.newtv.libs.bean.AuthBean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         12:57
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IActiveAuth : IService {
    fun active(bean: ActivateBean, observer: DataObserver<String>):Long
    fun auth(bean: AuthBean, observer: DataObserver<String>):Long
}