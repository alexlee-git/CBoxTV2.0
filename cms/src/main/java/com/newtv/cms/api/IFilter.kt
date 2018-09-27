package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.FilterItem
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         17:00
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface IFilter : IService {

    /**
     * 获取筛选选项列表
     */
    fun getFilterKeyWords(appkey: String, channelId: String, categoryId: String,
                          observer: DataObserver<ModelResult<List<FilterItem>>>)


}