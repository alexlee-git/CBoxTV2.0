package tv.newtv.cboxtv.cms.net;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface IPlayPermissionCheckApi {

    @Headers({"Content-Type: application/json","Accept: application/json","host_type: "+ HeadersInterceptor.PERMISSTION_CHECK})//需要添加头
    @POST("goldenpheasant/api/orders/check")
    Call<ResponseBody> getCheckResult(@Body RequestBody requestBody);
}
