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
    const val MODEL_UP_VERSTION: String = "UpVersion"       //更新
    const val MODEL_CLOCK: String = "Clock"       //同步时间
    const val MODEL_BOOTGUIDE: String = "BootGuide"       //获取服务地址
    const val MODEL_ACTIVE_AUTH: String = "ActiveAuth"       //认证鉴权


    const val CONTENT_TYPE_PS: String = "PS" //节目集
    const val CONTENT_TYPE_CG: String = "CG" //节目合集
    const val CONTENT_TYPE_CP: String = "CP" //子节目
    const val CONTENT_TYPE_PG: String = "PG" //单节目
    const val CONTENT_TYPE_FG: String = "FG" //人物
    const val CONTENT_TYPE_LV: String = "LV" //直播
    const val CONTENT_TYPE_TV: String = "TV" //电视栏目

    fun <T> findModel(type: String): T? {
        return ModelFactory.findModel<T>(type)
    }

}