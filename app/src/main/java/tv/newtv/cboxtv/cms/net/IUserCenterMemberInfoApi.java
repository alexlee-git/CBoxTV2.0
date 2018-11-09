package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 项目名称： CBoxTV2.0
 * 类描述：获取用户中心会员信息接口
 * 创建人：wqs
 * 创建时间： 2018/9/13 0013 15:43
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface IUserCenterMemberInfoApi {
    //获取用户会员信息
    @Headers("host_type: " + HeadersInterceptor.USER)
    @GET("goldenpheasant/api/programRights")
    Observable<ResponseBody> getMemberInfo(@Header("Authorization") String Authorization,
                                           @Query("productId") String productId,
                                           @Query("appKey") String appKey);
}
