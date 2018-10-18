package com.newtv.cms

import io.reactivex.Observable
import okhttp3.ResponseBody
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:15
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal abstract class BaseModel {

    private val executors: ConcurrentLinkedQueue<Executor<*>> = ConcurrentLinkedQueue()

    fun stop() {
        for (iterator: Executor<*> in executors.iterator()) {
            iterator.cancel()
        }
    }

    fun stop(executor: Executor<*>) {
        executor.cancel()
    }

    fun destroy() {
        stop()
        executors.clear()
    }

    abstract fun getType(): String

    fun <T> BuildExecuter(observable: Observable<ResponseBody>, type: Type?): Executor<T> {
        val executor: Executor<T> = Executor(observable, type, object : Executor.IExecutor<T> {
            override fun onCancel(executor: Executor<T>) {
                executors.remove(executor)
            }
        })
        executors.add(executor)
        return executor
    }

    fun getLeft(contentId: String): String {
        return contentId.substring(0, 2)
    }

    fun getRight(contentId: String): String {
        return contentId.substring(contentId.length - 2)
    }
}