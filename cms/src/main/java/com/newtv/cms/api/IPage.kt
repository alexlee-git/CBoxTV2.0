package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Page

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:21
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface IPage : IService {

    /**
     * 获取页面数据
     */
    fun getPage(appkey:String,channelId:String,pageId:String,observer:
    DataObserver<ModelResult<List<Page>>>)
}