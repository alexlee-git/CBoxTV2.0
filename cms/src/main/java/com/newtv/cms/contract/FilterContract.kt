package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IFilter
import com.newtv.cms.bean.FilterItem
import com.newtv.cms.bean.ModelResult
import com.newtv.libs.Libs


class FilterContract {

    interface View : ICmsView {
        fun onFilterResult(context: Context, result: ModelResult<List<FilterItem>>)
    }

    interface Presenter : ICmsPresenter {
        fun getFilter(categoryId: String)
    }

    class FilterPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view),
            Presenter {
        override fun getFilter( categoryId: String) {
            val filter: IFilter? = getService(SERVICE_FILTER)
            filter?.getFilterKeyWords(Libs.get().appKey, Libs.get().channelId,categoryId, object : DataObserver<ModelResult<List<FilterItem>>> {
                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }

                override fun onResult(result: ModelResult<List<FilterItem>>, requestCode: Long) {
                    if (result.isOk()) {
                        view?.onFilterResult(context, result)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

            })

        }
    }
}