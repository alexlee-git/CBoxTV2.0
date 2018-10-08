package tv.newtv.cboxtv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

//import com.tencent.bugly.crashreport.CrashReport;


import com.bumptech.glide.Glide;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.NetworkManager;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.cms.util.SystemUtils;
import tv.newtv.cboxtv.cms.util.YSLogUtils;
import tv.newtv.cboxtv.uc.db.DataSupport;
import tv.newtv.cboxtv.utils.FileUtil;
import tv.newtv.cboxtv.utils.PicassoBuilder;
//import tv.newtv.key.KeyHelper;

/**
 * Created by lixin on 2018/1/11.
 */

public class LauncherApplication extends MultiDexApplication {

    public static Context AppContext = null;

    private boolean isAdSDKInit = false;//广告初始化结果

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (BuildConfig.DEBUG) {
//            MultiDex.install(this);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();

        //解决Rxjava的onError()异常
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("TAG","throw test");
            }
        });


        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this);
        }

        AppContext = this.getApplicationContext();
        //KeyHelper.init(getApplicationContext());
        initADsdk();
        DataSupport.init(getApplicationContext());
        initBugly();
        Observable<String> mBackNavObservable = RxBus.get().register(Constant.INIT_SDK);
        mBackNavObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String value) throws Exception {
                        if (Constant.INIT_ADSDK.equals(value)) {
                            initADsdk();
                        } else if (Constant.INIT_LOGSDK.equals(value)) {
                            LogUploadUtils.initSDk();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(throwable);
                    }
                });
        PicassoBuilder.init(this);

        Log.e(Constant.TAG, "Application onCreate : ");
        DisplayUtils.init(this);
        NetworkManager.getInstance().init(getApplicationContext());

        YSLogUtils.getInstance(getApplicationContext()).initTracker();//央视网日志初始化
    }

    /**
     * 初始化bugly
     */
    private void initBugly() {

//        CrashReport.initCrashReport(getApplicationContext(), "e8a44dd463", false);

    }

    @SuppressLint("CheckResult")
    private void initADsdk() {
        //日志上传sdk初始化
        if (isAdSDKInit) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                e.onNext(AdSDK.getInstance().init(Constant.BASE_URL_AD,
                        SystemUtils.getMac(AppContext),
                        Constant.APP_KEY, Constant.CHANNEL_ID,
                        FileUtil.getCacheDirectory(getApplicationContext(), "ad_cache")
                                .getAbsolutePath()));//广告初始化
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean value) throws Exception {
                        isAdSDKInit = value;
                        Log.e("adASD", "广告初始化结果=" + value);
                    }
                });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if(level == TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory();
            PicassoBuilder.getBuilder().clear();
        }
        Glide.get(this).trimMemory(level);

        LogUtils.d(String.format(Locale.getDefault(), "trimmemory level=%d", level));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide.get(this).clearMemory();
        PicassoBuilder.getBuilder().clear();
    }
}
