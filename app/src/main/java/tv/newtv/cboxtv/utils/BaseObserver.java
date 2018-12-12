package tv.newtv.cboxtv.utils;

import com.newtv.libs.util.SharePreferenceUtils;

import io.reactivex.Observer;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.LauncherApplication;

public abstract class BaseObserver<R> implements Observer<ResponseBody> {


    @Override
    public void onError(Throwable e) {
        try {
            if(UserCenterUtils.isUserOffline(e)){
                SharePreferenceUtils.clearToken(LauncherApplication.AppContext);
                dealwithUserOffline();
            }
        } catch (Exception exception) {
            e.printStackTrace();
        }
    }

    public void checkUserOffline(String str) {
        try {
            if(UserCenterUtils.isUserOffline(str)){
                SharePreferenceUtils.clearToken(LauncherApplication.AppContext);
                dealwithUserOffline();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void dealwithUserOffline();
}
