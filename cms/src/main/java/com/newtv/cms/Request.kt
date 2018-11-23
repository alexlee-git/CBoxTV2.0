package com.newtv.cms

import com.newtv.cms.service.*
import com.newtv.libs.Constant
import com.newtv.libs.HeadersInterceptor
import com.newtv.libs.util.HttpsUtils
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         11:11
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */
internal object Request {
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
            .baseUrl(Constant.BASE_URL_NEW_CMS)
            .build()

    val nav: INavRetro by lazy { retrofit.create(INavRetro::class.java) }
    val content: IContentRetro by lazy { retrofit.create(IContentRetro::class.java) }
    val page: IPageRetro by lazy { retrofit.create(IPageRetro::class.java) }
    val person: IPersonRetro by lazy { retrofit.create(IPersonRetro::class.java) }
    val category: ICategoryRetro by lazy { retrofit.create(ICategoryRetro::class.java) }
    val corner: ICornerRetro by lazy { retrofit.create(ICornerRetro::class.java) }
    val splash: ISplashRetro by lazy { retrofit.create(ISplashRetro::class.java) }
    val program: ITvProgramRetro by lazy { retrofit.create(ITvProgramRetro::class.java) }
    val filter: IFilterRetro by lazy { retrofit.create(IFilterRetro::class.java) }
    val upVersion: IUpVersionRetro by lazy { retrofit.create(IUpVersionRetro::class.java) }
    val clock: IClockRetro by lazy { retrofit.create(IClockRetro::class.java) }
    val bootGuide: IBootGuideRetro by lazy { retrofit.create(IBootGuideRetro::class.java) }
    val activeAuth: IActiveAuthRetro by lazy { retrofit.create(IActiveAuthRetro::class.java) }
    val playChk: IPlayChkRetro by lazy { retrofit.create(IPlayChkRetro::class.java) }
    val search: ISearchRetro by lazy { retrofit.create(ISearchRetro::class.java) }
    val alternate: IAlternateRetro by lazy { retrofit.create(IAlternateRetro::class.java) }
    val default: IDefaultRetro by lazy { retrofit.create(IDefaultRetro::class.java) }
    val UserCenterMemberInfoApi by lazy { retrofit.create(IUserCenterMemberInfoApi::class.java) }

}