package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.Oriented
import com.newtv.cms.bean.UpVersion

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         10:09
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
interface IUpVersion : IService {
    fun getUpVersion(map: Map<String, String>, observer: DataObserver<UpVersion>):Long
    fun getIsOriented(map: Map<String, String>, observer: DataObserver<Oriented>):Long
}