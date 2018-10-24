package com.newtv.cms.contract

import android.content.Context

import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IPage
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.Page
import com.newtv.libs.Libs

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.view
 * 创建事件:         15:50
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
class PageContract {
    interface View : ICmsView {
        fun onPageResult(page: List<Page>?)
    }

    interface Presenter : ICmsPresenter {
        fun getPageContent(contentId: String)
    }

    class ContentPresenter(context: Context, view: View) : CmsServicePresenter<View>(context, view), Presenter {

        override fun getPageContent(contentId: String) {
            val page = getService<IPage>(CmsServicePresenter.SERVICE_PAGE)
            page?.getPage(
                    Libs.get().appKey,
                    Libs.get().channelId,
                    contentId, object : DataObserver<ModelResult<List<Page>>> {
                override fun onResult(result: ModelResult<List<Page>>) {
                    if (result.isOk()) {
                        view?.onPageResult(result.data)
                    } else {
                        onError(result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }
    }

}
