package com.newtv.cms.bean

import com.google.gson.annotations.SerializedName
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
        @SerializedName(value = "contentID",alternate = arrayOf("contentId"))
        val contentID: String,
        val contentType: String,
        val contentUUID: String,
        val duration: String,
        val hImage: String,
        val startTime: String,
        val title: String
)