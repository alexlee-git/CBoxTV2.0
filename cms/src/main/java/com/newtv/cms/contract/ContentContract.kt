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

    interface ContentCallabck : ICmsView {
        fun onContentResult(content: Content?)
    }

    interface View : ICmsView {
        fun onContentResult(uuid: String, content: Content?)
        fun onSubContentResult(uuid: String, result: ArrayList<SubContent>?)
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
        fun getContent(uuid: String, autoSub: Boolean): Long

        fun getContent(uuid: String, lock: Boolean, callback: View?)
        fun getContent(uuid: String, autoSub: Boolean, contentType: String)

        /**
         * 是不是电视剧
         */
        fun isTvSeries(content: Content?): Boolean

        fun cancel(id: Long)
        fun getSubContent(uuid: String): Long
    }

    class ContentPresenter(context: Context, view: View?)
        : CmsServicePresenter<View>(context, view), Presenter {

        private var contentService: IContent? = null

        init {
            contentService = getService(SERVICE_CONTENT)
        }

        override fun getContent(uuid: String, lock: Boolean, callback: View?) {
            contentService?.let {
                it.getContentInfo(Libs.get().appKey, Libs.get().channelId, uuid, lock, object
                    : DataObserver<ModelResult<Content>> {
                    override fun onResult(result: ModelResult<Content>, requestCode: Long) {
                        if (result.isOk()) {
                            callback?.onContentResult(uuid, result.data);
                        } else {
                            callback?.onError(context, result.errorMessage)
                        }
                    }

                    override fun onError(desc: String?) {
                        callback?.onError(context, desc)
                    }
                })
                return
            }
        }

        override fun isTvSeries(content: Content?): Boolean {
            content?.let {
                val videoType = content.videoType
                return !(!TextUtils.isEmpty(videoType) && (TextUtils.equals(videoType, "电视剧") || TextUtils.equals(videoType, "动漫")))
            }
            return false
        }

        override fun cancel(id: Long) {
            contentService?.cancel(id)
        }

        override fun getSubContent(uuid: String): Long {
            contentService?.let {
                val id: Long = it.getSubContent(Libs.get().appKey, Libs.get().channelId, uuid, object
                    : DataObserver<ModelResult<List<SubContent>>> {
                    override fun onResult(result: ModelResult<List<SubContent>>, requestCode: Long) {
                        if (result.isOk()) {
                            view?.onSubContentResult(uuid, ArrayList(result.data))
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

        fun getSubContentsWithCallback(contentResult: Content?, uuid: String, contentTYpe: String?) {
            if (contentResult != null) {
                contentService?.let { iContent ->
                    var single = false
                    var suuid: String? = uuid
                    if (Constant.CONTENTTYPE_PG.equals(contentTYpe) || Constant.CONTENTTYPE_CP.equals(contentTYpe)) {
                        view?.onContentResult(uuid, contentResult)
                        return
                    }
                    iContent.getSubContent(Libs.get().appKey, Libs.get().channelId, suuid!!,
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
                                        view?.let {
                                            it.onContentResult(uuid, contentResult)
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

        override fun getContent(uuid: String, autoSub: Boolean, contentType: String) {
            view?.let {
                if (it is LoadingView) it.onLoading()
            }
            contentService?.getContentInfo(Libs.get().appKey, Libs.get().channelId, uuid, false, object
                : DataObserver<ModelResult<Content>> {
                override fun onResult(result: ModelResult<Content>, requestCode: Long) {
                    if (result.isOk()) {
                        if (!autoSub) {
                            view?.onContentResult(uuid, result.data);
                        } else {
                            getSubContentsWithCallback(result.data, uuid, contentType)
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

        override fun getContent(uuid: String, autoSub: Boolean): Long {
            view?.let {
                if (it is LoadingView) it.onLoading()
            }
            contentService?.let {
                return it.getContentInfo(Libs.get().appKey, Libs.get().channelId, uuid, false,
                        object
                            : DataObserver<ModelResult<Content>> {
                            override fun onResult(result: ModelResult<Content>, requestCode: Long) {
                                if (result.isOk()) {
                                    if (!autoSub) {
                                        view?.onContentResult(uuid, result.data);
                                    } else {
                                        getSubContentsWithCallback(result.data, uuid, result
                                                .data?.contentType)
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
            return 0L
        }
    }
}