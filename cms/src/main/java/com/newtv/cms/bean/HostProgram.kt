package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:28
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */

data class HostProgram(
    val subTitle: String,
    val vImage: String,
    val movieLevel: Int,
    val grade: Int,
    val contentID: Int,
    val periods: String,
    val contentUUID: String,
    val title: String,
    val hImage: String,
    val contentType: String,
    val vipFlag: String,
    val drm: Any
)