package tv.newtv.cboxtv.exit.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecommendApi {
    @GET("api/v31/8acb5c18e56c1988723297b1a8dc9260/600001/page/762.json/")
    Observable<ResponseBody> getRecommendData();
}
