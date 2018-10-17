package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IContent
import com.newtv.cms.bean.Content
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.libs.Libs

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.contract
 * 创建事件:         11:52
 * 创建人:           weihaichao
 * 创建日期:          2018/10/17
 */
class ContentContract {
    interface View : ICmsView {
        fun onContentResult(content: Content?)
        fun onSubContentResult(result: List<SubContent>?)
    }

    interface Presenter : ICmsPresenter {
        fun getContent(uuid: String)
        fun getSubContent(uuid: String)
    }

    class ContentPresenter(context: Context, view: View)
        : CmsServicePresenter<View>(context, view), Presenter {
        override fun getSubContent(uuid: String) {
            val content: IContent? = getService<IContent>(SERVICE_CONTENT)
            content?.getSubContent(Libs.get().appKey, Libs.get().channelId, uuid, object
                : DataObserver<ModelResult<List<SubContent>>> {
                override fun onResult(result: ModelResult<List<SubContent>>) {
                    if (result.isOk()) {
                        view?.onSubContentResult(result.data)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }

        override fun getContent(uuid: String) {
            val content: IContent? = getService<IContent>(SERVICE_CONTENT)
            content?.getContentInfo(Libs.get().appKey, Libs.get().channelId, uuid, object
                : DataObserver<ModelResult<Content>> {
                override fun onResult(result: ModelResult<Content>) {
                    if (result.isOk()) {
                        view?.onContentResult(result.data)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }

            })
        }
    }
}