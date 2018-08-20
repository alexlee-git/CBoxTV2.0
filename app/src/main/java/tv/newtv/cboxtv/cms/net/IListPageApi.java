package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by caolonghe on 2018/1/11.
 */

public interface IListPageApi {
    @GET
    Observable<ResponseBody> getListPageResponse(@Url String Url);

    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getRetrievalProgramSerialList.json")
    Observable<ResponseBody> getScreenResult(@Query("type") String type, @Query("appKey") String appkey,
                                             @Query("channelCode") String channelCode, @Query("contentType") String contentType,
                                             @Query("year") String year, @Query("area") String area,
                                             @Query("classType") String classType,
                                             @Query("startnum") String startnum,
                                             @Query("size") String size);

    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getRetrievalKeywords.json")
    Observable<ResponseBody> getMarkData();


    //栏目搜索
    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getSearchListByCatagory.json")
    Observable<ResponseBody> getSearchCategoryData(@Query("contentType") String contentType,
                                                   @Query("firstCatagory") String firstCatagory,
                                                   @Query("appKey") String appKey,
                                                   @Query("channelCode") String channelCode,
                                                   @Query("startnum") String startnum,
                                                   @Query("size") String size);


    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getSearchListByKeyword.json")
    Observable<ResponseBody> getSearchListByKeyword(@Query("type") String type, @Query("appKey") String appkey,
                                                    @Query("channelCode") String channelCode, @Query("contentType") String contentType,
                                                    @Query("year") String year, @Query("area") String area,
                                                    @Query("classType") String classType,
                                                    @Query("startnum") String startnum,
                                                    @Query("size") String size);

    @GET
    Observable<ResponseBody> getMarkDataResult(@Url String url);
}
