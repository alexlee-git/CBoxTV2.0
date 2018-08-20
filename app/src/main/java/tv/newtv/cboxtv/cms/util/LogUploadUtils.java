package tv.newtv.cboxtv.cms.util;

import android.annotation.SuppressLint;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.logsdk.logSDK;
import tv.newtv.cboxtv.Constant;

public class LogUploadUtils {
    private static String TAG = "logsdk";

    private static boolean isUpload = true;//日志初始化和上传的开关
    private static logSDK logUpload;//日志上传对象
    private static boolean isToInit = true;
    private static boolean isinit = false;

    @SuppressLint("CheckResult")
    public static boolean initSDk() {

        if (isinit) {
            return isinit;
        }

        //日志上传sdk初始化
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                if(isinit){
                    e.onNext(true);
                    return;
                }
                logUpload = logSDK.getInstance();
                Boolean b = logUpload.sdkInit(Constant.LOG_ADDR, "", Constant.UUID, Constant
                        .CHANNEL_CODE, Constant.APPKEY);
                e.onNext(b);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean value) throws Exception {
                        isinit = value;
                        Log.i(TAG, "logsdk初始化结果：" + value);
                    }
                });

        return isinit;
    }

    /**
     * @param type
     * @param content
     * @return 0成功 <0失败
     */
    @SuppressLint("CheckResult")
    public static void uploadLog(final int type, final String content) {
        int result = -1;
        RxBus.get().post(Constant.INIT_SDK, Constant.INIT_LOGSDK);
        if (!isUpload) {
            return;
        }
        if (null != logUpload) {

            //日志上传sdk初始化
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    Log.e("logsdk", "日志报上内容=" + content + ",type=" + type);
                    e.onNext(logUpload.logUpload(type, content));
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer value) throws Exception {
                            Log.e(TAG, "日志上传结果result=：" + value);
                        }
                    });

        }
    }

    public static boolean isInit() {
        return isinit;
    }
}
