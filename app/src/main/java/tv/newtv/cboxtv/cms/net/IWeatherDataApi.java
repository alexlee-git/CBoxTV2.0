package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
  * TODO :获取天气数据
  * auther : 王海龙
  * created : 2018/2/26 19:09
  */

public interface IWeatherDataApi {
    @GET
    Observable<ResponseBody> getWeatherResponse(@Url String url);
}
