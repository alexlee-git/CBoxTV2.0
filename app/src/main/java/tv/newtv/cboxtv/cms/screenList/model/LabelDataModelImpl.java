package tv.newtv.cboxtv.cms.screenList.model;


import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.newtv.cboxtv.cms.screenList.api.LabelApi;
import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;
import tv.newtv.cboxtv.cms.screenList.common.Common;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public class LabelDataModelImpl implements LabelDataModel {


    @Override
    public void requestLabelData(Map<String, Object> map, final DataCompleteListener listener) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)// 在此处添加拦截器即可，默认日志级别为BASIC
                .build();


        Retrofit builder = new Retrofit.Builder()
                .baseUrl(Common.BASE_DATA_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();


        Observable<ResponseBody> observable = builder.create(LabelApi.class).getData(map);


        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        Gson gson = new Gson();
                        if (responseBody!=null){
                            LabelDataBean dataBean = gson.fromJson(responseBody.string(), LabelDataBean.class);
                            Log.d("DataModelImpl2", "pageDataBean:" + dataBean);
                            if (dataBean != null)
                                listener.sendLabelData(dataBean);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }
}
