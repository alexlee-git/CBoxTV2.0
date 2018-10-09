package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:33
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */

data class SubContent(
    val subTitle: String,//副标题
    val vImage: String,//竖海报
    val movieLevel: String,//影片等级
    val grade: Int,//评分
    val contentID: Int,//内容ID
    val periods: String,//集号
    val contentUUID: String,//内容UUID
    val title: String,//标题
    val hImage: String,//横海报
    val contentType: String,//内容类型
    val vipFlag: String,//付费类型
    val drm: String //是否付费 腾讯内容专用： 0不付费 1普通付费 2drm付费
)