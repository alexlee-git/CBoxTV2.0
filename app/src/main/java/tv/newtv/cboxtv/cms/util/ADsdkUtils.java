package tv.newtv.cboxtv.cms.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.cms.ad.JsonParse;
import tv.newtv.cboxtv.cms.ad.model.AdInfo;
import tv.newtv.cboxtv.cms.ad.model.AdInfos;
import tv.newtv.cboxtv.cms.ad.model.MaterialInfo;
import tv.newtv.cboxtv.cms.details.view.ADSdkCallback;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.utils.ADHelper;

/**
 * Created by Administrator on 2018/5/1.
 */

public class ADsdkUtils {

    @SuppressLint("CheckResult")
    public static void getAD(final String adType, final String adLoc, int flag, final ADSdkCallback callback) {

//        if (BuildConfig.DEBUG){
//            callback.showAd(null,null);
//            return;
//        }
        RxBus.get().post(Constant.INIT_SDK,Constant.INIT_ADSDK);

        final StringBuffer sb = new StringBuffer();

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(AdSDK.getInstance().getAD(adType, null, null, adLoc, null, null, sb));
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {


                        final ADHelper.AD mAd = ADHelper.getInstance().parseADString(LauncherApplication
                                .AppContext, sb.toString());

                        Log.e("AdHelper", "显示:" + mAd);
                        if (mAd==null){
                            callback.showAd(null,null);
                            return;
                        }
                        callback.AdPrepare(mAd);
                        mAd.setCallback(new ADHelper.ADCallback() {
                            @Override
                            public void showAd(String type, String url) {
                                callback.showAd(type,url);
                            }

                            @Override
                            public void showAdItem(ADHelper.AD.ADItem adItem) {
                                callback.showAdItem(adItem);
                            }

                            @Override
                            public void updateTime(int total, int left) {
//
                                callback.updateTime(total,left);
                            }

                            @Override
                            public void complete() {
                                callback.complete();
                            }
                        }).start();

                    }
                });

    }



}
