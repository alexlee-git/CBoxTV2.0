package com.newtv.cms

import com.newtv.cms.service.*
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

    init {
        if (BuildConfig.DEBUG) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient = okhttp3.OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()!!

    private val retrofit = retrofit2.Retrofit.Builder()
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl("http://111.32.138.57:81")
            .build()

    val nav: INavRetro by lazy { retrofit.create(INavRetro::class.java) }
    val content: IContentRetro by lazy { retrofit.create(IContentRetro::class.java) }
    val page: IPageRetro by lazy { retrofit.create(IPageRetro::class.java) }
    val category:ICategoryRetro by lazy { retrofit.create(ICategoryRetro::class.java) }
    val corner:ICornerRetro by lazy { retrofit.create(ICornerRetro::class.java) }
    val splash:ISplashRetro by lazy { retrofit.create(ISplashRetro::class.java) }
    val tv:IHostRetro by lazy { retrofit.create(IHostRetro::class.java) }
    val program:ITvProgramRetro by lazy { retrofit.create(ITvProgramRetro::class.java) }
    val filter:IFilterRetro by lazy { retrofit.create(IFilterRetro::class.java) }

}