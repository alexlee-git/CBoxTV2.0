package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IAlternate
import com.newtv.cms.bean.Alternate
import com.newtv.cms.bean.ModelResult
import com.newtv.libs.Libs

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.contract
 * 创建事件:         17:59
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 */
class AlternateContract {

    interface View : ICmsView {
        fun onAlternateResult(alternates: List<Alternate>?)
    }

    interface LoadingView : View {
        fun onLoading()
        fun loadComplete()
    }

    interface Presenter : ICmsPresenter {
        fun getTodayAlternate(contentId: String)
    }

    class AlternatePresenter(context: Context, view: View?) : CmsServicePresenter<View>(context,
            view), Presenter {

        private var alternate:IAlternate? = null

        init {
            alternate = getService<IAlternate>(SERVICE_ALTERNATE)
        }

        override fun getTodayAlternate(contentId: String) {

            alternate?.let { alter ->
                if(view != null && view is LoadingView){
                    view.onLoading()
                }
                alter.getTodayAlternate(Libs.get().appKey, Libs.get().channelId,
                        contentId,
                        object : DataObserver<ModelResult<List<Alternate>>> {
                            override fun onResult(result: ModelResult<List<Alternate>>, requestCode: Long) {
                                if(view != null && view is LoadingView){
                                    view.loadComplete()
                                }
                                if (result.isOk()) {
                                    view?.onAlternateResult(result.data)
                                } else {
                                    view?.onError(context,result.errorCode, result.errorMessage)
                                }
                            }

                            override fun onError(code: String?, desc: String?) {
                                view?.onError(context,code, desc)
                            }

                        })
            }
        }

    }
}