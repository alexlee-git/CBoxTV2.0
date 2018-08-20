package tv.newtv.cboxtv.cms.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.model.ActivateBean;
import tv.newtv.cboxtv.cms.ad.model.AuthBean;
import tv.newtv.cboxtv.cms.net.NetClient;

/**
 * Created by TCP on 2018/4/12.
 */

public class ActivateAuthUtils {
    public static final int ACTIVATE = 1;
    public static final int AUTH = 2;

    public static final int FAIL           = 3;
    public static final int NET_ERROR      = 4; // 异常
    public static final int JSON_EXCEPTION = 5; //json解析错误
    public static final int IO_EXCEPTION   = 6; //io 异常
    public static final int NOT_SELF_DEVICE = 7; //不是合作终端

    public static final int LOCAL_EXCEPTION = 10; //10以下是客户端定义的错误类型

    /**
     * 重试次数
     * 存在本地UUID错误的情况
     * 当检测到错误时重新激活获取UUID
     */
    private static final int RETRY_NUMBER = 2;
    private static int number = 0;

    /**
     * 激活获取UUID
     *
     * @param context
     * @param callback
     */
    public static void activate(final Context context, final String appkey, final String channelId,
                                final Callback callback) {

        if (TextUtils.isEmpty(Constant.UUID))
            Constant.UUID = (String) SPrefUtils.getValue(context.getApplicationContext(),
                    Constant.UUID_KEY, "");

        if (TextUtils.isEmpty(Constant.UUID)) {
            ActivateBean activateBean = new ActivateBean(SystemUtils.getMac(context), appkey, channelId,
                    System.currentTimeMillis()+"");
            LogUtils.i(activateBean.toString());
            NetClient.INSTANCE
                    .getActivateAuthApi()
                    .activate(activateBean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                JSONObject response = new JSONObject(value.string());
                                int statusCode = response.getInt("statusCode");
                                if (1 == statusCode) {
                                    JSONObject obj = response.getJSONObject("response");
                                    String uuid = obj.getString("uuid");
                                    Constant.UUID = uuid;
                                    SPrefUtils.setValue(context.getApplicationContext(), Constant
                                            .UUID_KEY, uuid);
                                    LogUtils.i("app激活成功");
                                    callback.success(ACTIVATE);
                                    auth(context, appkey, channelId, Constant.UUID, callback);
                                } else {
                                    LogUtils.i("app激活失败");
                                    callback.fail(ACTIVATE, statusCode);
                                }
                            } catch (JSONException e) {
                                LogUtils.e(e.toString());
                                callback.fail(ACTIVATE, JSON_EXCEPTION);
                            } catch (IOException e) {
                                LogUtils.e(e.toString());
                                callback.fail(ACTIVATE, IO_EXCEPTION);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.i("onError: " + e);
                            callback.fail(ACTIVATE, NET_ERROR);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            auth(context, appkey, channelId, Constant.UUID, callback);
        }
    }

    /**
     * 认证
     *
     * @param context
     * @param callback
     */
    public static void auth(final Context context, final String appkey, final String channelId, String uuid,
                            final Callback callback) {
        AuthBean authBean = new AuthBean(SystemUtils.getMac(context), appkey, channelId, uuid,
                System.currentTimeMillis()+"");

        NetClient.INSTANCE
                .getActivateAuthApi()
                .auth(authBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            JSONObject response = new JSONObject(value.string());
                            int statusCode = response.getInt("statusCode");
                            if (1 == statusCode) {
                                String message = response.getString("message");
                                LogUtils.i("认证message=" + message);
                                callback.success(AUTH);
                            } else {
                                /**
                                 * 1 为成功
                                 500 为系统异常
                                 1001 为key不存在
                                 1002 为mac不正确或当前mac与uuid不匹配
                                 1003 为密文签名不正确
                                 1004 为APP被禁用
                                 */
                                if(1002 == statusCode && number++ < RETRY_NUMBER){
                                    Constant.UUID = "";
                                    SPrefUtils.setValue(context.getApplicationContext(),Constant.UUID_KEY,"");
                                    activate(context,appkey,channelId,callback);
                                    return;
                                }
                                callback.fail(AUTH, statusCode);
                            }

                        } catch (JSONException e) {
                            LogUtils.e(e.toString());
                            callback.fail(AUTH, JSON_EXCEPTION);
                        } catch (IOException e) {
                            LogUtils.e(e.toString());
                            callback.fail(AUTH, IO_EXCEPTION);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.i("onError: " + e);
                        callback.fail(AUTH, NET_ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface Callback {
        void success(int type);

        void fail(int type, int status);
    }

}
