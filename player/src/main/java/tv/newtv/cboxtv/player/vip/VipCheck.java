package tv.newtv.cboxtv.player.vip;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.cms.Request;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class VipCheck {
    private static final String TAG = "VipCheck";
    /**
     * 免费
     */
    public static final String VIP_FLAG_FREE = "0";
    /**
     * 单点包月（买会员或者买单点都能看）
     */
    public static final String VIP_FLAG_VIP_BUY = "1";
    /**
     * Vip才能看
     */
    public static final String VIP_FLAG_VIP = "3";
    /**
     *  单点才能看
     */
    public static final String VIP_FLAG_BUY = "4";

    public static void isBuy(String productIds,String contentUUID,Context context,final BuyFlagListener listener){
        String token = SharePreferenceUtils.getToken(context);
        if (!TextUtils.isEmpty(token) && UserStatus.isLogin()) {

            Request.INSTANCE.getMemberInfoApi()
                    .getBuyFlag("Bearer "+token,productIds, Libs.get().getAppKey(),
                            Libs.get().getChannelId(),contentUUID,"3.1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String result = responseBody.string();
                                LogUtils.i(TAG,result);
                                JSONObject jsonObject = new JSONObject(result);
                                boolean buyFlag = jsonObject.optBoolean("buyFlag");
                                if(listener != null){
                                    listener.buyFlag(buyFlag);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if(listener != null){
                                    listener.buyFlag(false);
                                }}
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(listener != null){
                                listener.buyFlag(false);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else {
            listener.buyFlag(false);
        }
    }

    public interface BuyFlagListener{
        void buyFlag(boolean buyFlag);
    }

}
