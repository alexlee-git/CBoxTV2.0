package tv.newtv.cboxtv.cms.net;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;

/**
 * Created by lixin on 2018/1/11.
 */

public interface INavInfoApi {
    @Headers("host_type: " + HeadersInterceptor.CMS)
    @GET("icms_api/api/navigation/{newtv}/{channelid}/index.json") //
        // http://172.25.5.101/icms_api/api/navigation/newtv/5/index.json
    Observable<NavInfoResult<List<NavInfoResult.NavInfo>>> getNavInfo(@Path("newtv") String tag, @Path("channelid") String channelid);

//    @GET // http://172.25.5.101/icms_api/api/navigation/newtv/5/index.json
//    Observable<NavInfoResult<List<NavInfoResult.NavInfo>>> getNavInfo(@Url String url);
}
