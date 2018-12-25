package com.newtv.cms.bean

import com.google.gson.annotations.SerializedName

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         17:52
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
data class Alternate(
        val alternateListID: String,
        @SerializedName(value = "contentID", alternate = arrayOf("contentId"))
        val contentID: String,
        val contentType: String,
        val contentUUID: String,
        val duration: String,
        val hImage: String,
        val startTime: String,
        val title: String,
        var isAd: String? = "0"
) {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return hashCode() == it.hashCode()
        }
        return super.equals(other)
    }

    fun IsAd():String?{
        return isAd
    }

    override fun hashCode(): Int {
        var result = alternateListID.hashCode()
        result = 31 * result + contentID.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + contentUUID.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + hImage.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}