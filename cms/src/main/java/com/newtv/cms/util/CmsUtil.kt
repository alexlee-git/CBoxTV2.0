package com.newtv.cms.util

import android.annotation.SuppressLint
import android.text.TextUtils
import com.newtv.cms.bean.LiveParam
import com.newtv.cms.bean.Video
import com.newtv.libs.util.CalendarUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.util
 * 创建事件:         16:46
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */
object CmsUtil {

    private fun getLiveParam(video:Video?): LiveParam? {
        if (video != null) {
            if ("LIVE" == video.videoType && !TextUtils.isEmpty(video.liveUrl)) {
                return isLiveTime(video.liveParam)
            }
        }
        return null
    }


    @JvmStatic
    fun isLive(video: Video?): LiveParam? {
        val param = getLiveParam(video)
        return if (param != null && CmsUtil.checkLiveParam(param)) {
            param
        } else null
    }

    /**
     *
     */
    fun isLiveTime(params: List<LiveParam>?): LiveParam? {
        if (params == null || params.size <= 0)
            return null
        params.forEach {
            if (checkLiveParam(it)) {
                return it
            }
        }
        return null
    }

    /**
     *
     */
    fun checkLiveParam(param: LiveParam): Boolean {
        val week: Int = CalendarUtil.getCurrentWeek()
        if (!TextUtils.isEmpty(param.liveParam)) {
            if (param.liveParam.contains(Integer.toString(week))) {
                return checkInTime(param.playStartTime, param.playEndTime, false)
            }
        } else {
            return checkInTime(param.playStartTime, param.playEndTime, true);
        }
        return false
    }

    @SuppressLint("SimpleDateFormat")
    @Suppress("NAME_SHADOWING")
    private fun checkInTime(start: String, end: String, hasDate: Boolean = false): Boolean {
        val now = Date()
        val fmt: DateFormat
        if (hasDate) {
            fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val start: Date = fmt.parse(start)
            val end: Date = fmt.parse(end)
            return now.after(start) && now.before(end)
        } else {
            fmt = SimpleDateFormat("HH:mm:ss")
            val current = formatToSeconds(fmt.format(now))
            return formatToSeconds(start) < current && formatToSeconds(end) > current
        }
    }

    fun formatToSeconds(timeFormat: String?): Int {
        if (timeFormat == null) {
            return 0
        }
        val times = timeFormat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var result = 0
        for (index in 0..2) {
            if (times.size >= index + 1) {
                val value = times[index]
                if (!TextUtils.isEmpty(value)) {
                    when (index) {
                        0 -> result += 3600 * Integer.parseInt(value)
                        1 -> result += 60 * Integer.parseInt(value)
                        else -> result += Integer.parseInt(value)
                    }
                }
            }
        }
        return result
    }


}