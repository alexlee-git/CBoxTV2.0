package com.newtv.cms.contract

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.libs.Constant
import com.newtv.libs.Libs
import com.newtv.libs.ad.ADConfig
import com.newtv.libs.ad.ADHelper
import com.newtv.libs.util.LogUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import tv.icntv.adsdk.AdSDK

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.contract
 * 创建事件:         13:06
 * 创建人:           weihaichao
 * 创建日期:          2018/10/17
 */
class AdContract {

    interface Presenter : ICmsPresenter {
        val adItem: ADHelper.AD.ADItem?
        val ad: ADHelper.AD?
        val isAdHasEvent: Boolean
        fun getAdByType(adType: String?, adLoc: String?, flag: String?, extends: HashMap<*, *>?)
        fun getAdByType(adType: String?, adLoc: String?, flag: String?, extends: HashMap<*, *>?, callback: Callback?)

        fun getAdByChannel(adType: String?, adLoc: String?, flat: String?, firstChannel: String?,
                           secondChannel: String?, topicId: String?, extends: HashMap<*, *>?)

        fun getAdByChannel(adType: String?, adLoc: String?, flag: String?, firstChannel: String?,
                           secondChannel: String?, topicId: String?, extends: HashMap<*, *>?,
                           callback: Callback?)
    }

    interface Callback {
        fun showAd(type: String?, url: String?, extends: HashMap<*, *>?)
    }

    interface View : ICmsView {
        fun showAd(type: String?, url: String?, extends: HashMap<*, *>?)
        fun updateTime(total: Int, left: Int)
        fun complete()
    }

    class AdPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view),
            Presenter {

        override fun getAdByType(adType: String?, adLoc: String?, flag: String?, extends: HashMap<*, *>?, callback: Callback?) {
            getAdWithType(adType, adLoc, flag, extends, callback)
        }

        override fun getAdByChannel(adType: String?, adLoc: String?, flag: String?, firstChannel:
        String?, secondChannel: String?, topicId: String?, extends: HashMap<*,
                        *>?, callback: Callback?) {
            getAdWithChannel(adType, adLoc, flag, firstChannel, secondChannel, topicId, extends,
                    callback)
        }

        fun getAdWithType(adType: String?, adLoc: String?, flag: String?, extends: HashMap<*, *>?,
                         callback: Any?) {
            LogUtils.e("AdConstract", "getAdByType")
            val sb = StringBuffer()
            Observable.create(ObservableOnSubscribe<Int> { e ->
                e.onNext(AdSDK.getInstance().getAD(adType, null, null, adLoc, null, null, sb))
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Int> {
                        override fun onSubscribe(d: Disposable) {
                            mDisposable = d
                        }

                        override fun onNext(integer: Int) {
                            parseAdResult(sb, extends, callback)
                        }

                        override fun onError(e: Throwable) {
                            view?.showAd(null, null, extends)
                        }

                        override fun onComplete() {
                        }
                    })
        }

        fun getAdWithChannel(adType: String?, adLoc: String?, flag: String?, firstChannel: String?,
                           secondChannel: String?, topicId: String?, extends: HashMap<*,
                        *>?, callback: Any?) {

            LogUtils.e("AdConstract", "getAdByChannel")
            val sb = StringBuffer()
            Observable.create(ObservableOnSubscribe<Int> { e ->
                val config = ADConfig.getInstance()
                val stringBuilder = StringBuilder()
                addExtend(stringBuilder, "panel", firstChannel)
                addExtend(stringBuilder, "secondpanel", secondChannel)
                addExtend(stringBuilder, "topic", topicId)
                addExtend(stringBuilder, "secondcolumn", config.secondColumnId)
                e.onNext(AdSDK.getInstance().getAD(adType, config.columnId, config
                        .seriesID, adLoc, null, stringBuilder.toString(), sb))
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Int> {

                        override fun onSubscribe(d: Disposable) {
                            mDisposable = d
                        }

                        override fun onNext(integer: Int) {
                            parseAdResult(sb, extends, callback)
                        }

                        override fun onError(e: Throwable) {
                            view?.showAd(null, null, extends)
                        }

                        override fun onComplete() {
                        }

                    })
        }


        override fun getAdByChannel(adType: String?, adLoc: String?, flag: String?, firstChannel:
        String?, secondChannel: String?, topicId: String?, extends: HashMap<*, *>?) {
            getAdWithChannel(adType, adLoc, flag, firstChannel, secondChannel, topicId, extends, view)
        }

        @SuppressLint("CheckResult")
        override fun getAdByType(adType: String?, adLoc: String?, flag: String?, extends: HashMap<*,
                *>?) {
            getAdWithType(adType, adLoc, flag, extends, view)
        }

        private var mDisposable: Disposable? = null
        override var ad: ADHelper.AD? = null
        override var adItem: ADHelper.AD.ADItem? = null

        override val isAdHasEvent: Boolean
            get() = !(adItem == null
                    || TextUtils.isEmpty(adItem!!.eventType)
                    || Constant.EXTERNAL_OPEN_URI != adItem!!.eventType
                    || TextUtils.isEmpty(adItem!!.eventContent))

        override fun destroy() {
            super.destroy()

            if (ad != null) {
                ad!!.cancel()
                ad = null
            }
            adItem = null

            if (mDisposable != null) {
                if (!mDisposable!!.isDisposed) {
                    mDisposable!!.dispose()
                }
                mDisposable = null
            }
        }


        /**
         * @param buffer 广告请求返回的结果
         * @param extends 附加数据
         * @param callback 回调方法
         */
        private fun parseAdResult(buffer: StringBuffer, extends: HashMap<*, *>?, callback: Any?) {

            LogUtils.e("AdConstract", "adResult=${buffer.toString()}")

            ad = ADHelper.getInstance().parseADString(Libs.get()
                    .context, buffer.toString())

            if (ad == null) {
                callback?.let {

                    if (it is View) {
                        it.showAd(null, null, extends)
                    } else if (it is Callback) {
                        it.showAd(null, null, extends)
                    }
                }
                return
            }
            Log.e("AdHelper", "显示:" + ad!!)
            ad!!.setCallback(object : ADHelper.ADCallback {
                override fun showAd(type: String, url: String) {
                    callback?.let {
                        if (it is View) {
                            it.showAd(type, url, extends)
                        } else if (it is Callback) {
                            it.showAd(type, url, extends)
                        }
                    }
                }

                override fun showAdItem(adItem: ADHelper.AD.ADItem) {
                    this@AdPresenter.adItem = adItem
                }

                override fun updateTime(total: Int, left: Int) {
                    callback?.let {
                        if (it is View) {
                            it.updateTime(total, left)
                        }
                    }
                }

                override fun complete() {
                    callback?.let {
                        if (it is View) {
                            it.complete()
                        }
                    }
                }
            }).start()
        }

        private fun addExtend(result: StringBuilder, key: String, value: String?) {
            if (TextUtils.isEmpty(value)) {
                return
            }
            if (TextUtils.isEmpty(result)) {
                result.append(key).append("=").append(value)
            } else {
                result.append("&").append(key).append("=").append(value)
            }
        }
    }

}
