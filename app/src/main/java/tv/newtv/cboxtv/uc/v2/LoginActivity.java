package tv.newtv.cboxtv.uc.v2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.uc.pay.ExterPayBean;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.Utils;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayOrderActivity;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.utils.UserCenterUtils;


/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         17:03
 * 创建人:           weihaichao
 * 创建日期:          2018/8/24
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, View
        .OnFocusChangeListener {

    private final String TAG = "LoginActivity";
    private ImageView img_login;
    private Button mButton;
    private FrameLayout frameLayout_qrcode;
    private QrcodeUtil mQrcodeUtil;
    private String mDeviceCode;
    private String Authorization;
    private final int MSG_RESULT_TOKEN = 1;
    private final int MSG_RESULT_INVALID = 2;
    private Disposable disposable_Qrcode, disposable_token;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private TextView tv_title_full;
    private ImageView img_qrcode_full;
    private Bitmap mBitmap;
    private String mQRcode;
    private int expires;
    private boolean mFlagPay;
    private ExterPayBean mExterPayBean;
    private String mVipFlag;
    private int location = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        location = getIntent().getIntExtra("location", -1);
        mFlagPay = getIntent().getBooleanExtra("ispay", false);
        mExterPayBean = (ExterPayBean) getIntent().getSerializableExtra("payBean");
        if (mExterPayBean != null) {
            Log.i(TAG, "mExterPayBean: " + mExterPayBean);
            mVipFlag = mExterPayBean.getVipFlag();
        }
        Log.i(TAG, "mFlagPay: " + mFlagPay);
        if (TextUtils.isEmpty(Authorization)) {
            Authorization = Utils.getAuthorization(LoginActivity.this);
            Constant.Authorization = Authorization;
        }
        if (location == 1) {
            Log.i(TAG, "location: " + location);
            mButton.requestFocus();
        }

    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        img_login = findViewById(R.id.login_imageview);
        mButton = findViewById(R.id.mobile_login_btn);
        frameLayout_qrcode = findViewById(R.id.login_frame_qrcode_root);
        mButton.setOnClickListener(this);
        frameLayout_qrcode.setOnClickListener(this);
        mButton.setOnFocusChangeListener(this);
        frameLayout_qrcode.setOnFocusChangeListener(this);
        mQrcodeUtil = new QrcodeUtil();
        mPopupView = LayoutInflater.from(this).inflate(R.layout.layout_usercenter_qr_code_full, null);
        img_qrcode_full = mPopupView.findViewById(R.id.layout_usercenter_img_qrcode);
        tv_title_full = mPopupView.findViewById(R.id.usercenter_full_screen_qr_top_title);
        tv_title_full.setText(getResources().getString(R.string.usercenter_login_scan_phone));
        mPopupWindow = new PopupWindow(mPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(Authorization)) {
            Authorization = Utils.getAuthorization(LoginActivity.this);
            Constant.Authorization = Authorization;
        }

        getQrcode(Authorization, Constant.RESPONSE_TYPE, Constant.CLIENT_ID);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobile_login_btn:
                Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                intent.putExtra("ispay", mFlagPay);
                intent.putExtra("payBean", mExterPayBean);
                startActivity(intent);
                finish();
                break;
            case R.id.login_frame_qrcode_root:
                mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            onItemGetFocus(v);
        } else {
            onItemLoseFocus(v);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_RESULT_TOKEN);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESULT_TOKEN:
                    getToken(Authorization,
                            Constant.RESPONSE_TYPE,
                            mDeviceCode,
                            Constant.CLIENT_ID);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MSG_RESULT_TOKEN, 2000);
                    }
                    break;
                case MSG_RESULT_INVALID:
                    getQrcode(Authorization, Constant.RESPONSE_TYPE, Constant.CLIENT_ID);
                    break;
            }
            return false;
        }
    });


    private void getQrcode(String Authorization, String response_type, String client_id) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getLoginQRCode(Authorization, response_type, client_id, Libs.get().getChannelId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable_Qrcode = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login Qrcode :" + data.toString());
                                JSONObject mJsonObject = new JSONObject(data);
                                mDeviceCode = mJsonObject.optString("device_code");
                                mQRcode = mJsonObject.optString("veriﬁcation_uri_complete");
                                expires = mJsonObject.optInt("expires_in");
                                mQrcodeUtil.createQRImage(mQRcode, getResources().getDimensionPixelOffset(R.dimen.width_448px),
                                        getResources().getDimensionPixelOffset(R.dimen.height_448px), mBitmap, img_login);

                                mQrcodeUtil.createQRImage(mQRcode, getResources().getDimensionPixelOffset(R.dimen.height_607px),
                                        getResources().getDimensionPixelOffset(R.dimen.height_607px), mBitmap, img_qrcode_full);
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessageDelayed(MSG_RESULT_INVALID, expires * 1000);
                                    mHandler.sendEmptyMessage(MSG_RESULT_TOKEN);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                            if (disposable_Qrcode != null) {
                                disposable_Qrcode.dispose();
                                disposable_Qrcode = null;
                            }
                            String error = getResources().getString(R.string.send_phone_err);
                            if (e instanceof HttpException) {
                                HttpException httpException = (HttpException) e;
                                try {
                                    String responseString = httpException.response().errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    error = jsonObject.getString("msg");
                                    Log.i(TAG, "error: " + responseString);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            if (disposable_Qrcode != null) {
                                disposable_Qrcode.dispose();
                                disposable_Qrcode = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken(String Authorization, String response_type, String mDeviceCode, String client_id) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getAccessToken(Authorization, response_type, mDeviceCode, client_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable_token = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login responseBody :" + data);
                                JSONObject mJsonObject = new JSONObject(data);
                                String mAccessToken = mJsonObject.optString("access_token");
                                String RefreshToken = mJsonObject.optString("refresh_token");
                                Log.i(TAG, "Login access_token :" + mAccessToken);
                                Log.i(TAG, "Login RefreshToken :" + RefreshToken);
                                SharePreferenceUtils.saveToken(LoginActivity.this, mAccessToken, RefreshToken);

                                UserCenterRecordManager.getInstance().getUserBehaviorUtils(getApplicationContext(), UserCenterRecordManager.REQUEST_RECORD_OFFSET, UserCenterRecordManager.REQUEST_RECORD_LIMIT);

                                if (mHandler != null) {
                                    mHandler.removeMessages(MSG_RESULT_TOKEN);
                                }
                                //微信登录上报日志
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "8,0,1");
                                // LogUploadUtils.uploadKey(Constant.USER_ID, SharePreferenceUtils.getUserId(LoginActivity.this));
                                LogUploadUtils.setLogFileds(Constant.USER_ID, SharePreferenceUtils.getUserId(LoginActivity.this));
                                if (mFlagPay) {
                                    if (mVipFlag != null) {
                                        Intent mIntent = new Intent();
                                        if (mVipFlag.equals(Constant.BUY_ONLY)) {
                                            mIntent.setClass(LoginActivity.this, PayOrderActivity.class);
                                        } else {
                                            mIntent.setClass(LoginActivity.this, PayChannelActivity.class);
                                        }
                                        mIntent.putExtra("payBean", mExterPayBean);
                                        startActivity(mIntent);
                                    }
                                }
                                UserCenterUtils.setLogin(true);
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (disposable_token != null) {
                                disposable_token.dispose();
                                disposable_token = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (disposable_token != null) {
                                disposable_token.dispose();
                                disposable_token = null;
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
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (disposable_Qrcode != null) {
            disposable_Qrcode.dispose();
            disposable_Qrcode = null;
        }
        if (disposable_token != null) {
            disposable_token.dispose();
            disposable_token = null;
        }
    }

    private void onItemGetFocus(View view) {
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

}
