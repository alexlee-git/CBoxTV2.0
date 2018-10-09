package com.newtv.cms

import android.content.Context

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:01
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
interface ICmsView<P : ICmsPresenter> {
    fun setPresenter(presenter: P)
    fun tip(context: Context, message: String)
    fun onError(context: Context, desc: String)
}