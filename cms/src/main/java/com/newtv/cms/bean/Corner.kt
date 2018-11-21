package com.newtv.cms.bean

import android.text.TextUtils
import java.lang.reflect.Field

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         15:56
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */

data class Corner(
        var cornerCondition: List<CornerCondition>? = null,
        var cornerId: String? = null,
        var cornerImg: String? = null,
        var cornerPosition: String? = null
) {
    companion object {
        //1.左上 2右上 3左下 4右下
        const val LEFT_TOP: String = "1"
        const val RIGHT_TOP: String = "2"
        const val LEFT_BOTTOM: String = "3"
        const val RIGHT_BOTTOM: String = "4"
    }
}

data class CornerCondition(
        val fieldName: String,
        val fieldValue: String
)