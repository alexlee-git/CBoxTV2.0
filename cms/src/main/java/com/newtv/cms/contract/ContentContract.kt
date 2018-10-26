package com.newtv.cms.contract

import android.content.Context
import android.text.TextUtils
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IContent
import com.newtv.cms.bean.Content
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.libs.Constant
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
        fun onSubContentResult(result: ArrayList<SubContent>?)
    }

    interface LoadingView : View {
        fun onLoading()
        fun loadComplete()
    }

    interface Presenter : ICmsPresenter {
        /**
         * @param uuid ID
         * @param autoSub 自动获取播放列表
         *                  如果设置为true，onContentResult回调结果中Content中data自动获取
         *                  如果设置为false，onContentResult回调结果中data为空
         */
        fun getContent(uuid: String, autoSub: Boolean)

        /**
         * 是不是电视剧
         */
        fun isTvSeries(content: Content?): Boolean

        fun cancel(id: Long)
        fun getSubContent(uuid: String): Long
    }

    class ContentPresenter(context: Context, view: View)
        : CmsServicePresenter<View>(context, view), Presenter {

        override fun isTvSeries(content: Content?): Boolean {
            content?.let {
                val videoType = content.videoType
                return !(!TextUtils.isEmpty(videoType) && (TextUtils.equals(videoType, "电视剧") || TextUtils.equals(videoType, "动漫")))
            }
            return false
        }

        override fun cancel(id: Long) {
            val content: IContent? = getService(SERVICE_CONTENT)
            content?.cancel(id)
        }

        override fun getSubContent(uuid: String): Long {
            val content: IContent? = getService(SERVICE_CONTENT)

            content?.let {
                val id: Long = it.getSubContent(Libs.get().appKey, Libs.get().channelId, uuid, object
                    : DataObserver<ModelResult<List<SubContent>>> {
                    override fun onResult(result: ModelResult<List<SubContent>>, requestCode: Long) {
                        if (result.isOk()) {
                            view?.onSubContentResult(ArrayList(result.data))
                        } else {
                            view?.onError(context, result.errorMessage)
                        }
                    }

                    override fun onError(desc: String?) {
                        view?.onError(context, desc)
                    }
                })
                return id
            }
            return 0L
        }

        fun getSubContentsWithCallback(contentResult: Content?, uuid: String) {
            if (contentResult != null) {
                val content: IContent? = getService(SERVICE_CONTENT)
                content?.let { iContent ->
                    var single = false
                    var suuid: String? = uuid
                    if (Constant.CONTENTTYPE_PG.equals(contentResult.contentType)
                            || Constant.CONTENTTYPE_CP.equals(contentResult.contentType)) {
                        single = true
                        suuid = contentResult.csContentIDs
                    }
                    iContent.getSubContent(Libs.get().appKey, Libs.get().channelId,
                            suuid!!,
                            object : DataObserver<ModelResult<List<SubContent>>> {
                                override fun onResult(result: ModelResult<List<SubContent>>, requestCode: Long) {
                                    if (result.isOk()) {
                                        if (!single) {
                                            contentResult.data = (ArrayList(result.data))
                                        } else {
                                            result.data?.let {
                                                for (sub: SubContent in it) {
                                                    if (TextUtils.equals(sub.contentID, uuid)) {
                                                        contentResult.data = arrayListOf(sub)
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                        view?.onContentResult(contentResult)
                                        view?.let {
                                            if (it is LoadingView) it.loadComplete()
                                        }
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

        override fun getContent(uuid: String, autoSub: Boolean) {
            val content: IContent? = getService(SERVICE_CONTENT)
            view?.let {
                if (it is LoadingView) it.onLoading()
            }
            content?.getContentInfo(Libs.get().appKey, Libs.get().channelId, uuid, object
                : DataObserver<ModelResult<Content>> {
                override fun onResult(result: ModelResult<Content>, requestCode: Long) {
                    if (result.isOk()) {
                        if (!autoSub) {
                            view?.onContentResult(result.data);
                        } else {
                            getSubContentsWithCallback(result.data, uuid)
                        }
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