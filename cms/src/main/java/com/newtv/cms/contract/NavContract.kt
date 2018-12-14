package com.newtv.cms.contract

import android.content.Context

import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.INav
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Nav
import com.newtv.libs.Libs

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.menu
 * 创建事件:         15:28
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
class NavContract {
    interface View : ICmsView {
        fun onNavResult(context: Context, result: List<Nav>?)
    }

    interface Presenter : ICmsPresenter {
        fun requestNav()
    }

    class MainNavPresenter(context: Context, view: View) : CmsServicePresenter<View>(context, view), Presenter {


        private var navService:INav? = null

        init {
            navService = getService<INav>(CmsServicePresenter.SERVICE_NAV)
        }

        override fun requestNav() {
            navService?.getNav(
                    Libs.get().appKey,
                    Libs.get().channelId,
                    object : DataObserver<ModelResult<List<Nav>>> {
                        override fun onResult(result: ModelResult<List<Nav>>, requestCode: Long) {
                            if (result.isOk()) {
                                view?.onNavResult(context, result.data)
                            } else {
                                onError(result.errorCode, result.errorMessage)
                            }
                        }

                        override fun onError(code: String?, desc: String?) {
                            view?.onError(context,code, desc)
                        }
                    })
        }

    }


}
