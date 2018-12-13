package com.newtv.cms.models

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.newtv.cms.*
import com.newtv.cms.api.ICategory
import com.newtv.cms.bean.CategoryTreeNode
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:30
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class CategoryModel : BaseModel(), ICategory {

    override fun getType(): String {
        return Model.MODEL_CATEGORY
    }

    override fun getCategoryTree(appkey: String, channelCode: String,
                                 observer: DataObserver<ModelResult<List<CategoryTreeNode>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelCode)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        val executor: Executor<ModelResult<List<CategoryTreeNode>>> =
                buildExecutor(
                        Request.category.getCategoryTree(appkey, channelCode),
                        object : TypeToken<ModelResult<List<CategoryTreeNode>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }

    override fun getCategoryContent(appkey: String, channelCode: String, contentId: String,
                                    observer: DataObserver<ModelResult<List<SubContent>>>): Long {
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelCode)) {
            observer.onError(CmsErrorCode.APP_ERROR_KEY_CHANNEL_EMPTY, "AppKey or ChannelCode is Empty")
            return 0
        }
        if (TextUtils.isEmpty(contentId) || contentId.length < 2) {
            observer.onError(CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentId size is to short")
            return 0
        }
        val left: String = getLeft(contentId)
        val right: String = getRight(contentId)
        val executor: Executor<ModelResult<List<SubContent>>> =
                buildExecutor(Request.category.getCategoryContent(appkey,
                        channelCode, left, right, contentId),
                        object : TypeToken<ModelResult<List<SubContent>>>() {}.type)
        executor.observer(observer)
                .execute()
        return executor.getID()
    }
}