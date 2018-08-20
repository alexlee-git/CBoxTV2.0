package tv.newtv.cboxtv.cms.net;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018/4/19.
 */

public interface UpVersionApi {

    @Headers("host_type: " + HeadersInterceptor.VERSION_UP)
    @GET("bradypod/api/apps")
    Observable<ResponseBody> getUpVersion(@QueryMap Map<String, String>map);

    @Headers("host_type: " + HeadersInterceptor.IS_ORIENTED)
    @GET("bradypod/api/apps/isOriented")
    Observable<ResponseBody> getIsOriented(@QueryMap Map<String, String>map);


}




