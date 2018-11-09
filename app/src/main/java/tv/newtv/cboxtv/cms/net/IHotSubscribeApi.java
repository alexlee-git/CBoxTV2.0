package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午4:47
 * 创建人:           lixin
 * 创建日期:         2018/9/20
 */


public interface IHotSubscribeApi {
    // icms_api/api/8acb5c18e56c1988723297b1a8dc9260/600001/page/490.json
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appKey}/{channelid}/page/{hot_subscribe}.json")
    Observable<ResponseBody> getHotSubscribeInfo(@Path("appKey") String appKey, @Path("channelid") String channelid, @Path("hot_subscribe") String hotSubscribe);
}
