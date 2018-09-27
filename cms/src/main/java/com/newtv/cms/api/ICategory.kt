package com.newtv.cms.api

import com.newtv.cms.DataObserver
import com.newtv.cms.bean.CategoryItem
import com.newtv.cms.bean.CategoryTreeNode
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:28
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface ICategory : IService {

    /**
     * 获取指定EPG下栏目树，包含一级栏目和二级栏目
     */
    fun getCategoryTree(appkey: String, channelCode: String,
                        observer: DataObserver<ModelResult<List<CategoryTreeNode>>>)

    /**
     * 获取栏目下内容列表
     */
    fun getCategoryContent(appkey: String, channelCode: String, contentId: String,
                           observer: DataObserver<ModelResult<List<CategoryItem>>>)
}