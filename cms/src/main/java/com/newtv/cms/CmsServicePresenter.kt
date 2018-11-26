package com.newtv.cms

import android.content.Context
import com.newtv.cms.api.IContent
import com.newtv.cms.api.IService

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         11:37
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
open class CmsServicePresenter<V : ICmsView>(
        val context: Context, val view: V?) : ICmsPresenter {

    override fun destroy() {
        stop()
        models.forEach {
            it.destroy()
            if(it is BaseModel){
                ModelFactory.attach(it)
            }
        }
        models.clear()
    }

    override fun stop() {
        models.forEach { it.stop() }
    }

    companion object {
        const val SERVICE_NAV: String = Model.MODEL_NAV               // interface-> INav
        const val SERVICE_CLOCK: String = Model.MODEL_CLOCK               // interface-> IClock
        const val SERVICE_BOOT_GUIDE: String = Model.MODEL_BOOTGUIDE               // interface->
        // IBootGuide
        const val SERVICE_CONTENT: String = Model.MODEL_CONTENT       // interface-> IContent
        const val SERVICE_DEFAULT: String = Model.MODEL_DEFAULT       // interface-> IDefault
        const val SERVICE_SEARCH: String = Model.MODEL_SEARCH       // interface-> ISearch
        const val SERVICE_ALTERNATE: String = Model.MODEL_ALTERNATE       // interface-> IAlternate
        const val SERVICE_ACTIVE_AUTH: String = Model.MODEL_ACTIVE_AUTH       // interface->
        // IActiveAuth
        const val SERVICE_CATEGORY: String = Model.MODEL_CATEGORY     // interface-> ICategory
        const val SERVICE_PAGE: String = Model.MODEL_PAGE             // interface-> IPage
        const val SERVICE_TV_PROGRAM: String = Model.MODEL_TV_PROGRAM // interface-> ITvProgram
        const val SERVICE_FILTER: String = Model.MODEL_FILTER         // interface-> IFilter
        const val SERVICE_SPLASH: String = Model.MODEL_SPLASH         // interface-> ISplash
        const val SERVICE_CORNER: String = Model.MODEL_CORNER         // interface-> ICorner
        const val SERVICE_CHK_PLAY: String = Model.MODEL_CHK_PLAY         // interface-> IPlayChk
        const val SERVICE_UPVERSTION: String = Model.MODEL_UP_VERSTION         // interface->

        const val SERVICE_PERSON_DETAIL:String = Model.MODEL_PERSON
        // IUpVersion
    }

    private val models: ArrayList<IService> = ArrayList()

    protected fun <T : IService> getService(type: String): T? {
        val model = Model.findModel<T>(type)
        model?.let {
            if (!models.contains(it)) {
                models.add(it)
            }
        }
        return model
    }
}