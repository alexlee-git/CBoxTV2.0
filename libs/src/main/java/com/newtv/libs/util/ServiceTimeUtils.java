package com.newtv.libs.util;

import android.text.TextUtils;

import com.newtv.libs.bean.TimeBean;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by TCP on 2018/5/23.
 */

public class ServiceTimeUtils {
    private static final String TAG = "ServiceTimeUtils";

    public interface TimeListener{

        void success(TimeBean timeBean);

        void fail();
    }


    public static void getServiceTime(final TimeListener listener){
//        NetClient.INSTANCE.getClockSyncApi()
//                .getClockData()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            String result = responseBody.string();
//                            if(!TextUtils.isEmpty(result)){
//                                TimeBean time = GsonUtil.fromjson(result, TimeBean.class);
//                                if("1".equals(time.getStatusCode())){
//                                    if(listener != null) {
//                                        listener.success(time);
//                                        return;
//                                    }
//                                }
//                            }
//                            if(listener != null){
//                                listener.fail();
//                            }
//                        } catch (IOException e) {
//                            LogUtils.e(e.toString());
//                            if(listener != null){
//                                listener.fail();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if(listener != null){
//                            listener.fail();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }
}
