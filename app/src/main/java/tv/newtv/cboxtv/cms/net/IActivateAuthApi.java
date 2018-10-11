package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.newtv.libs.bean.ActivateBean;
import com.newtv.libs.bean.AuthBean;

/**
 * Created by TCP on 2018/4/11.
 */

public interface IActivateAuthApi {
    @Headers("host_type: " + HeadersInterceptor.ACTIVATE)
    @POST("monkeyking/service/apps/activate")
    Observable<ResponseBody> activate(@Body ActivateBean activateBean);

    @Headers("host_type: " + HeadersInterceptor.ACTIVATE)
    @POST("monkeyking/service/apps/auth")
    Observable<ResponseBody> auth(@Body AuthBean authBean);

}
