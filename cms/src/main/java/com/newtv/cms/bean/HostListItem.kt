package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:22
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */

data class HostListItem(
    val subTitle: Any,
    val vImage: String,
    val movieLevel: Any,
    val grade: Any,
    val contentID: Int,
    val periods: String,
    val contentUUID: String,
    val title: String,
    val hImage: String,
    val contentType: String,
    val vipFlag: String,
    val drm: Any
)