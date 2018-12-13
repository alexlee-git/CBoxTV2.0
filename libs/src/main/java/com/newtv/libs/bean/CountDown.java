package com.newtv.libs.bean;

import com.newtv.libs.MainLooper;
import com.newtv.libs.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.bean
 * 创建事件:         17:54
 * 创建人:           weihaichao
 * 创建日期:          2018/4/19
 */
public class CountDown {

    private int mTime;
    private Listen mListen;
    private Observable observable;
    private Disposable mDisposable;

    public CountDown(int seconds) {
        mTime = seconds;
        observable = Observable.interval(1000, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        mListen = null;
        observable = null;
        cancel();
    }

    public void listen(Listen listen) {
        mListen = listen;
    }

    public void start() {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        mTime--;
                        MainLooper.get().post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListen != null) mListen.onCount(mTime);
                            }
                        });
                        if (mTime == 0) {
                            MainLooper.get().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mListen != null) mListen.onComplete();
                                }
                            });
                            CountDown.this.cancel();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void unSubscribe(){
        if(mDisposable != null){
            if(!mDisposable.isDisposed()){
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }

    public void cancel() {
        LogUtils.e("CountDown", "cancel countdown...");
        unSubscribe();
        if (mTime != 0) {
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (mListen != null) mListen.onCancel();
                    mListen = null;
                }
            });
        }
    }

    public interface Listen {
        void onCount(int time);

        void onComplete();

        void onCancel();
    }
}
