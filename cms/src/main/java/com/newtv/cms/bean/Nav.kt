package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:05
 * 创建人:           weihaichao
 * 创建日期:          2018/9/21
 */

data class Nav(
    val pageType: String,
    val currentIcon: String,
    val isFocus: String,
    val defaultIcon: String,
    val widthIcon: String,
    val logo: String,
    val id: String,
    val title: String,
    val isMenu: String,
    val focusIcon: String,
    val poster: String,
    val child: Any
)