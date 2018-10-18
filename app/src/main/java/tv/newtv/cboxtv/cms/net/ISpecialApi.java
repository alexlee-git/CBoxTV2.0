package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by lin on 2018/3/9.
 */

public interface ISpecialApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelId}/page/{pageUUID}.json")
    Observable<ResponseBody> getPageData(@Path("appkey") String appkey, @Path("channelId") String channelId,
                                         @Path("pageUUID") String pageUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelId}/{left}/{right}/{contentID}.json")
    //http://111.32.132.156/icms_api/api/8acb5c18e56c1988723297b1a8dc9260/600001/84/98/84198.json
    Observable<ResponseBody> getDoublePageData(@Path("appkey") String appkey, @Path("channelId") String channelId,
                                               @Path("left") String left, @Path("right") String right, @Path("contentID") String contentID);
}
