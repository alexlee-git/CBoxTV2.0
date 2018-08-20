package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by lixin on 2018/1/15.
 */

public interface IClockSyncApi {
    @Headers("host_type: " + HeadersInterceptor.SERVER_TIME)
    @GET("panda/service/current/time")
    Observable<ResponseBody> getClockData();
}
