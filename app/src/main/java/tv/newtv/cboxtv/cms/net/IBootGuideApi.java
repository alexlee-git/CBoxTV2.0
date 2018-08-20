package tv.newtv.cboxtv.cms.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tv.newtv.cboxtv.Constant;

/**
 * Created by lixin on 2018/3/14.
 */

public interface IBootGuideApi {
    @GET(Constant.BOOT_GUIDE_HOST + "auth/bootGuide")
    Observable<ResponseBody> getServerAddresses(@Query("platformid") String platformId);
}
