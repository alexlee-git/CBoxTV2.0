package com.newtv.cms;

import java.io.IOException;

import okhttp3.Response;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         14:40
 * 创建人:           weihaichao
 * 创建日期:          2018/12/12
 */
public class ResponseInterceptor implements okhttp3.Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        okhttp3.Response response = chain.proceed(request);

        return response.newBuilder().code(200).build();
    }
}
