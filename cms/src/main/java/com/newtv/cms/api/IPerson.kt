package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.cms.bean.TvFigure

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         10:46
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
interface IPerson {

    /**
     * 获取主持人主持的电视栏目列表
     */
    fun getPersonTvList(appkey: String, channelId: String, UUID: String, observer:
    DataObserver<ModelResult<List<SubContent>>>)

    /**
     * 获取主持人相关的节目
     */
    fun getPersonProgramList(appkey: String, channelId: String, UUID: String, observer:
    DataObserver<ModelResult<List<SubContent>>>)

    /**
     * 获取主持人相关的主持人
     */
    fun getPersonFigureList(appkey: String, channelId: String, UUID: String, observer:
    DataObserver<ModelResult<List<TvFigure>>>)
}