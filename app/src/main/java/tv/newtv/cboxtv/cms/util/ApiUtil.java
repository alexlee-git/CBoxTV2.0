//package tv.newtv.cboxtv.cms.util;
//
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//import tv.newtv.cboxtv.Constant;
//
///**
// * Created by lixin on 2018/1/11.
// */
//
//public class ApiUtil {
//
//    private static ApiUtil mInstance;
//
//    private OkHttpClient mOkHttpClient;
//    private GsonConverterFactory mGsonConverterFactory;
//    private RxJava2CallAdapterFactory mRxJavaCallAdapterFactory;
//    private IActivateAuthApi iActivateAuthApi;
//
//    private ApiUtil() {
//        mGsonConverterFactory = GsonConverterFactory.create();
//        mRxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
//
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        mOkHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build();
//    }
//
//    public static ApiUtil getInstance() {
//        if (mInstance == null) {
//            synchronized (ApiUtil.class) {
//                if (mInstance == null) {
//                    mInstance = new ApiUtil();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    public IActivateAuthApi getActivateAuthApi(){
//        if(iActivateAuthApi == null) {
//            Retrofit retrofit = new Retrofit.Builder().client(mOkHttpClient)
//                    .addCallAdapterFactory(mRxJavaCallAdapterFactory)
//                    .addConverterFactory(mGsonConverterFactory)
//                    .baseUrl(Constant.BASE_URL_ACTIVATE)
//                    .build();
//            iActivateAuthApi = retrofit.create(IActivateAuthApi.class);
//        }
//
//        return iActivateAuthApi;
//    }
//}
