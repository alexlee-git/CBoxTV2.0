package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.Time

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         11:38
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IClock : IService {
    fun sync(observer: DataObserver<Time>)
}