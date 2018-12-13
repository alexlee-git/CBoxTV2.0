package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IDefault

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.contract
 * 创建事件:         16:53
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
class DefaultConstract {
    interface View : ICmsView {
        fun onResult(result: String, extends: HashMap<*, *>)
    }

    interface Presenter : ICmsPresenter {
        fun request(url: String, extends: HashMap<*, *>)
    }

    class DefaultPresenter(context: Context, view: View)
        : CmsServicePresenter<View>(context, view), Presenter {

        private var defaultService:IDefault? = null

        init {
            defaultService = getService(SERVICE_DEFAULT);
        }


        override fun request(url: String, extends: HashMap<*, *>) {

            defaultService?.getJson(url, object : DataObserver<String> {
                override fun onResult(result: String, requestCode: Long) {
                    view?.onResult(result,extends)
                }

                override fun onError(code: String?, desc: String?) {
                    view?.onError(context,code, desc)
                }
            })
        }
    }
}