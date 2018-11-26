package com.newtv.cms.service

import com.newtv.libs.BootGuide
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.service
 * 创建事件:         15:39
 * 创建人:           weihaichao
 * 创建日期:          2018/11/23
 */
interface IUserCenterRetro {
    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/device_code")
    fun getLoginQRCode(@Header("Authorization") Authorization: String,
                                @Field("response_type") response_type: String,
                                @Field("client_id") client_id: String,
                                @Field("channel_code") channel_code: String): Observable<ResponseBody>


    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/device_code")
    fun getAccessToken(@Header("Authorization") Authorization: String,
                                @Field("grant_type") grant_type: String,
                                @Field("device_code") device_code: String,
                                @Field("client_id") client_id: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/refresh_token")
    fun refreshToken(@Header("Authorization") Authorization: String,
                              @Field("refresh_token") refresh_token: String,
                              @Field("client_id") client_id: String,
                              @Field("grant_type") grant_type: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER)
    @GET("/kangaroo/user/info")
    fun getUser(@Header("Authorization") Authorization: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER)
    @GET("/goldenpheasant/api/programRights")
    abstract fun getUserTime(@Header("Authorization") Authorization: String,
                             @Query("productId") productId: String,
                             @Query("appKey") appKey: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/authorization/sms_code")
    fun sendSMSCode(@Header("Authorization") Authorization: String,
                             @Field("response_type") response_type: String,
                             @Field("client_id") client_id: String,
                             @Field("mobile") mobile: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER)
    @FormUrlEncoded
    @POST("/kangaroo/token/sms_code")
    fun verifySMSCode(@Header("Authorization") Authorization: String,
                               @Field("grant_type") grant_type: String,
                               @Field("client_id") client_id: String,
                               @Field("mobile") mobile: String,
                               @Field("sms_code") sms_code: String): Observable<ResponseBody>


    //获取订单
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/api/orders/order")
    fun getPayResponse(@Header("Authorization") Authorization: String, @Body requestBody: RequestBody): Observable<ResponseBody>

    //获取订单
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/service/orders/scanQrOrder")
    fun getPayResponse_new(@Header("Authorization") Authorization: String, @Body requestBody: RequestBody): Observable<ResponseBody>

    //渠道
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/api/channels")
    fun getPayChannel(): Observable<ResponseBody>

    //获取地址
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/api/orders/queryOrderById")
    fun getPayResult(@Header("Authorization") Authorization: String, @Query("orderId") orderId: String): Observable<ResponseBody>


    //询价3和4
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/q/price")
    fun getProductPrice(@Query("prdId") prdId: String, @Query("channelId") channelId: String): Observable<ResponseBody>

    //询价1
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/{productId}")
    fun getProductPrices(@Path("productId") productId: String,
                                  @Query("appKey") prdId: String,
                                  @Query("prdType") prdType: String,
                                  @Query("channelId") channelId: String): Observable<ResponseBody>

    //vip产品包
    @Headers("host_type: " + BootGuide.PRODUCT)
    @GET("/mandrill/service/products/brief/vip")
    fun getProduct(@Query("appKey") prdId: String): Observable<ResponseBody>

    //结果
    @Headers("host_type: " + BootGuide.PAY)
    @POST("/goldenpheasant/api/programRights")
    fun getPayFlag(@Header("Authorization") Authorization: String, @Query("productIds") productIds: Array<String>,
                            @Query("appKey") appKey: String, @Query("channelId") channelId: String, @Query("contentUuid") contentUuid: String): Observable<ResponseBody>

    //刷新二维码
    @Headers("host_type: " + BootGuide.PAY)
    @GET("/goldenpheasant/service/orders/refreshQrUrl")
    fun getRefreshOrder(@Header("Authorization") Authorization: String,
                                 @Query("orderId") order: String): Observable<ResponseBody>

    // 历史
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/historys")
    fun addHistory(@Header("authorization") authorization: String,
                            @Field("user_id") user_id: String,
                            @Field("channel_code") channel_code: String,
                            @Field("app_key") app_key: String,
                            @Field("programset_id") programset_id: String,
                            @Field("programset_name") programset_name: String,
                            @Field("is_program") is_program: String,
                            @Field("poster") poster: String,
                            @Field("program_progress") program_progress: String,
                            @Field("user_name") user_name: String,
                            @Field("program_dur") program_dur: String,
                            @Field("program_watch_dur") program_watch_dur: String,
                            @Field("is_panda") is_panda: Boolean,
                            @Field("check_record") check_record: Boolean,
                            @Field("program_child_id") program_child_id: String,
                            @Field("score") grade: String,
                            @Field("video_type") videoType: String,
                            @Field("total_count") totalCnt: String,
                            @Field("superscript") superscript: String,
                            @Field("content_type") contentType: String,
                            @Field("latest_episode") curEpisode: String,
                            @Field("action_type") actionType: String): Observable<ResponseBody>


    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/historys/del")
    fun deleteHistory(@Header("Authorization") Authorization: String,
                               @Query("is_program") is_program: String,
                               @Query("channel_code") channel_code: String,
                               @Query("app_key") app_key: String,
                               @Query("program_child_ids") program_child_ids: String,
                               @Query("programset_ids") programset_ids: String): Observable<ResponseBody>

//    @FormUrlEncoded
//    @POST("content/history/info")
//    Observable<JsonObject> getHistory(@Header("Authorization") String Authorization, @Field("contentUUid") String contentUUid, @Field("contentType") String contentType);

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/historys")
    fun getHistoryList(@Header("Authorization") Authorization: String,
                                @Query("app_key") app_key: String,
                                @Query("channel_code") channel_code: String,
                                @Query("user_id") user_id: String,
                                @Query("offset") offset: String,
                                @Query("limit") limit: String): Observable<ResponseBody>

    // 收藏
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/collections")
    fun addCollect(@Header("authorization") authorization: String,
                            @Field("user_id") user_id: String,
                            @Field("channel_code") channel_code: String,
                            @Field("app_key") app_key: String,
                            @Field("programset_id") programset_id: String,
                            @Field("programset_name") programset_name: String,
                            @Field("is_program") is_program: String,
                            @Field("poster") poster: String,
                            @Field("program_child_id") program_child_id: String,
                            @Field("score") score: String,
                            @Field("video_type") video_type: String,
                            @Field("total_count") total_count: String,
                            @Field("superscript") superscript: String,
                            @Field("content_type") content_type: String,
                            @Field("latest_episode") latest_episode: String,
                            @Field("action_type") action_type: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/collections/del")
    fun deleteCollect(@Header("authorization") authorization: String,
                               @Query("user_id") user_id: String,
                               @Query("is_program") is_program: String,
                               @Query("channel_code") channel_code: String,
                               @Query("app_key") app_key: String,
                               @Query("programset_ids") programset_ids: Array<String>): Observable<ResponseBody>


    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/collections")
    fun getCollectList(@Header("Authorization") Authorization: String,
                                @Query("user_id") user_id: String,
                                @Query("is_program") is_program: String,
                                @Query("app_key") app_key: String,
                                @Query("channel_code") channel_code: String,
                                @Query("offset") offset: String,
                                @Query("limit") limit: String): Observable<ResponseBody>


    // 关注
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/concerns")
    fun addFollow(@Header("authorization") authorization: String,
                           @Field("user_id") user_id: String,
                           @Field("channel_code") channel_code: String,
                           @Field("app_key") app_key: String,
                           @Field("programset_id") programset_id: String,
                           @Field("programset_name") programset_name: String,
                           @Field("is_program") is_program: String,
                           @Field("poster") poster: String,
                           @Field("content_type") content_type: String,
                           @Field("action_type") action_type: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/concerns/del")
    fun deleteFollow(@Header("authorization") authorization: String,
                              @Query("user_id") user_id: String,
                              @Query("is_program") is_program: String,
                              @Query("channel_code") channel_code: String,
                              @Query("app_key") app_key: String,
                              @Query("programset_ids") programset_ids: Array<String>): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/concerns")
    fun getFollowList(@Header("authorization") authorization: String,
                               @Query("user_id") user_id: String,
                               @Query("is_program") is_program: String,
                               @Query("app_key") app_key: String,
                               @Query("channel_code") channel_code: String,
                               @Query("offset") offset: String,
                               @Query("limit") limit: String): Observable<ResponseBody>

    // 订阅
    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @FormUrlEncoded
    @POST("/gazella/service/subscribes")
    fun addSubscribes(@Header("authorization") authorization: String,
                               @Field("user_id") user_id: String,
                               @Field("channel_code") channel_code: String,
                               @Field("app_key") app_key: String,
                               @Field("programset_id") programset_id: String,
                               @Field("programset_name") programset_name: String,
                               @Field("is_program") is_program: String,
                               @Field("poster") poster: String,
                               @Field("program_child_id") program_child_id: String,
                               @Field("score") score: String,
                               @Field("video_type") video_type: String,
                               @Field("total_count") total_count: String,
                               @Field("superscript") superscript: String,
                               @Field("content_type") content_type: String,
                               @Field("latest_episode") latest_episode: String,
                               @Field("action_type") action_type: String): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @DELETE("/gazella/service/subscribes/del")
    fun deleteSubscribes(@Header("authorization") authorization: String,
                                  @Query("user_id") user_id: String,
                                  @Query("is_program") is_program: String,
                                  @Query("channel_code") channel_code: String,
                                  @Query("app_key") app_key: String,
                                  @Query("programset_ids") programset_ids: Array<String>): Observable<ResponseBody>

    @Headers("host_type: " + BootGuide.USER_BEHAVIOR)
    @GET("/gazella/service/subscribes")
    fun getSubscribesList(@Header("authorization") authorization: String,
                                   @Query("user_id") user_id: String,
                                   @Query("is_program") is_program: String,
                                   @Query("app_key") app_key: String,
                                   @Query("channel_code") channel_code: String,
                                   @Query("offset") offset: String,
                                   @Query("limit") limit: String): Observable<ResponseBody>
}