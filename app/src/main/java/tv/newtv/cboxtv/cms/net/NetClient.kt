package tv.newtv.cboxtv.cms.net

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import tv.newtv.cboxtv.BuildConfig
import com.newtv.libs.Constant
import com.newtv.libs.util.HttpsUtils
import java.util.concurrent.TimeUnit

/**
 * Created by cuiwj on 2018/4/16.
 */

object NetClient {
    private val logInterceptor = HttpLoggingInterceptor()
    private val headersInterceptor = HeadersInterceptor();

    init {
        if (BuildConfig.DEBUG) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
    }

    private val sslFactory = HttpsUtils.getSslSocketFactory(null, null, null)
    private val httpClient = okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslFactory.sSLSocketFactory, sslFactory.trustManager)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(headersInterceptor)
            .addInterceptor(logInterceptor)
            .build()!!

    private val retrofit = retrofit2.Retrofit.Builder()
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(com.newtv.libs.Constant.BASE_URL_CMS)
            .build()


    val clockSyncApi by lazy { retrofit.create(IClockSyncApi::class.java) }
    val pageDataApi by lazy { retrofit.create(IPageDataApi::class.java) }
    val programSeriesInfoApi by lazy { retrofit.create(IProgramSeriesInfoApi::class.java) }
    val detailsPageApi by lazy { retrofit.create(IDetailsPageApi::class.java) }
    val listPageApi by lazy { retrofit.create(IListPageApi::class.java) }
    val searchRecommendApi by lazy { retrofit.create(ISearchRecommendApi::class.java) }
    val searchResultApi by lazy { retrofit.create(ISearchResultApi::class.java) }
    val specialApi by lazy { retrofit.create(ISpecialApi::class.java) }
    val superScriptApi by lazy { retrofit.create(ISuperScriptApi::class.java) }
    val bootGuideApi by lazy { retrofit.create(IBootGuideApi::class.java) }
    val activateAuthApi by lazy { retrofit.create(IActivateAuthApi::class.java) }
    val upVersion by lazy { retrofit.create(UpVersionApi::class.java) }
    val MenuApi by lazy { retrofit.create(IMenuApi::class.java)}
    val playPermissionCheckApi by lazy { retrofit.create(IPlayPermissionCheckApi::class.java)}
}

