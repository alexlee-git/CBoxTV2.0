package com.newtv.cms

import android.util.Log
import com.newtv.cms.models.*
import java.util.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:46
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal object ModelFactory {

    private val modelMap: HashMap<String, LinkedList<BaseModel>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    fun <T> findModel(type: String): T? {
        if (modelMap.containsKey(type)) {
            modelMap[type]?.let {
                if (it.size > 0) {
                    Log.e("ModuleFactory", "get model from cache type=$type")
                    return it.pollFirst() as T
                }
            }
        } else {
            modelMap[type] = LinkedList()
        }
        val model: BaseModel? = buildModel(type)
        model?.let {
            return it as T
        }
        return null
    }

    private fun buildModel(type: String): BaseModel? {
        Log.e("ModuleFactory", "build model type=$type")
        return when (type) {
            Model.MODEL_NAV -> NavModel()
            Model.MODEL_CONTENT -> ContentModel()
            Model.MODEL_PAGE -> PageModel()
            Model.MODEL_CATEGORY -> CategoryModel()
            Model.MODEL_CORNER -> CornerModel()
            Model.MODEL_SPLASH -> SplashModel()
            Model.MODEL_FILTER -> FilterModel()
            Model.MODEL_TV_PROGRAM -> TvProgramModel()
            Model.MODEL_UP_VERSTION -> UpVersionModel()
            Model.MODEL_CLOCK -> ClockModel()
            Model.MODEL_BOOTGUIDE -> BootGuideModel()
            Model.MODEL_ACTIVE_AUTH -> AutiveAuthModel()
            Model.MODEL_CHK_PLAY -> PlayChkModel()
            Model.MODEL_PERSON -> PersonModel()
            Model.MODEL_SEARCH -> SearchModel()
            Model.MODEL_ALTERNATE -> AlternateModel()
            Model.MODEL_DEFAULT -> DefaultModel()
            Model.MODEL_USERCENTER -> UserCenterModel()

            else -> {
                Log.e("ModuleFactory", "$type is not registered ! please write it in buildModel")
                null
            }
        }
    }

    fun attach(model: BaseModel) {
        if (!modelMap.containsKey(model.getType())) {
            modelMap[model.getType()] = LinkedList()
        }
        Log.e("ModuleFactory", "put model into cache type=${model.getType()}")
        modelMap[model.getType()]?.push(model)
    }
}