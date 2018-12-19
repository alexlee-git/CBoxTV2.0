package com.newtv.cms.bean

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.newtv.cms.CmsErrorCode

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         09:42
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */
class ModelResult<T> {

    companion object {
        const val IS_AD_TYPE = "1"
    }
    @SerializedName(value = "errorCode",alternate = arrayOf("resultCode"))
    var errorCode: String? = null

    @SerializedName(value = "errorMessage",alternate = arrayOf("resultMessage"))
    var errorMessage: String? = null
        get() {
            field = CmsErrorCode.getErrorMessage(errorCode)
            if (TextUtils.isEmpty(field)) {
                return "UNKNOWN ERROR"
            }
            return field
        }
    var updateTime: String? = null
    val isNav: Int? = null
    val subTitle: String? = null
    val pageTitle: String? = null
    val background: String? = null
    val description: String? = null
    val isAd: String? = null
    val templateZT: String? = null
    var data: T? = null
    var total:Int?=0

    fun asAd():Boolean{
        return IS_AD_TYPE == isAd
    }

    fun isOk(): Boolean {
        return "0".equals(errorCode)
    }
}