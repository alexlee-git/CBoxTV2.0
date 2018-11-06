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
        var cornerPosition: String? = null,
        var cornerType: String? = null
) {
    /**
     * 是否适配使用
     */
    fun suitFor(any: Any): Boolean {
        try {
            val clz: Array<Field> = any::class.java.declaredFields
            clz.forEach {
                cornerCondition?.let { list ->
                    list.forEach { condition ->
                        if (TextUtils.equals(condition.fieldName, it.name)
                                && TextUtils.equals(condition.fieldValue, it.get(any).toString())) {
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}

data class CornerCondition(
        val fieldName: String,
        val fieldValue: String
)