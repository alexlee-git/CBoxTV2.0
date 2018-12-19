package com.newtv.cms

import android.content.Context
import android.text.TextUtils
import com.newtv.cms.bean.Alternate
import com.newtv.cms.contract.AlternateContract
import com.newtv.cms.util.CmsUtil
import com.newtv.libs.util.LogUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.observer
 * 创建事件:         10:06
 * 创建人:           weihaichao
 * 创建日期:          2018/12/17
 */
class AlternateRefresh(context: Context, alterCallback: AlternateCallback?)
    : AlternateContract.Callback {

    interface AlternateCallback {
        fun onChange(id:String,title: Alternate)
        fun onError(id:String,code: String?, desc: String?)
    }

    override fun onFailed(cid: String, code: String?, desc: String?) {
        if (!TextUtils.equals(mId, cid)) return
        mCallback?.onError(cid,code, desc)
    }

    override fun onAlternateResult(cid: String, alternates: List<Alternate>) {
        if (!TextUtils.equals(mId, cid)) return
        val index: Int = CmsUtil.binarySearch(alternates, System.currentTimeMillis(),
                0, alternates.size - 1)
        if (index > 0) {
            val alternate: Alternate = alternates.get(index)
            mCallback?.onChange(cid,alternate)
        }
    }

    private var mId: String? = null
    private var mCallback: AlternateCallback? = null
    private var mPresenter: AlternateContract.Presenter? = null
    private var mObserver:Observable<Long>? = null
    private var mDisposable:Disposable? = null

    init {
        LogUtils.d("AlternateRefresh","new instance()")
        mPresenter = AlternateContract.AlternatePresenter(context, null)
        mCallback = alterCallback
        mObserver = Observable.interval(5,TimeUnit.MINUTES)//五分钟刷新一次轮播数据
        mObserver!!.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long>{
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: Long) {
                        refresh()
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    fun detach() {
        if(mDisposable != null){
            if(!mDisposable!!.isDisposed){
                mDisposable!!.dispose()
            }
            mDisposable = null
        }
        mPresenter?.stop()
        mId = null
        mCallback = null
        mObserver = null
    }

    fun refresh(){
        if(!TextUtils.isEmpty(mId)){
            mPresenter?.requestAlternateWithCallback(mId!!, this)
        }
    }

    fun equals(id:String):Boolean{
        return TextUtils.equals(mId,id)
    }

    fun attach(alternateId: String) {
        if (TextUtils.equals(alternateId, mId)) {
            return
        }
        if (mId != null) {
            mPresenter?.stop()
        }
        mId = alternateId
        if (!TextUtils.isEmpty(mId)) {
            mPresenter?.requestAlternateWithCallback(mId!!, this)
        } else {
            onFailed(alternateId, CmsErrorCode.APP_ERROR_CONTENT_ID_EMPTY, "ContentID is Empty")
        }
    }
}