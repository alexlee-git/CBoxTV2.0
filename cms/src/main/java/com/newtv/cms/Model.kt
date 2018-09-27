package com.newtv.cms

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:17
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal object Model {

    const val MODEL_NAV: String = "Nav"             //首页导航
    const val MODEL_CONTENT: String = "Content"     //内容
    const val MODEL_PAGE: String = "Page"           //页面内容
    const val MODEL_CATEGORY: String = "Category"   //栏目
    const val MODEL_CORNER: String = "Corner"       //角标
    const val MODEL_SPLASH: String = "Splash"       //开机图片
    const val MODEL_HOST: String = "Host"           //主持人
    const val MODEL_TV_PROGRAM: String = "TvProgram"       //电视栏目
    const val MODEL_FILTER: String = "Filter"       //筛选

    fun <T> findModel(type: String): T? {
        return ModelFactory.findModel<T>(type)
    }

}