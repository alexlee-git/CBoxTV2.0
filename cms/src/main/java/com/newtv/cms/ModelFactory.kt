package com.newtv.cms

import android.util.Log
import com.newtv.cms.models.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:46
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal object ModelFactory {

    private val modelMap: HashMap<String, BaseModel> = HashMap()

    @Suppress("UNCHECKED_CAST")
    fun <T> findModel(type: String): T? {
        if (!modelMap.containsKey(type)) {
            val model: BaseModel? = buildModel(type)
            model?.let {
                modelMap[type] = it
                return model as T
            }
            return null
        }
        return modelMap[type] as T
    }

    private fun buildModel(type: String): BaseModel? {
        var result: BaseModel? = null
        result = when (type) {
            Model.MODEL_NAV -> NavModel()
            Model.MODEL_CONTENT -> ContentModel()
            Model.MODEL_PAGE -> PageModel()
            Model.MODEL_CATEGORY -> CategoryModel()
            Model.MODEL_CORNER -> CornerModel()
            Model.MODEL_SPLASH -> SplashModel()
            Model.MODEL_HOST -> HostModel()
            Model.MODEL_FILTER -> FilterModel()
            Model.MODEL_TV_PROGRAM -> TvProgramModel()
            else -> {
                Log.e("ModleFactory", "$type is not registered ! please write it in buildModel")
                null
            }
        }
        return result
    }

    fun attach(model: BaseModel) {
        modelMap[model.getType()] = model
    }
}