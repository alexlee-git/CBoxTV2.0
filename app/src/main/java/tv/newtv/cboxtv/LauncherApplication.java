package tv.newtv.cboxtv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.FileUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.PicassoBuilder;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SystemUtils;
import com.newtv.libs.util.YSLogUtils;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.player.Player;
import tv.newtv.cboxtv.player.PlayerObserver;
import tv.newtv.cboxtv.utils.DBUtil;

//import com.tencent.bugly.crashreport.CrashReport;

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
        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this);
        }

        AppContext = this.getApplicationContext();
        Libs.init(this, BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, BuildConfig.FLAVOR);

        Player.get().attachObserver(new PlayerObserver() {
            @Override
            public void onFinish(Content playInfo, int index, int position) {
                if(index == 0 && position == 0) return;
                DBUtil.addHistory(playInfo, index, position, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if(code == 0){
                            LogUtils.e("写入历史记录成功");
                        }
                    }
                });
            }

            @Override
            public void onExitApp() {

            }

            @Override
            public Activity getCurrentActivity() {
                return ActivityStacks.get().getCurrentActivity();
            }

            @Override
            public Intent getPlayerActivityIntent() {
                return new Intent(LauncherApplication.this,NewTVLauncherPlayerActivity.class);
            }
        });


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

        YSLogUtils.getInstance(getApplicationContext()).initTracker();//央视网日志初始化
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(Constant.TAG, "accept: "+throwable);
            }
        });
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
                        BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID,
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

        if (level == TRIM_MEMORY_UI_HIDDEN) {
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
