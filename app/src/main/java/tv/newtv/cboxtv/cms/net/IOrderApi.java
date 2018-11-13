package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 项目名称：CBoxTV2.0
 * 包名：tv.newtv.cboxtv.cms.net
 * 文件描述：订单
 * 作者：lxq
 * 创建时间：2018/9/11
 * 更改时间：2018/9/11
 */
public interface IOrderApi {
    //获取订单
    @Headers("host_type: " + AppHeadersInterceptor.PAY)
    @GET("goldenpheasant/api/orders")
    Observable<ResponseBody> getOrders(@Header("Authorization") String Authorization,
                                         @Query("appKey") String appKey,
                                         @Query("channelId") String channelId,
                                         @Query("payChannelId") String payChannelId,
                                         @Query("status") String status,
                                         @Query("offset") String offset,
                                         @Query("limit") String limit);
}
