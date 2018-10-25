package com.newtv.cms

import com.google.gson.Gson
import com.newtv.libs.util.LogUtils
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
                           val model: String,
                           val callback: IExecutor<T>?) {

    private var mID: Long = 0;
    var isCancel: Boolean = false //是否已经退出请求

    init {
        mID = System.currentTimeMillis()
        LogUtils.d(Companion.TAG, "[ $TAG create from=$model id=$mID ]")
    }

    internal fun getID(): Long {
        return mID
    }

    interface IExecutor<T> {
        fun onCancel(executor: Executor<T>)
    }

    var mObserver: DataObserver<T>? = null
    var mDisposable: Disposable? = null

    internal fun observer(observer: DataObserver<T>): Executor<T> {
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

        LogUtils.d(Companion.TAG, "[ $TAG cancel from=$model id=$mID ]")
    }

    @Suppress("UNCHECKED_CAST")
    internal fun execute() {
        observable
                .retryWhen(RetryWithDelay(5, 5, TimeUnit.SECONDS))
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
                                mObserver?.onResult(result, mID)
                            } else {
                                mObserver?.onResult(t.string() as T, mID)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mObserver?.onError(e.message)
                        }
                    }
                })
    }

    companion object {
        const val TAG = "Executor"
    }
}