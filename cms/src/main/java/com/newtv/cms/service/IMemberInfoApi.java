package com.newtv.cms.service;

import com.newtv.libs.BootGuide;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
public interface IMemberInfoApi {
    //获取用户会员信息
    @Headers("host_type: " + BootGuide.USER)
    @GET("goldenpheasant/api/programRights")
    Observable<ResponseBody> getMemberInfo(@Header("Authorization") String Authorization,
                                           @Query("productId") String productId,
                                           @Query("appKey") String appKey);

    //节目集权益信息
    @Headers("host_type: " + BootGuide.USER)
    @POST("goldenpheasant/api/programRights")
    Observable<ResponseBody> getBuyFlag(@Header("Authorization") String authorization,
                                        @Query("productIds") String productIds,
                                        @Query("appKey") String appKey,
                                        @Query("channelId") String channelId,
                                        @Query("contentUuid") String contentUuid,
                                        @Query("version") String version);

}
