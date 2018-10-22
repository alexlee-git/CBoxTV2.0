package com.newtv.cms.contract

import android.content.Context
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IContent
import com.newtv.cms.api.IPerson
import com.newtv.cms.bean.ModelResult
import com.newtv.cms.bean.SubContent
import com.newtv.libs.Libs
import java.util.ArrayList

/**
 * Created by linzy on 2018/10/18.
 */
class PersonDetailsConstract {
    interface View : ICmsView {
        fun setPersonTvList(contents : ArrayList<SubContent>?)
        fun setPersonProgramList(contents : ArrayList<SubContent>?)
    }

    interface Presenter : ICmsPresenter {
        /**
         * 获取主持人主持的电视栏目列表
         */
        fun getPersonTvList(uuid : String)
        /**
         * 获取主持人相关的节目
         */
        fun getPersonProgramList(uuid : String)
    }

    class PersonDetailPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view), Presenter{
        override fun getPersonTvList(uuid: String) {
            val content : IPerson? = getService<IPerson>(SERVICE_PERSON_DETAIL)

            content?.getPersonTvList(Libs.get().appKey, Libs.get().channelId, uuid, object
                : DataObserver<ModelResult<ArrayList<SubContent>>> {
                override fun onResult(result: ModelResult<ArrayList<SubContent>>) {
                    if (result.isOk()) {
                        view?.setPersonProgramList(result.data)
                    } else {
                        view?.onError(context, result.errorMessage)
                    }
                }

                override fun onError(desc: String?) {
                    view?.onError(context, desc)
                }
            })
        }

        override fun getPersonProgramList(uuid: String) {
            val content : IPerson? = getService<IPerson>(SERVICE_PERSON_DETAIL)

            content?.getPersonProgramList(Libs.get().appKey, Libs.get().channelId, uuid, object
                : DataObserver<ModelResult<ArrayList<SubContent>>> {
                override fun onResult(result: ModelResult<ArrayList<SubContent>>) {
                    if (result.isOk()) {
                        view?.setPersonTvList(result.data)
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
