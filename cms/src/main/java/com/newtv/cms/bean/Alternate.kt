package com.newtv.cms.bean

import com.newtv.libs.util.LogUtils
import com.newtv.libs.util.PlayerTimeUtils

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         17:52
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
data class Alternate(
        val alternateListID: String,
        val contentID: String,
        val contentType: String,
        val contentUUID: String,
        val duration: String,
        val hImage: String,
        val startTime: String,
        val title: String
) {
    fun getStartTimeValue(): Long {
        return PlayerTimeUtils.parseTime(startTime, "yyyy-MM-dd HH:mm:ss.S")
    }

    fun getPlayDuration(): Int {
        return duration.toInt()
    }

    fun isInPlayTime(): Boolean {
        val start: Long = getStartTimeValue()
        val now: Long = System.currentTimeMillis()
        val dur: Int = getPlayDuration()
        LogUtils.d("Alternate", "start=$start now=$now end=${start + dur}")
        return start > now && start + Integer.parseInt(duration) > now
    }
}