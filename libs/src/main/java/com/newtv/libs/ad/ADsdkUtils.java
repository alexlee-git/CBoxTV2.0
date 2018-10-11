package com.newtv.libs.ad;

import android.annotation.SuppressLint;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.RxBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;

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


                        final ADHelper.AD mAd = ADHelper.getInstance().parseADString(Libs.get()
                                .getContext(), sb
                                .toString());

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
