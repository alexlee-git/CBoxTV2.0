package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.ICorner

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.contract
 * 创建事件:         13:16
 * 创建人:           weihaichao
 * 创建日期:          2018/10/26
 */
class CornerContract {

    interface View : ICmsView {
        fun onCornerResult(context: Context, result: String?)
    }

    interface Presenter : ICmsPresenter {
        fun getCorner(appkey: String, channelCode: String)
    }

    class CornerPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view),
            Presenter {

        private var cornerService:ICorner? = null

        init {
            cornerService = getService(SERVICE_CORNER)
        }


        override fun getCorner(appkey: String, channelCode: String) {

            cornerService?.getCorner(appkey, channelCode, object : DataObserver<String> {
                override fun onResult(result: String, requestCode: Long) {
                    result.let {
                        view?.onCornerResult(context, it)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }
    }
}