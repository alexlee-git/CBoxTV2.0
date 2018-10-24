package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Nav

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:18
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface INav : IService {

    /**
     * 获取导航数据
     *
     * @param appkey APP_KEY
     * @param channelId CHANNEL_ID
     * @param observer 回调
     */
    fun getNav(appkey: String, channelId: String, observer:
    DataObserver<ModelResult<List<Nav>>>):Long

}