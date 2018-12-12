package tv.newtv.cboxtv.player.view;

import com.newtv.libs.util.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         10:21
 * 创建人:           weihaichao
 * 创建日期:          2018/11/2
 */
class PlayerTimer {

    private static final String TAG = "PlayerTimer";
    private Disposable mDisposable;
    private PlayerTimerCallback mLiveTimerCallback;
    private Observable<Long> mObservale;
    private int keepLookSeconds = 0;

    PlayerTimer() {

    }

    boolean isRunning() {
        return mDisposable != null && !mDisposable.isDisposed();
    }

    void setCallback(PlayerTimerCallback callback) {
        mLiveTimerCallback = callback;
    }

    /**
     * 开始计时
     */
    public void start() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            return;
        }

        if(mObservale == null){
            mObservale = Observable.interval(1000, TimeUnit.MILLISECONDS);
        }

        mDisposable = mObservale.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                keepLookSeconds += 1;
                                if (mLiveTimerCallback != null) {
                                    mLiveTimerCallback.onChange(keepLookSeconds);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e(TAG, "interval exception = " + throwable.getMessage());
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e(TAG, "interval complete");
                            }
                        });
    }

    void cancel() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
        mObservale = null;
    }

    public void reset() {
        keepLookSeconds = 0;
    }

    interface PlayerTimerCallback {
        void onChange(int currentSecond);
    }


}
