package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by caolonghe on 2018/1/11.
 */

public interface IProgramSeriesInfoApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelId}/{left}/{right}/{contentUUID}.json")
    Observable<ResponseBody> getProgramResponse(@Path("appkey") String appkey, @Path("channelId") String channelId,
                                                @Path("left") String left, @Path("right") String right,
                                                @Path("contentUUID") String contentUUID);
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @POST("goldenpheasant/api/programRights")
    Observable<ResponseBody> getPayFlag(@Query("userId") String userId, @Query("userToken") String userToken,
                                        @Query("appKey") String appKey, @Query("channelId") String channelId, @Query("contentUuid") String contentUuid);
}
