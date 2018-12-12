package tv.newtv.cboxtv.cms.net;

import com.newtv.libs.BootGuide;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.cms.net
 * 创建事件:     上午 11:59
 * 创建人:       caolonghe
 * 创建日期:     2018/9/6 0006
 */
public interface IUserCenterLoginApi {

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/device_code")
    Observable<ResponseBody> getLoginQRCode(@Header("Authorization") String Authorization,
                                            @Field("response_type") String response_type,
                                            @Field("client_id") String client_id,
                                            @Field("channel_code") String channel_code);

    /**
     * 获取M站购买二维码
     *
     * @param Authorization
     * @param response_type
     * @param client_id
     * @param channel_code
     * @return
     */
    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/device_code")
    Observable<ResponseBody> getMemberQRCode(@Header("Authorization") String Authorization,
                                             @Field("response_type") String response_type,
                                             @Field("client_id") String client_id,
                                             @Field("channel_code") String channel_code,
                                             @Field("state") String state);


    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/device_code")
    Observable<ResponseBody> getAccessToken(@Header("Authorization") String Authorization,
                                            @Field("grant_type") String grant_type,
                                            @Field("device_code") String device_code,
                                            @Field("client_id") String client_id);

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/refresh_token")
    Observable<ResponseBody> refreshToken(@Header("Authorization") String Authorization,
                                          @Field("refresh_token") String refresh_token,
                                          @Field("client_id") String client_id,
                                          @Field("grant_type") String grant_type);

    @Headers("host_type: " + BootGuide.USER)
    @GET("/kangaroo/user/info")
    Observable<ResponseBody> getUser(@Header("Authorization") String Authorization);

    @Headers("host_type: " + BootGuide.USER)
    @GET("/goldenpheasant/api/programRights")
    Observable<ResponseBody> getUserTime(@Header("Authorization") String Authorization,
                                         @Query("productId") String productId,
                                         @Query("appKey") String appKey);

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/sms_code")
    Observable<ResponseBody> sendSMSCode(@Header("Authorization") String Authorization,
                                         @Field("response_type") String response_type,
                                         @Field("client_id") String client_id,
                                         @Field("mobile") String mobile);


    @Headers("host_type: " + BootGuide.CNTV_USER_LOGIN_HOST)
    @FormUrlEncoded
    @GET("/regist/getVerifiCode.action")
    Observable<ResponseBody> sendSMSCodeByCNTV(@Field("method") String method,
                                               @Field("mobile") String mobile,
                                               @Field("verifyCode") String verifyCode,
                                               @Field("isCheckCode") boolean isCheckCode);

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/sms_code")
    Observable<ResponseBody> verifySMSCode(@Header("Authorization") String Authorization,
                                           @Field("grant_type") String grant_type,
                                           @Field("client_id") String client_id,
                                           @Field("mobile") String mobile,
                                           @Field("sms_code") String sms_code);

    @Headers("host_type: " + BootGuide.CNTV_USER_LOGIN_HOST)
    @FormUrlEncoded
    @GET("/regist/getVerifiCode.action")
    Observable<ResponseBody> verifySMSCodeByCNTV(@Field("method") String method,
                                                 @Field("mobile") String mobile,
                                                 @Field("verifyCode") String verifyCode,
                                                 @Field("isCheckCode") boolean isCheckCode);


    //获取订单
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/api/orders/order")
    Observable<ResponseBody> getPayResponse(@Header("Authorization") String Authorization, @Body RequestBody requestBody);

    //获取订单
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/service/orders/scanQrOrder")
    Observable<ResponseBody> getPayResponse_new(@Header("Authorization") String Authorization, @Body RequestBody requestBody);

    //渠道
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/api/channels")
    Observable<ResponseBody> getPayChannel();

    //获取地址
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/api/orders/queryOrderById")
    Observable<ResponseBody> getPayResult(@Header("Authorization") String Authorization, @Query("orderId") String orderId);


    //询价3和4
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/q/price")
    Observable<ResponseBody> getProductPrice(@Query("prdId") String prdId, @Query("channelId") String channelId);

    //询价1
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/{productId}")
    Observable<ResponseBody> getProductPrices(@Path("productId") String productId,
                                              @Query("appKey") String prdId,
                                              @Query("prdType") String prdType,
                                              @Query("channelId") String channelId);

    //vip产品包
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/brief/vip")
    Observable<ResponseBody> getProduct(@Query("appKey") String prdId);

    //结果
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/api/programRights")
    Observable<ResponseBody> getPayFlag(@Header("Authorization") String Authorization, @Query("productIds") String[] productIds,
                                        @Query("appKey") String appKey, @Query("channelId") String channelId, @Query("contentUuid") String contentUuid);

    //刷新二维码
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/service/orders/refreshQrUrl")
    Observable<ResponseBody> getRefreshOrder(@Header("Authorization") String Authorization,
                                             @Query("orderId") String order);

    //节目集权益信息
    @Headers("host_type: " + BootGuide.PAY)
    @POST("goldenpheasant/api/programRights")
    Observable<ResponseBody> getBuyFlag(@Header("Authorization") String authorization,
                                        @Query("productIds") String productIds,
                                        @Query("appKey") String appKey,
                                        @Query("channelId") String channelId,
                                        @Query("contentUuid") String contentUuid,
                                        @Query("version") String version);

    // 历史
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/historys")
    Observable<ResponseBody> addHistory(@Header("authorization") String authorization,
                                        @Field("user_id") String user_id,
                                        @Field("channel_code") String channel_code,
                                        @Field("app_key") String app_key,
                                        @Field("programset_id") String programset_id,
                                        @Field("programset_name") String programset_name,
                                        @Field("is_program") String is_program,
                                        @Field("poster") String poster,
                                        @Field("program_progress") String program_progress,
                                        @Field("user_name") String user_name,
                                        @Field("program_dur") String program_dur,
                                        @Field("program_watch_dur") String program_watch_dur,
                                        @Field("is_panda") boolean is_panda,
                                        @Field("check_record") boolean check_record,
                                        @Field("program_child_id") String program_child_id,
                                        @Field("score") String grade,
                                        @Field("video_type") String videoType,
                                        @Field("total_count") String totalCnt,
                                        @Field("superscript") String superscript,
                                        @Field("content_type") String contentType,
                                        @Field("latest_episode") String curEpisode,
                                        @Field("action_type") String actionType,
                                        @Field("program_child_name") String programChildId,
                                        @Field("content_id") String contentId);


    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/historys/del")
    Observable<ResponseBody> deleteHistory(@Header("Authorization") String Authorization,
                                           @Query("is_program") String is_program,
                                           @Query("channel_code") String channel_code,
                                           @Query("app_key") String app_key,
                                           @Query("program_child_ids") String program_child_ids,
                                           @Query("programset_ids") String programset_ids);

//    @FormUrlEncoded
//    @POST("content/history/info")
//    Observable<JsonObject> getHistory(@Header("Authorization") String Authorization, @Field("contentUUid") String contentUUid, @Field("contentType") String contentType);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/historys")
    Observable<ResponseBody> getHistoryList(@Header("Authorization") String Authorization,
                                            @Query("app_key") String app_key,
                                            @Query("channel_code") String channel_code,
                                            @Query("user_id") String user_id,
                                            @Query("offset") String offset,
                                            @Query("limit") String limit);

    // 收藏
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/collections")
    Observable<ResponseBody> addCollect(@Header("authorization") String authorization,
                                        @Field("user_id") String user_id,
                                        @Field("channel_code") String channel_code,
                                        @Field("app_key") String app_key,
                                        @Field("programset_id") String programset_id,
                                        @Field("programset_name") String programset_name,
                                        @Field("is_program") String is_program,
                                        @Field("poster") String poster,
                                        @Field("program_child_id") String program_child_id,
                                        @Field("score") String score,
                                        @Field("video_type") String video_type,
                                        @Field("total_count") String total_count,
                                        @Field("superscript") String superscript,
                                        @Field("content_type") String content_type,
                                        @Field("latest_episode") String latest_episode,
                                        @Field("action_type") String action_type,
                                        @Field("program_child_name") String programChildName,
                                        @Field("content_id") String contentId);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/collections/del")
    Observable<ResponseBody> deleteCollect(@Header("authorization") String authorization,
                                           @Query("user_id") String user_id,
                                           @Query("is_program") String is_program,
                                           @Query("channel_code") String channel_code,
                                           @Query("app_key") String app_key,
                                           @Query("programset_ids") String[] programset_ids);

//    @FormUrlEncoded
//    @POST("collections")
//    Observable<JsonObject> getFavorite(@Header("Authorization") String Authorization, @Field("contentUUid") String contentUUid,
//                                       @Field("contentType") String contentType);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/collections")
    Observable<ResponseBody> getCollectList(@Header("Authorization") String Authorization,
                                            @Query("user_id") String user_id,
                                            @Query("is_program") String is_program,
                                            @Query("app_key") String app_key,
                                            @Query("channel_code") String channel_code,
                                            @Query("offset") String offset,
                                            @Query("limit") String limit);


    // 关注
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/concerns")
    Observable<ResponseBody> addFollow(@Header("authorization") String authorization,
                                       @Field("user_id") String user_id,
                                       @Field("channel_code") String channel_code,
                                       @Field("app_key") String app_key,
                                       @Field("programset_id") String programset_id,
                                       @Field("programset_name") String programset_name,
                                       @Field("is_program") String is_program,
                                       @Field("poster") String poster,
                                       @Field("content_type") String content_type,
                                       @Field("action_type") String action_type,
                                       @Field("content_id") String contentId);

    ;

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/concerns/del")
    Observable<ResponseBody> deleteFollow(@Header("authorization") String authorization,
                                          @Query("user_id") String user_id,
                                          @Query("is_program") String is_program,
                                          @Query("channel_code") String channel_code,
                                          @Query("app_key") String app_key,
                                          @Query("programset_ids") String[] programset_ids);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/concerns")
    Observable<ResponseBody> getFollowList(@Header("authorization") String authorization,
                                           @Query("user_id") String user_id,
                                           @Query("is_program") String is_program,
                                           @Query("app_key") String app_key,
                                           @Query("channel_code") String channel_code,
                                           @Query("offset") String offset,
                                           @Query("limit") String limit);

    // 订阅
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/subscribes")
    Observable<ResponseBody> addSubscribes(@Header("authorization") String authorization,
                                           @Field("user_id") String user_id,
                                           @Field("channel_code") String channel_code,
                                           @Field("app_key") String app_key,
                                           @Field("programset_id") String programset_id,
                                           @Field("programset_name") String programset_name,
                                           @Field("is_program") String is_program,
                                           @Field("poster") String poster,
                                           @Field("program_child_id") String program_child_id,
                                           @Field("score") String score,
                                           @Field("video_type") String video_type,
                                           @Field("total_count") String total_count,
                                           @Field("superscript") String superscript,
                                           @Field("content_type") String content_type,
                                           @Field("latest_episode") String latest_episode,
                                           @Field("action_type") String action_type,
                                           @Field("content_id") String contentId);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/subscribes/del")
    Observable<ResponseBody> deleteSubscribes(@Header("authorization") String authorization,
                                              @Query("user_id") String user_id,
                                              @Query("is_program") String is_program,
                                              @Query("channel_code") String channel_code,
                                              @Query("app_key") String app_key,
                                              @Query("programset_ids") String[] programset_ids);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/subscribes")
    Observable<ResponseBody> getSubscribesList(@Header("authorization") String authorization,
                                               @Query("user_id") String user_id,
                                               @Query("is_program") String is_program,
                                               @Query("app_key") String app_key,
                                               @Query("channel_code") String channel_code,
                                               @Query("offset") String offset,
                                               @Query("limit") String limit);

    //兑换码
    @Headers("host_type: " + BootGuide.USER)
    @POST("/goldenpheasant/service/exchangeCards/exchange")
    Observable<ResponseBody> getCodeExChange(@Header("Authorization") String Authorization,
                                             @Body RequestBody requestBody);

    //兑换码二维码
    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/device_code")
    Observable<ResponseBody> getCodeExChangeQRCode(@Header("Authorization") String Authorization,
                                                   @Field("response_type") String response_type,
                                                   @Field("client_id") String client_id,
                                                   @Field("channel_code") String channel_code,
                                                   @Field("state") String state);

}
