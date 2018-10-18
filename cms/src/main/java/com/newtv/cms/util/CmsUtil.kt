package com.newtv.cms.util

import android.annotation.SuppressLint
import android.text.TextUtils
import com.newtv.cms.bean.LiveParam
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

    /**
     *
     */
    @JvmStatic
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
    @JvmStatic
    fun checkLiveParam(param: LiveParam): Boolean {
        val week: Int = CalendarUtil.getInstance().week
        if (!TextUtils.isEmpty(param.liveParam)) {
            if (param.liveParam.split(",").contains(Integer.toString(week))) {
                return checkInTime(param.playStartTime, param.playEndTime, false)
            }
        } else {
            return checkInTime(param.playStartTime, param.playEndTime, true);
        }
        return false
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("SimpleDateFormat")
    private fun checkInTime(start: String, end: String, hasDate: Boolean = false): Boolean {
        val now = Date()
        val fmt: DateFormat
        if (hasDate) {
            fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        } else {
            fmt = SimpleDateFormat("HH:mm:ss")
        }
        val start: Date = fmt.parse(start)
        val end: Date = fmt.parse(end)
        return now.after(start) && now.before(end);
    }


}