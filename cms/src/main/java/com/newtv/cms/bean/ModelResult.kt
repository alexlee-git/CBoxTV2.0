package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         09:42
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */
class ModelResult<T> {
    var errorCode: String? = null
    var errorMesssage: String? = null
    val updateTime:String? = null
    var data: T? = null

    fun isOk(): Boolean {
        return "0".equals(errorCode)
    }
}