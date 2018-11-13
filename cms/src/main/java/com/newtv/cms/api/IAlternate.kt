package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.Alternate
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         17:53
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
interface IAlternate : IService {
    /**
     * 获取内容详情
     */
    fun getTodayAlternate(appkey: String,
                          channelId: String,
                          contentId: String,
                          observer: DataObserver<ModelResult<List<Alternate>>>
    ): Long
}