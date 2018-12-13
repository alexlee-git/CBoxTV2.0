package com.newtv.cms

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:20
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
interface DataObserver<T> {
    fun onResult(result: T, requestCode: Long)
    fun onError(code: String?, desc: String?)
}