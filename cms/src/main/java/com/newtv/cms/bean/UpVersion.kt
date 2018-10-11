package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         10:18
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */

data class UpVersion(
    val versionCode: String,
    val versionName: String,
    val versionDescription: String,
    val packageSize: String,
    val packageAddr: String,
    val packageMD5: String,
    val upgradeType: String,
    val channelId: String,
    val channelCode: String,
    val appKey: String
)