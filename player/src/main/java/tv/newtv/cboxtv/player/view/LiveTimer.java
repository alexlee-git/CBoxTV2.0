package tv.newtv.cboxtv.player.view;

import com.newtv.libs.util.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.player.model.LiveInfo;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         10:21
 * 创建人:           weihaichao
 * 创建日期:          2018/11/2
 */
class LiveTimer {

    private static final String TAG = "LiveTimer";
    private LiveInfo mLiveInfo;
    private Disposable mDisposable;
    private boolean mPause = false;
    private LiveTimerCallback mLiveTimerCallback;

    void setLiveInfo(LiveInfo liveInfo) {
        mLiveInfo = liveInfo;
        start();
    }

    boolean isRunning() {
        return mDisposable != null && !mDisposable.isDisposed();
    }

    void setCallback(LiveTimerCallback callback) {
        mLiveTimerCallback = callback;
    }

    /**
     * 开始计时
     */
    private void start() {
        if(mPause){
            mPause = false;
            return;
        }
        mDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.e(TAG,  mLiveInfo.toString());
                        //TODO 直播时间结束
                        boolean complete = mLiveInfo.isComplete();
                        if(complete){
                            cancel();
                        }
                        if (mLiveTimerCallback != null) {
                            mLiveTimerCallback.onChange(mLiveInfo.getCurrentTimeStr(),
                                    mLiveInfo.getStartTimeStr(),
                                    mLiveInfo.getEndTimeStr(),mLiveInfo.isComplete()
                            );
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

    void pause(){
        mPause = true;
    }

    void cancel() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }

    interface LiveTimerCallback {
        void onChange(String current, String start, String end, boolean isComplete);
    }


}
