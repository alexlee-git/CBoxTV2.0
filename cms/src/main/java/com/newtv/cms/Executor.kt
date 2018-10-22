package com.newtv.cms

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:27
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal class Executor<T>(val observable: Observable<ResponseBody>,
                           val type: Type?,
                           val callback: IExecutor<T>?
) {

    var isCancel: Boolean = false //是否已经退出请求

    interface IExecutor<T> {
        fun onCancel(executor: Executor<T>)
    }

    var mObserver: DataObserver<T>? = null
    var mDisposable: Disposable? = null

    fun observer(observer: DataObserver<T>): Executor<T> {
        mObserver = observer
        return this
    }

    fun cancel() {
        isCancel = true
        mDisposable?.let {
            if (!it.isDisposed) it.dispose()
            mDisposable = null
        }
        mObserver = null
        callback?.onCancel(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun execute() {
        observable
                .retryWhen(RetryWithDelay(3, 2, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onError(e: Throwable) {
                        mObserver?.onError(e.message)
                        cancel()
                    }

                    override fun onComplete() {
                        cancel()
                    }


                    override fun onNext(t: ResponseBody) {
                        if (isCancel) return
                        try {
                            if (type != null) {
                                val result = Gson().fromJson<T>(t.string(), type)
                                mObserver?.onResult(result)
                            } else {
                                mObserver?.onResult(t.string() as T)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mObserver?.onError(e.message)
                        }
                    }
                })
    }
}