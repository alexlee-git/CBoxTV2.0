package com.newtv.cms.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:33
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */

open class SubContent : Serializable {
    var subTitle: String? = null //副标题
    var vImage: String? = null //竖海报
    var movieLevel: String? = null //影片等级
    var grade: String? = null //评分
    @SerializedName(value = "contentID", alternate = arrayOf("contentId"))
    var contentID: String? = null //内容ID
    var periods: String? = null //集号
    var contentUUID: String? = null //内容UUID
    var title: String? = null //标题
    var hImage: String? = null //横海报
    var contentType: String? = null //内容类型
    var vipFlag: String? = null //付费类型
    var recentNum: String? = null //节目集最新集号（非节目集该字段为空）最新集号（用于电视剧，动漫显示更新至**集）
    var isFinish: String? = null // 是否更新完（非节目集该字段为空）0：非；1：是
    var drm: String? = null //是否付费 腾讯内容专用： 0不付费 1普通付费 2drm付费

}