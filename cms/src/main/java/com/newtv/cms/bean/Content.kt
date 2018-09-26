package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:31
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */

data class Content(
    val description: String,
    val language: Any,
    val title: String,
    val playOrder: Int,
    val MAMID: String,
    val duration: Int,
    val categoryIDs: String,
    val subTitle: String,
    val vImage: String,
    val movieLevel: Int,
    val seriesSum: Any,
    val definition: String,
    val vipProductId: String,
    val contentType: String,
    val area: Any,
    val priceNum: Any,
    val videoType: String,
    val director: String,
    val contentID: Int,
    val csContentIDs: String,
    val tags: String,
    val actors: Any,
    val airtime: Any,
    val sortType: Any,
    val grade: Int,
    val premiereChannel: String,
    val contentUUID: String,
    val hImage: String,
    val videoClass: String,
    val vipFlag: String
)