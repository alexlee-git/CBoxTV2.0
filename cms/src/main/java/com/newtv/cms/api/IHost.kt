package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.HostListItem
import com.newtv.cms.bean.HostProgram

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         16:23
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface IHost : IService {

    /**
     * 获取主治人主持的电视栏目列表
     */
    fun getTvList(appkey:String, channelId:String, contentId:String,
                  observer: DataObserver<ModelResult<List<HostListItem>>>)

    /**
     * 获取主持人相关节目
     */
    fun getProgramList(appkey: String, channelId: String, contentId: String,
                       observer: DataObserver<ModelResult<List<HostProgram>>>)


    /**
     * 获取主持人相关的主持人
     */
    fun getFigureList(appkey: String, channelId: String, contentId: String,
                      observer: DataObserver<ModelResult<List<HostProgram>>>)
}