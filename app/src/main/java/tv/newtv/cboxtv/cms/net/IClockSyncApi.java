package tv.newtv.cboxtv.cms.net;

import com.newtv.libs.BootGuide;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by lixin on 2018/1/15.
 */

public interface IClockSyncApi {
    @Headers("host_type: " + BootGuide.SERVER_TIME)
    @GET("panda/service/current/time")
    Observable<ResponseBody> getClockData();
}
