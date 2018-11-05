@file:Suppress("UNREACHABLE_CODE")

package com.newtv.cms.bean

data class Program(
        val dataUrl: String,
        val defaultFocus: Int,
        val contentId: String,
        val contentType: String,
        val img: String,
        val title: String,
        val subTitle: String,
        val l_id: String,
        val l_uuid: String,
        val l_contentType: String,
        val l_actionType: String,
        val l_actionUri: String,
        val l_focusId: String,
        val l_focusParam: String,
        val grade: String,
        val lSuperScript: String,
        val rSuperScript: String,
        val lSubScript: String,
        val rSubScript: String,
        val columnPoint: String,
        val rowPoint: String,
        val columnLength: String,
        val rowHeight: String,
        val cellType: String,
        val cellCode: String,
        val isAd: Int,
        val sortNum: String,
        val seriesSubUUID: String,
        val apk: String,
        val apkPageType: String,
        val apkPageParam: String,
        val specialParam: String,
        val recommendedType: String,
        val recentNum: String,
        val isFinish: String,
        val video: Video?
) {
    fun getLiveParam(): LiveParam? {
        return null
    }
}