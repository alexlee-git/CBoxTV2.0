package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/9 0009 14:51
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface ISearchRecommendApi {
    @Headers("host_type: " + AppHeadersInterceptor.CMS)
    @GET("icms_api/api/{appKey}/{channelid}/hotsearch.json")
    Observable<ResponseBody> getRecommendResponse(@Path("appKey") String appKey, @Path("channelid") String channelid);
}
