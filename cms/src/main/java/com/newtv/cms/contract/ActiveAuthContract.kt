package com.newtv.cms.contract

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IActiveAuth
import com.newtv.libs.Constant
import com.newtv.libs.Libs
import com.newtv.libs.bean.ActivateBean
import com.newtv.libs.bean.AuthBean
import com.newtv.libs.util.LogUtils
import com.newtv.libs.util.SPrefUtils
import com.newtv.libs.util.SystemUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         12:33
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
class ActiveAuthContract {

    interface View : ICmsView {

        fun authResult()

        fun activeResult()

        fun failed(type: Int, status: Int)
    }

    interface Presenter : ICmsPresenter {
        fun auth()

        fun active()
    }

    class ActiveAuthPresenter(context: Context, view: View) : CmsServicePresenter<View>(context, view), Presenter {
        private var num = 0

        private var activeAuthService: IActiveAuth? = null

        private var handler: Handler? = Handler(Handler.Callback { message ->
            when (message.what) {
                Constract.RETRY_ACTIVE -> active()
            }
            false
        })




        init {
            activeAuthService = getService<IActiveAuth>(CmsServicePresenter.SERVICE_ACTIVE_AUTH)
        }

        override fun destroy() {
            super.destroy()

            handler?.removeCallbacksAndMessages(null)
            handler = null
        }

        private fun onFailed(type: Int, status: Int) {
            if (status < Constract.LOCAL_EXCEPTION
                    && num < Constract.MAX_NUM * Constant.activateUrls.size && handler != null) {
                Constant.BASE_URL_ACTIVATE = Constant.activateUrls[num / Constract.MAX_NUM]
                val message = handler?.obtainMessage(Constract.RETRY_ACTIVE)
                handler?.sendMessageDelayed(message, Constract.TIME_ONE_SECOND.toLong())
                num++
            } else {
                view!!.failed(type, status)
            }
        }

        override fun auth() {
            activeAuthService?.let { service ->
                val authBean = AuthBean(SystemUtils.getMac(context),
                        Libs.get().appKey,
                        Libs.get().channelId,
                        Constant.UUID,
                        System.currentTimeMillis().toString() + "")
                var number = 0
                service.auth(authBean, object : DataObserver<String> {
                    override fun onResult(result: String, requestCode: Long) {
                        try {
                            val response = JSONObject(result)
                            val statusCode = response.getInt("statusCode")
                            if (1 == statusCode) {
                                val message = response.getString("message")
                                LogUtils.i("认证message=$message")
                                view?.authResult()
                            } else {
                                /**
                                 * 1 为成功
                                 * 500 为系统异常
                                 * 1001 为key不存在
                                 * 1002 为mac不正确或当前mac与uuid不匹配
                                 * 1003 为密文签名不正确
                                 * 1004 为APP被禁用
                                 */
                                if (1002 == statusCode && number++ < Constract.RETRY_NUMBER) {
                                    Constant.UUID = ""
                                    SPrefUtils.setValue(context.applicationContext,
                                            Constant.UUID_KEY, "")
                                    active()
                                    return
                                }
                                onFailed(Constract.AUTH, statusCode)
                            }
                        } catch (e: JSONException) {
                            LogUtils.e(e.toString())
                            onFailed(Constract.AUTH, Constract.JSON_EXCEPTION)
                        }

                    }

                    override fun onError(code: String?, desc: String?) {
                        onFailed(Constract.AUTH, Constract.NET_ERROR);
                    }
                })
            }
        }

        override fun active() {
            if (TextUtils.isEmpty(Constant.UUID)) {
                Constant.UUID = SPrefUtils.getValue(context,
                        Constant.UUID_KEY, "") as String
            }
            if (TextUtils.isEmpty(Constant.UUID)) {
                activeAuthService?.let { service ->

                    val activateBean = ActivateBean(SystemUtils
                            .getMac(context),
                            Libs.get().appKey,
                            Libs.get().channelId,
                            System.currentTimeMillis().toString() + "")
                    service.active(activateBean, object : DataObserver<String> {
                        override fun onResult(result: String, requestCode: Long) {
                            try {
                                val response = JSONObject(result)
                                val statusCode = response.getInt("statusCode")
                                if (1 == statusCode) {
                                    val obj = response.getJSONObject("response")
                                    val uuid = obj.getString("uuid")
                                    Constant.UUID = uuid
                                    SPrefUtils.setValue(context, Constant
                                            .UUID_KEY, uuid)
                                    LogUtils.i("app激活成功")
                                    view?.activeResult()
                                    auth()
                                } else {
                                    LogUtils.i("app激活失败")
                                    onFailed(Constract.ACTIVATE, statusCode)
                                }
                            } catch (e: JSONException) {
                                LogUtils.e(e.toString())
                                onFailed(Constract.ACTIVATE, Constract.JSON_EXCEPTION)
                            }

                        }

                        override fun onError(code: String?, desc: String?) {
                            onFailed(Constract.ACTIVATE, Constract.NET_ERROR);
                        }
                    })
                }
            } else {
                auth()
            }
        }

        object Constract {

            const val ACTIVATE = 1
            const val AUTH = 2

            val FAIL = 3
            val NET_ERROR = 4 // 异常
            internal val JSON_EXCEPTION = 5 //json解析错误
            val IO_EXCEPTION = 6 //io 异常
            const val NOT_SELF_DEVICE = 7 //不是合作终端
            val LOCAL_EXCEPTION = 10 //10以下是客户端定义的错误类型


            /**
             * 重试次数
             * 存在本地UUID错误的情况
             * 当检测到错误时重新激活获取UUID
             */
            val RETRY_NUMBER = 2
            internal val MAX_NUM = 3
            internal val TIME_ONE_SECOND = 200
            val RETRY_ACTIVE = 0x998

        }

    }
}
