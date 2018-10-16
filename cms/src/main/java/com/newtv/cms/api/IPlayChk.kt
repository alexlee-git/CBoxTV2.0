package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ChkRequest

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:16
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
interface IPlayChk : IService {
    fun check(request:ChkRequest,observer: DataObserver<String>)
}