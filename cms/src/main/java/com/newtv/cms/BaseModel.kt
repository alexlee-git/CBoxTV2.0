package com.newtv.cms

import io.reactivex.Observable
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:15
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
abstract class BaseModel {
    abstract fun getType(): String

    fun <T> execute(observable: Observable<ResponseBody>, type: Type): Executor<T> {
        return Executor(observable, type)
    }

    fun getLeft(contentId: String): String {
        return contentId.substring(0, 2)
    }

    fun getRight(contentId: String): String {
        return contentId.substring(contentId.length - 2)
    }
}