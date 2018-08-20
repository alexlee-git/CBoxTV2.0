package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import tv.newtv.cboxtv.player.menu.model.HeadMenuBean;

/**
 * Created by TCP on 2018/4/17.
 */

public interface IMenuApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/categorytree.json")
    Observable<HeadMenuBean> getCategoryTree(@Path("appkey") String appkey, @Path("channelCode") String channelCode);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_historylist.json")
    Observable<ResponseBody> getLastList(@Path("appkey") String appkey, @Path("channelCode") String channelCode, @Path("left") String left, @Path("right") String right, @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelId}/{left}/{right}/{contentUUID}.json")
    Observable<ResponseBody> getPsList(@Path("appkey") String appkey, @Path("channelId") String channelId, @Path("left") String left, @Path("right") String right, @Path("contentUUID") String contentUUID);
}
