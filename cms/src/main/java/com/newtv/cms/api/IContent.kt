package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.Content
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:06
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface IContent {

    /**
     * 获取内容详情
     */
    fun getInfo(appkey: String, channelId: String, contentId: String,
                observer: DataObserver<ModelResult<Content>>)
}