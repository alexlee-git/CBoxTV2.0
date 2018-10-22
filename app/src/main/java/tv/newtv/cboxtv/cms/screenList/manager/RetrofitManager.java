package tv.newtv.cboxtv.cms.screenList.manager;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.newtv.cboxtv.cms.screenList.common.Common;

/**
 * Created by 冯凯 on 2018/4/18.
 */

public class RetrofitManager {

    private Retrofit retrofit;


    private RetrofitManager(String url) {
        this.retrofit = getRetrofit(url);

    }

    static class InnerRetrofit {

        private static RetrofitManager manager = new RetrofitManager(Common.BASE_URL);

    }

    public static RetrofitManager getRetrofitManager() {
        return InnerRetrofit.manager;
    }



    public static Retrofit getRetrofit(String url) {
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

        //创建获取retrofit对象的方法

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                ;



        return builder.build();
    }

    //如果网址改变的情况下
    public static RetrofitManager getRetrofitManager(String baseUrl) {
        return new RetrofitManager(baseUrl);
    }

    public <T> T create(Class<T> tClass) {
        return retrofit.create(tClass);

    }

}
