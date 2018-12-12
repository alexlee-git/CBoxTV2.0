package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.ICategory
import com.newtv.cms.bean.CategoryTreeNode
import com.newtv.cms.bean.ModelResult
import com.newtv.libs.Libs


class CategoryContract {

    interface View : ICmsView {
        fun onCategoryResult(context: Context, result: ModelResult<List<CategoryTreeNode>>)
    }

    interface Presenter : ICmsPresenter {
        fun getCategory()
    }

    class CategoryPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view),
            Presenter {

        private var categoryService:ICategory? = null

        init {
            categoryService = getService(SERVICE_CATEGORY)
        }

        override fun getCategory() {

            categoryService?.getCategoryTree(Libs.get().appKey, Libs.get().channelId,  object : DataObserver<ModelResult<List<CategoryTreeNode>>>{
                override fun onResult(result: ModelResult<List<CategoryTreeNode>>, requestCode: Long) {
                    if (result.isOk()) {
                        view?.onCategoryResult(context, result)
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