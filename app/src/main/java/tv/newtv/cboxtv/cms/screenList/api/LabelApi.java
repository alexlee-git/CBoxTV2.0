package tv.newtv.cboxtv.cms.screenList.api;



import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public interface LabelApi {

    @GET("api/v31/8acb5c18e56c1988723297b1a8dc9260/600001/categorytree/categorytree.json/")
    Observable<ResponseBody> getFirstMenu(@Query("searchFlag") int searchFlag);

    @GET("api/v31/8acb5c18e56c1988723297b1a8dc9260/600001/filterkeywords/101.json")
    Observable<ResponseBody> getSecondMenu();



}
