package com.newtv.cms.models

import com.google.gson.reflect.TypeToken
import com.newtv.cms.BaseModel
import com.newtv.cms.DataObserver
import com.newtv.cms.Model
import com.newtv.cms.Request
import com.newtv.cms.api.ICategory
import com.newtv.cms.bean.CategoryItem
import com.newtv.cms.bean.CategoryTreeNode
import com.newtv.cms.bean.ModelResult

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:30
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
class CategoryModel : BaseModel(), ICategory {

    override fun getType(): String {
        return Model.MODEL_CATEGORY
    }

    override fun getCategoryTree(appkey: String, channelCode: String,
                                 observer: DataObserver<ModelResult<List<CategoryTreeNode>>>) {
        execute<ModelResult<List<CategoryTreeNode>>>(Request.category.getCategoryTree(appkey,
                channelCode), object : TypeToken<ModelResult<List<CategoryTreeNode>>>() {}.type)
                .observer(observer)
                .execute()
    }

    override fun getCategoryContent(appkey: String, channelCode: String, contentId: String,
                                    observer: DataObserver<ModelResult<List<CategoryItem>>>) {
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        execute<ModelResult<List<CategoryItem>>>(Request.category.getCategoryContent(appkey,
                channelCode, left, right, contentId),
                object : TypeToken<ModelResult<List<CategoryItem>>>() {}.type)
                .observer(observer)
                .execute()
    }
}