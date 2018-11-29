package com.newtv.libs;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TCP on 2018/5/8.
 */

public class HeadersInterceptor implements okhttp3.Interceptor {
    public static final String CMS = "CMS";
    public static final String SERVER_TIME = "SERVER_TIME";
    public static final String VERSION_UP = "VERSION_UP";
    public static final String AD = "AD";
    public static final String IS_ORIENTED = "IS_ORIENTED";
    public static final String LOG = "LOG";
    public static final String ACTIVATE = "ACTIVATE";
    public static final String ACTIVATE2 = "ACTIVATE2";
    public static final String ACTIVATE3 = "ACTIVATE3";
    public static final String CDN = "CDN";
    public static final String DYNAMIC_KEY = "DYNAMIC_KEY";
    public static final String SEARCH = "SEARCH";
    public static final String PERMISSTION_CHECK = "PERMISSTION_CHECK";
    public static final String USER = "USER";
    public static final String PAY = "PAY";
    public static final String USER_BEHAVIOR = "USER_BEHAVIOR";
    public static final String PRODUCT = "PRODUCT";
    public static final String PAGE_MEMBER = "PAGE_MEMBER";
    public static final String PAGE_COLLECTION = "PAGE_COLLECTION";
    public static final String PAGE_SUBSCRIPTION = "PAGE_SUBSCRIPTION";
    public static final String PAGE_USERCENTER = "PAGE_USERCENTER";
    public static final String HTML_PATH_HELPER = "HTML_PATH_HELPER";
    public static final String HTML_PATH_ABOUT_US = "HTML_PATH_ABOUT_US";
    public static final String HTML_PATH_MEMBER_PROTOCOL = "HTML_PATH_MEMBER_PROTOCOL";
    public static final String HTML_PATH_USER_PROTOCOL = "HTML_PATH_USER_PROTOCOL";
    public static final String MEMBER_CENTER_PARAMS = "MEMBER_CENTER_PARAMS";

    // cms3.1新增bootguide字段
    public static final String NEW_CMS = "NEW_CMS";
    public static final String NEW_SEARCH = "NEW_SEARCH";
    public static final String HOTSEARCH_CONTENTID = "HOTSEARCH_CONTENTID"; //搜索id
    public static final String EXIT_CONTENTID = "EXIT_CONTENTID";

    public static final String BOOT_GUIDE = "BOOT_GUIDE";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl oldHttpUrl = request.url();
        Request.Builder builder = request.newBuilder();
        String headerValue = request.header("host_type");
        HttpUrl newBaseUrl = null;
        if (headerValue != null) {
            builder.removeHeader("host_type");
            switch (headerValue) {
                case BootGuide.CMS:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.CMS));
                    break;
                case BootGuide.SEARCH:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.SEARCH));
                    break;
                case BootGuide.SERVER_TIME:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.SERVER_TIME));
                    break;
                case BootGuide.VERSION_UP:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.VERSION_UP));
                    break;
                case BootGuide.ACTIVATE:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.ACTIVATE));
                    break;
                case BootGuide.AD:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.AD));
                    break;
                case BootGuide.LOG:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.LOG));
                    break;
                case BootGuide.CDN:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.CDN));
                    break;
                case BootGuide.DYNAMIC_KEY:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.DYNAMIC_KEY));
                    break;
                case BootGuide.PERMISSTION_CHECK:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.PERMISSTION_CHECK));
                    break;
                case BootGuide.IS_ORIENTED:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.IS_ORIENTED));
                    break;
                case BootGuide.USER:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.USER));
                    break;
                case BootGuide.PAY:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.PAY));
                    break;
                case BootGuide.PRODUCT:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.PRODUCT));
                    break;
                case BootGuide.NEW_CMS:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.NEW_CMS));
                    break;
                case BootGuide.NEW_SEARCH:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.NEW_SEARCH));
                    break;
                case BootGuide.USER_BEHAVIOR:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.USER_BEHAVIOR));
                    break;
                case BootGuide.BOOT_GUIDE:
                    newBaseUrl = HttpUrl.parse(BootGuide.getBaseUrl(BootGuide.BOOT_GUIDE));
                    break;
            }

            if (newBaseUrl == null) {
                return chain.proceed(request);
            }
            HttpUrl newFullUrl = oldHttpUrl.newBuilder()
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(newBaseUrl.port())
                    .build();

            return chain.proceed(builder.url(newFullUrl).build());
        } else {
            return chain.proceed(request);
        }
    }
}
