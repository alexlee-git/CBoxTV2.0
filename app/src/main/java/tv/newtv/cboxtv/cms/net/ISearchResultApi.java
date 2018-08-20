package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/22 0009 16:07
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface ISearchResultApi {
    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getRetrievalProgramSerialList.json")
    Observable<ResponseBody> getRetrievalSearchResultResponse(@Query("appKey") String appKey, @Query("channelCode") String channelCode, @Query("contentType") String contentType, @Query("type") String type, @Query("year") String year,
                                                              @Query("area") String area, @Query("classType") String classType,
                                                              @Query("startnum") Integer startnum,
                                                              @Query("size") Integer size);

    @Headers("host_type: " + HeadersInterceptor.SEARCH)
    @GET("newtv-solr-search/pps/getSearchListByKeyword.json")
    Observable<ResponseBody> getKeywordSearchResultResponse(@Query("appKey") String appKey, @Query("channelCode") String channelCode, @Query("contentType") String contentType, @Query("keyword") String keyword, @Query("keywordType") String keywordType, @Query("programType") String programType, @Query("startnum") Integer startNum, @Query("size") Integer size);
}
