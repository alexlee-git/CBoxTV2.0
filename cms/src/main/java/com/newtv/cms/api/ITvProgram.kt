package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import java.util.ArrayList

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         16:42
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface ITvProgram : IService {
    /**
     * 最新一期电视栏目
     */
    fun getCurrentList(appKey: String,
                       channelid: String,
                       pageuuid: String,
                       observer: DataObserver<ModelResult<ArrayList<SubContent>>>):Long


    /**
     * 电视栏目往期内容列表
     */
    fun getHistoryList(appKey: String,
                       channelid: String,
                       pageuuid: String,
                       observer: DataObserver<ModelResult<ArrayList<SubContent>>>):Long

    /**
     * 电视栏目相关人物列表
     */
    fun getTvFigureList(appKey: String,
                        channelid: String,
                        pageuuid: String,
                        observer: DataObserver<ModelResult<ArrayList<SubContent>>>):Long

    /**
     * 同栏目下的电视栏目列表
     */
    fun getTvFigureTvList(appKey: String,
                          channelid: String,
                          pageuuid: String,
                          observer: DataObserver<ModelResult<ArrayList<SubContent>>>):Long

}