package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         10:37
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
data class Video(
        val videoType: String,
        val contentId: String,
        val contentUUID: String,
        val liveUrl: String,
        val liveParam: List<LiveParam>?
) {
    override fun toString(): String {
        return "Video(videoType='$videoType', contentId='$contentId', contentUUID='$contentUUID', liveUrl='$liveUrl', liveParam=$liveParam)"
    }
}