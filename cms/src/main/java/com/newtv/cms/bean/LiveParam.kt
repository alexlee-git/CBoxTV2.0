package com.newtv.cms.bean

import android.text.TextUtils

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:07
 * 创建人:           weihaichao
 * 创建日期:         2018/10/8
 *
 * 1：当liveParam为空时，该直播为一次性直播，即从开始时间一直播放到结束时间
 * 2：当liveParam不为空时，按照循环的星期数和开始结束时间的时间进行循环
 */
data class LiveParam(
        val liveParam: String,          //直播循环参数，配置星期几，中间用竖线分割
        val playStartTime: String,      //直播开始时间
        val playEndTime: String,        //直播结束时间
        var isShowDate: Boolean          //是否包含日期
) {
    init {
        isShowDate = TextUtils.isEmpty(liveParam)
    }
}