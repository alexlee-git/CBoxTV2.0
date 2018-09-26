package com.newtv.cms

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         17:40
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
abstract class BasePresenter {

    protected abstract fun onCreate()
    protected abstract fun onStop()
    protected abstract fun onDestroy()

    fun <T> findModel(type: String): T? {
        return ModelFactory.findModel<T>(type)
    }

    fun create() {

    }

    fun stop() {

    }

    fun destroy() {

    }

    init {
        initialize()
    }

    private fun initialize() {

    }
}