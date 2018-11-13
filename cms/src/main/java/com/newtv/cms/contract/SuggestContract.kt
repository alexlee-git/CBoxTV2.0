package com.newtv.cms.contract

import android.content.Context

import com.newtv.cms.BuildConfig
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IPerson
import com.newtv.cms.api.ITvProgram
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.libs.Libs

import java.util.ArrayList

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         14:11
 * 创建人:           weihaichao
 * 创建日期:          2018/10/16
 */
class SuggestContract {

    interface View : ICmsView {
        fun columnSuggestResult(result: ArrayList<SubContent>?)
        fun columnFiguresResult(result: ArrayList<SubContent>?)
        fun columnPersonFiguresResult(result: ArrayList<SubContent>?)
    }

    interface Presenter : ICmsPresenter {
        /**
         * 栏目相关主持人
         */
        fun getColumnFigures(contentUUID: String)

        /**
         * 栏目相关推荐
         */
        fun getColumnSuggest(contentUUID: String)

        /**
         * 主持人相关主持人
         */
        fun getPersonFigureList(contentUUID: String)

    }

    class SuggestPresenter(context: Context, view: View) : CmsServicePresenter<View>(context, view), Presenter {

        override fun getColumnFigures(contentUUID: String) {
            val tvProgram = getService<ITvProgram>(CmsServicePresenter.SERVICE_TV_PROGRAM)
            tvProgram?.getTvFigureList(
                    Libs.get().appKey,
                    Libs.get().channelId,
                    contentUUID, object : DataObserver<ModelResult<ArrayList<SubContent>>> {
                override fun onResult(result: ModelResult<ArrayList<SubContent>>, requestCode: Long) {
                    if (result.isOk()) {
                        view?.columnFiguresResult(result.data)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }

        override fun getColumnSuggest(contentUUID: String) {
            val tvProgram = getService<ITvProgram>(CmsServicePresenter.SERVICE_TV_PROGRAM)
            tvProgram?.getTvFigureTvList(
                    Libs.get().appKey,
                    Libs.get().channelId,
                    contentUUID, object : DataObserver<ModelResult<ArrayList<SubContent>>> {
                override fun onResult(result: ModelResult<ArrayList<SubContent>>, requestCode: Long) {
                    if (result.isOk()) {
                        view?.columnSuggestResult(result.data)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }

        override fun getPersonFigureList(contentUUID: String) {
            val content = getService<IPerson>(CmsServicePresenter.SERVICE_PERSON_DETAIL)
            content?.getPersonFigureList(Libs.get().appKey, Libs.get().channelId, contentUUID, object : DataObserver<ModelResult<ArrayList<SubContent>>> {
                override fun onResult(result: ModelResult<ArrayList<SubContent>>, requestCode: Long) {
                    if (result.isOk()) {
                        view?.columnPersonFiguresResult(result.data)
                    } else {
                        view?.onError(context, "Error")
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }
    }
}
