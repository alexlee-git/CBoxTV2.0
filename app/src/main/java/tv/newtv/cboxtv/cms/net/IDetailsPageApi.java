package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;


/**
 * Created by gaoleichao on 2018/4/3.
 */

public interface IDetailsPageApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}" +
            ".json")
    Observable<ResponseBody> getInfo(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                     @Path("left") String left, @Path("right") String right,
                                     @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_currentlist.json")
    Observable<ResponseBody> getCurrentColmn(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                             @Path("left") String left, @Path("right") String right,
                                             @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_historylist.json")
    Observable<ResponseBody> getHistoryColmn(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                             @Path("left") String left, @Path("right") String right,
                                             @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_characterlist.json")
    Observable<ResponseBody> getCharacterlist(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                              @Path("left") String left, @Path("right") String right,
                                              @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_columnsByCharacter.json")
    Observable<ResponseBody> getColumnsByCharacter(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                                   @Path("left") String left, @Path("right") String right,
                                                   @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_columnlist.json")
    Observable<ResponseBody> getColumnsByPersons(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                                 @Path("left") String left, @Path("right") String right,
                                                 @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{left}/{right}/{contentUUID}_programlist.json")
    Observable<ResponseBody> getProgramList(@Path("appkey") String appkey, @Path("channelCode") String channelId,
                                            @Path("left") String left, @Path("right") String right,
                                            @Path("contentUUID") String contentUUID);

    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{channelId}/columnlist.json")
    Observable<ResponseBody> getChannelColmn(@Path("appkey") String appkey, @Path("channelCode") String channelCode
            , @Path("channelId") String channelId);


    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/{appkey}/{channelCode}/{channelId}/columnlist.json")
    Observable<ResponseBody> getCorrelation(@Path("appkey") String appkey, @Path("channelCode") String channelCode
            , @Path("channelId") String channelId);


}
