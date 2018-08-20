package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by lixin on 2018/3/9.
 */

public interface ISuperScriptApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/corner/corner.json")
    Observable<ResponseBody> getSuperscriptInfos();
}
