package com.newtv.cms.contract;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IActiveAuth;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.bean.ActivateBean;
import com.newtv.libs.bean.AuthBean;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SPrefUtils;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         12:33
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class ActiveAuthContract {

    public interface View extends ICmsView {

        void authResult();

        void activeResult();

        void failed(int type, int status);
    }

    public interface Presenter extends ICmsPresenter {
        void auth();

        void active();
    }

    public static class ActiveAuthPresenter extends CmsServicePresenter<View> implements Presenter {

        public static final int ACTIVATE = 1;
        public static final int AUTH = 2;

        public static final int FAIL = 3;
        public static final int NET_ERROR = 4; // 异常
        static final int JSON_EXCEPTION = 5; //json解析错误
        public static final int IO_EXCEPTION = 6; //io 异常
        public static final int NOT_SELF_DEVICE = 7; //不是合作终端
        public static final int LOCAL_EXCEPTION = 10; //10以下是客户端定义的错误类型


        /**
         * 重试次数
         * 存在本地UUID错误的情况
         * 当检测到错误时重新激活获取UUID
         */
        private static final int RETRY_NUMBER = 2;
        private static final int MAX_NUM = 3;
        private static final int TIME_ONE_SECOND = 200;
        private static final int RETRY_ACTIVE = 0x998;
        private static int number = 0;
        private int num = 0;

        private Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case RETRY_ACTIVE:
                        active();
                        break;
                }
                return false;
            }
        });

        @Override
        public void destroy() {
            super.destroy();

            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        public ActiveAuthPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        private void onFailed(int type, int status) {
            if (status < LOCAL_EXCEPTION
                    && num < MAX_NUM * Constant.activateUrls.size() && handler != null) {
                Constant.BASE_URL_ACTIVATE = Constant.activateUrls.get(num / MAX_NUM);
                Message message = handler.obtainMessage(RETRY_ACTIVE);
                handler.sendMessageDelayed(message, TIME_ONE_SECOND);
                num++;
            }else{
                getView().failed(type, status);
            }
        }

        @Override
        public void auth() {
            IActiveAuth activeAuth = getService(SERVICE_ACTIVE_AUTH);
            if (activeAuth != null) {
                AuthBean authBean = new AuthBean(SystemUtils.getMac(getContext()),
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(),
                        Constant.UUID,
                        System.currentTimeMillis() + "");
                activeAuth.auth(authBean, new DataObserver<String>() {
                    @Override
                    public void onResult(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            int statusCode = response.getInt("statusCode");
                            if (1 == statusCode) {
                                String message = response.getString("message");
                                LogUtils.i("认证message=" + message);
                                getView().authResult();
                            } else {
                                /**
                                 * 1 为成功
                                 500 为系统异常
                                 1001 为key不存在
                                 1002 为mac不正确或当前mac与uuid不匹配
                                 1003 为密文签名不正确
                                 1004 为APP被禁用
                                 */
                                if (1002 == statusCode && number++ < RETRY_NUMBER) {
                                    Constant.UUID = "";
                                    SPrefUtils.setValue(getContext().getApplicationContext(),
                                            Constant.UUID_KEY, "");
                                    active();
                                    return;
                                }
                                onFailed(AUTH, statusCode);
                            }
                        } catch (JSONException e) {
                            LogUtils.e(e.toString());
                            onFailed(AUTH, JSON_EXCEPTION);
                        }
                    }

                    @Override
                    public void onError(@Nullable String desc) {

                    }
                });
            }
        }

        @Override
        public void active() {
            if (TextUtils.isEmpty(Constant.UUID))
                Constant.UUID = (String) SPrefUtils.getValue(getContext().getApplicationContext(),
                        Constant.UUID_KEY, "");

            if (TextUtils.isEmpty(Constant.UUID)) {
                IActiveAuth activeAuth = getService(SERVICE_ACTIVE_AUTH);
                if (activeAuth != null) {

                    ActivateBean activateBean = new ActivateBean(SystemUtils
                            .getMac(getContext()),
                            Libs.get().getAppKey(),
                            Libs.get().getChannelId(),
                            System.currentTimeMillis() + "");
                    activeAuth.active(activateBean, new DataObserver<String>() {
                        @Override
                        public void onResult(String result) {
                            try {
                                JSONObject response = new JSONObject(result);
                                int statusCode = response.getInt("statusCode");
                                if (1 == statusCode) {
                                    JSONObject obj = response.getJSONObject("response");
                                    String uuid = obj.getString("uuid");
                                    Constant.UUID = uuid;
                                    SPrefUtils.setValue(getContext(), Constant
                                            .UUID_KEY, uuid);
                                    LogUtils.i("app激活成功");
                                    getView().activeResult();

                                    auth();
                                } else {
                                    LogUtils.i("app激活失败");
                                    onFailed(ACTIVATE, statusCode);
                                }
                            } catch (JSONException e) {
                                LogUtils.e(e.toString());
                                onFailed(ACTIVATE, JSON_EXCEPTION);
                            }
                        }

                        @Override
                        public void onError(@Nullable String desc) {

                        }
                    });
                }
            } else {
                auth();
            }
        }

    }
}
