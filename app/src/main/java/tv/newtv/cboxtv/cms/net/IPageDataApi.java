package tv.newtv.cboxtv.cms.net;

import com.newtv.libs.BootGuide;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by lixin on 2018/2/2.
 */

public interface IPageDataApi {
    @Headers("host_type: " + BootGuide.NEW_CMS)
    @GET("icms_api/api/{appKey}/{channelid}/page/{pageuuid}.json")
    Observable<ResponseBody> getPageData(@Path("appKey") String appKey, @Path("channelid") String channelid, @Path("pageuuid") String pageuuid);
}
