package tv.newtv.cboxtv.uc.v2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.utils.BaseObserver;
import tv.newtv.cboxtv.utils.UserCenterUtils;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         17:38
 * 创建人:           weihaichao
 * 创建日期:          2018/8/24
 */
public class UserInfoActivity extends BaseActivity implements View.OnKeyListener {
    private final String TAG = "UserInfoActivity";
    private String[] sexValues = {"男", "女", "未知"};
    private int sexIndex = 0;
    private TextView sexSelector;
    private Disposable disposable_user;
    private String token;
    private String Authorition;
    private TextView tv_id, tv_phone;
    private Button btn_exit;
    private String member_status;
    private ImageView img_vip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        //我的账户上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "0,6");
        member_status = getIntent().getStringExtra("member_status");

        tv_id = findViewById(R.id.userinfo_checkbox_id);
        tv_phone = findViewById(R.id.userinfo_checkbox_phone);
        btn_exit = findViewById(R.id.userinfo_btn_exit);
        img_vip = findViewById(R.id.userinfo_tv_vip);

        findViewById(R.id.sex_continer).setOnKeyListener(this);

        sexSelector = findViewById(R.id.sex_text);

        if (member_status != null) {
            if (member_status.equals("member_open_good")) {
                img_vip.setVisibility(View.VISIBLE);
                img_vip.setImageResource(R.drawable.uc_head_member_mark_v2);
            } else if (member_status.equals("member_open_lose")) {
                img_vip.setVisibility(View.VISIBLE);
                img_vip.setImageResource(R.drawable.uc_head_not_member_mark_v2);
            } else {
                img_vip.setVisibility(View.INVISIBLE);
            }
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    boolean isRefresh = TokenRefreshUtil.getInstance().isTokenRefresh(UserInfoActivity.this);
                    if (isRefresh) {
                        Log.i(TAG, "isToken is ture");
                        token = SharePreferenceUtils.getToken(UserInfoActivity.this);

                    } else {
                        Log.i(TAG, "isToken is false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(token);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String value) throws Exception {
                        if (TextUtils.isEmpty(value)) {
                            finish();
                        } else {
                            Authorition = "Bearer " + value;
                            getUserInfo(Authorition);
                        }
                    }
                });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录上报日志
                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "0,4");
                SharePreferenceUtils.clearToken(UserInfoActivity.this);
                UserCenterUtils.setLogin(false);
                finish();
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT
                    && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
                return false;
            }
            switch (v.getId()) {
                case R.id.sex_continer:
                    onSexChange(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
                    break;
            }
        }

        return false;
    }

    /**
     * 调整性别
     *
     * @param isLeftKey
     */
    private void onSexChange(boolean isLeftKey) {
        if (isLeftKey) {
            sexIndex -= 1;
        } else {
            sexIndex += 1;
        }
        if (sexIndex < 0) sexIndex = sexValues.length - 1;
        if (sexIndex > sexValues.length - 1) sexIndex = 0;
        updateUI();
        SharePreferenceUtils.saveSex(UserInfoActivity.this, sexIndex);
    }

    private void updateUI() {

        if (sexSelector != null) {
            sexSelector.setText(sexValues[sexIndex]);
        }

    }

    private void getUserInfo(String Authorization) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getUser(Authorization)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ResponseBody>() {

                        @Override
                        public void dealwithUserOffline() {
                            Log.i(TAG, "dealwithUserOffline: ");
                            UserCenterUtils.userOfflineStartLoginActivity(UserInfoActivity.this);

                        }

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable_user = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Log.i(TAG, "onNext: ");
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, data + "----");
                                checkUserOffline(data);
                                JSONObject jsonObject = new JSONObject(data);
                                String phone = jsonObject.optString("mobile");
                                if (phone.length() == 11) {
                                    phone = phone.substring(0, 3) + "xxxx" + phone.substring(7, 11);
                                }
                                tv_phone.setText(phone);
                                tv_id.setText(jsonObject.optString("id"));
                                sexIndex = jsonObject.optInt("isMale");

                                int status = SharePreferenceUtils.getSex(UserInfoActivity.this);
                                if (status == -1) {
                                    updateUI();
                                } else {
                                    sexIndex = status;
                                    sexSelector.setText(sexValues[status]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "onError: ");
                            super.onError(e);
                            if (disposable_user != null) {
                                disposable_user.dispose();
                                disposable_user = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (disposable_user != null) {
                                disposable_user.dispose();
                                disposable_user = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable_user != null) {
            disposable_user.dispose();
            disposable_user = null;
        }
    }
}
