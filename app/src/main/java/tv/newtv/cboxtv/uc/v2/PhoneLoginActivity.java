package tv.newtv.cboxtv.uc.v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.pay.ExterPayBean;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.Utils;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.player.vip.VipCheck;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayOrderActivity;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.uc.v2.member.UserAgreementActivity;
import tv.newtv.cboxtv.utils.UserCenterUtils;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         13:03
 * 创建人:           caolonghe
 * 创建日期:          2018/9/10
 */

public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PhoneLoginActivity.class.getSimpleName();

    public static final String PHONE_LOGIN_ACTION = "android.intent.action.PHONE_LOGIN";

    public static final int DELAY_MILLIS = 1000;
    public static final int CODE_SUCCESS = 1002;

    private RelativeLayout rel_phone, rel_code;
    private TextView tv_code_phone, tv_code_status, tv_code_inval;
    private TextView tv_agreenment;
    private TextView tv_success;
    private Button btn_refresh;
    private EditText mPhoneCodeInput;
    private EditText mPhoneLoginInput;
    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private KeyWordAdapter mAdapter;
    private int mTime = 5 * 60;
    private int mTime_success = 3;
    private String mMobile;
    private Disposable disposable_sendcode, disposable_sendok, disposable_buyFlag;
    private String[] key = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "后退", "0", "完成"};
    private boolean mFlagPay;
    private ExterPayBean mExterPayBean;
    private String mVipFlag;
    private boolean mFlagAuth;
    private boolean isSendOK = true;
    private String mContentUUID;
    private String mExternalAction;
    private String mExternalParams;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        initView();

        mFlagPay = getIntent().getBooleanExtra("ispay", false);
        mFlagAuth = getIntent().getBooleanExtra("isAuth", false);
        mExterPayBean = (ExterPayBean) getIntent().getSerializableExtra("payBean");
        mExternalAction = getIntent().getStringExtra("action");
        mExternalParams = getIntent().getStringExtra("params");
        Log.i(TAG, "PhoneLoginActivity--onCreate: mFlagPay = " + mFlagPay);
        if (mExterPayBean != null) {
            Log.i(TAG, "mExterPayBean = " + mExterPayBean.toString());
            mVipFlag = mExterPayBean.getVipFlag();
            Log.i(TAG, mExterPayBean.toString());
            mContentUUID = mExterPayBean.getContentUUID();
        }
    }

    public void initView() {

        rel_phone = (RelativeLayout) findViewById(R.id.user_phone_rel_1);
        rel_code = (RelativeLayout) findViewById(R.id.user_phone_rel_2);
        rel_phone.setVisibility(View.VISIBLE);
        rel_code.setVisibility(View.INVISIBLE);

        tv_success = findViewById(R.id.phone_login_success);
        tv_agreenment = findViewById(R.id.phone_login_user_agrement);
        tv_agreenment.setOnClickListener(this);
        tv_agreenment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tv_agreenment.setTextColor(Color.parseColor("#FFFFFF"));
                    tv_agreenment.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    tv_agreenment.getPaint().setAntiAlias(true);//抗锯齿
                } else {
                    tv_agreenment.setTextColor(Color.parseColor("#B3FFFFFF"));
                    tv_agreenment.getPaint().setFlags(0); // 取消设置的的划线
                    tv_agreenment.getPaint().setAntiAlias(true);//抗锯齿
                }
            }
        });
        tv_code_phone = findViewById(R.id.phone_login_tip_phone);
        tv_code_status = findViewById(R.id.phone_login_code_status);
        tv_code_inval = findViewById(R.id.phone_login_code_inval);
        btn_refresh = findViewById(R.id.phone_login_refresh_code);
        btn_refresh.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.login_recyclerview);
        mPhoneCodeInput = (EditText) findViewById(R.id.phone_login_input2);
        mPhoneLoginInput = (EditText) findViewById(R.id.phone_login_input1);
        mPhoneLoginInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPhoneCodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        mAdapter = new KeyWordAdapter(PhoneLoginActivity.this);
        layoutManager = new GridLayoutManager(PhoneLoginActivity.this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setDescendantFocusability(RecyclerView.FOCUS_AFTER_DESCENDANTS);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);
    }

    class KeyWordAdapter extends RecyclerView.Adapter<KeyWordAdapter.MyHolder> {

        private LayoutInflater mLayoutInflater;

        public KeyWordAdapter(Context mContext) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item_login_phone_key, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {

            holder.tv_name.setText(key[position]);

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEditKeyBoard(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return key.length;
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.phone_login_key);
            }
        }
    }

    private void setEditKeyBoard(int position) {
        EditText edit;
        if (rel_phone.getVisibility() == View.VISIBLE) {
            edit = mPhoneLoginInput;
        } else if (rel_code.getVisibility() == View.VISIBLE) {
            edit = mPhoneCodeInput;
        } else {
            rel_phone.setVisibility(View.VISIBLE);
            edit = mPhoneLoginInput;
        }
        if (position == 9) {
            int lenght = edit.getText().length();
            String input = edit.getText().toString().trim();
            if (lenght >= 1) {
                String newinput = input.substring(0, lenght - 1);
                Log.i(TAG, "newinput:" + newinput);
                edit.setText(newinput);
            } else {
                Log.i(TAG, "newinput:");
            }

        } else if (position == 11) {
            if (rel_phone.getVisibility() == View.VISIBLE) {
                sendCodePhone();
                return;
            } else if (rel_code.getVisibility() == View.VISIBLE) {
                if (isSendOK) {
                    isSendOK = false;
                    sendCodeOK();
                } else {
                    Log.i(TAG, "no sendCodeOK");
                }

                return;
            }
        } else {
            String input = edit.getText().toString().trim();
            edit.setText(input + key[position]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_login_refresh_code:
                sendCodePhone();
                break;
            case R.id.phone_login_user_agrement:
                startActivity(new Intent(this, UserAgreementActivity.class));
                break;
        }
    }

    private void sendCodeOK() {

        if (TextUtils.isEmpty(mPhoneCodeInput.getText().toString().trim())) {
            Toast.makeText(PhoneLoginActivity.this, getResources().getString(R.string.phone_login_code_err), Toast.LENGTH_SHORT).show();
            isSendOK = true;
            return;
        }
        getToken(Constant.Authorization, Constant.GRANT_TYPE_SMS,
                Constant.CLIENT_ID, mMobile, mPhoneCodeInput.getText().toString());

    }

    private void sendCodePhone() {
        mMobile = mPhoneLoginInput.getText().toString().trim();
        if (!checkMobile(mMobile)) {
            return;
        }
        Log.i(TAG, "onClick: mMobile = " + mMobile);
        if (TextUtils.isEmpty(Constant.Authorization)) {
            Constant.Authorization = Utils.getAuthorization(PhoneLoginActivity.this);
        }
        sendSMSCode(Constant.Authorization,
                Constant.GRANT_TYPE_SMS, Constant.CLIENT_ID, mMobile);
        rel_phone.setVisibility(View.INVISIBLE);
        rel_code.setVisibility(View.VISIBLE);
        tv_code_phone.setText(getResources().getString(R.string.phone_login_tip_code) + mMobile +
                getResources().getString(R.string.phone_login_tip_code1));
        tv_code_status.setText(getResources().getString(R.string.phone_login_status1));
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case DELAY_MILLIS:
                    if (mTime > 0) {
                        mTime--;
                        btn_refresh.setClickable(false);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(DELAY_MILLIS, 1000);
                        }
                    } else {
                        btn_refresh.setText(getResources().getString(R.string.phone_login_status4));
                        btn_refresh.setClickable(true);
                    }
                    break;
                case 2:
                    if (mHandler != null) {
                        mHandler.removeMessages(DELAY_MILLIS);
                    }
                    mTime = 5 * 60;
                    rel_code.setVisibility(View.INVISIBLE);
                    rel_phone.setVisibility(View.VISIBLE);
                    mPhoneLoginInput.requestFocus();
                    break;
                case CODE_SUCCESS:
                    isSendOK = false;
                    if (mTime_success > 0) {
                        tv_success.setVisibility(View.VISIBLE);
                        tv_success.setText(getResources().getString(R.string.phone_login_user_success) + mTime_success
                                + getResources().getString(R.string.phone_login_user_success1));
                        mTime_success--;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(CODE_SUCCESS, 1000);
                        }
                    } else {
                        Log.i(TAG, "mContentUUID: " + mContentUUID);
                        if (mFlagAuth) {
                            isBuy("", mContentUUID);
                        } else {
                            if (mFlagPay) {
                                if (mVipFlag != null) {
                                    Intent mIntent = new Intent();
                                    if (mVipFlag.equals(Constant.BUY_ONLY)) {
                                        mIntent.setClass(PhoneLoginActivity.this, PayOrderActivity.class);
                                    } else {
                                        mIntent.setClass(PhoneLoginActivity.this, PayChannelActivity.class);
                                    }
                                    mIntent.putExtra("payBean", mExterPayBean);
                                    startActivity(mIntent);
                                }
                            }
                            if (TextUtils.isEmpty(mExternalAction)&&TextUtils.isEmpty(mExternalParams)){
                                finish();
                            }else {
                                jumpActivity();
                            }
                        }
                    }
                    break;
            }

            return false;
        }
    });

    private void jumpActivity() {
        Class clazz = MainActivity.class;
        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        intent.putExtra("action", mExternalAction);
        intent.putExtra("params", mExternalParams);
        startActivity(intent);
        boolean isBackground = ActivityStacks.get().isBackGround();
        if (!isBackground && clazz == MainActivity.class) {
            ActivityStacks.get().finishAllActivity();
        }
        finish();
    }

    public boolean checkMobile(String mobile) {
        if (mobile.equals(null)) {
            Toast.makeText(PhoneLoginActivity.this, "手机号码不能为空!", Toast.LENGTH_LONG).show();
            mPhoneLoginInput.requestFocus();
                /*^匹配开始地方$匹配结束地方，[3|4|5|7|8]选择其中一个{4,8},\d从[0-9]选择
                {4,8}匹配次数4~8    ，java中/表示转义，所以在正则表达式中//匹配/,/匹配""*/
            //验证手机号码格式是否正确
            tv_code_status.setText(getResources().getString(R.string.phone_login_status2));
            return false;
        } else if (!mobile.matches("^1[3|4|5|7|8|9][0-9]\\d{4,8}$")) {
            Toast.makeText(PhoneLoginActivity.this, "手机号输入有误，请重新输入!", Toast.LENGTH_LONG).show();
            mPhoneLoginInput.setText("");
            mPhoneLoginInput.requestFocus();
            tv_code_status.setText(getResources().getString(R.string.phone_login_status2));
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.i("keyCode", keyCode + "---");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rel_code.getVisibility() == View.VISIBLE) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(2);
                }
                return true;
            } else {
                Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                intent.putExtra("location", 1);
                intent.putExtra("ispay", mFlagPay);
                intent.putExtra("payBean", mExterPayBean);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void uploadUserExterLog() {
        //添加用户
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append(8 + ",")
                .append(3 + ",")
                .append(1)
                .trimToSize();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, dataBuff.toString());
    }

    private void uploadUserExter() {

        String userid = SharePreferenceUtils.getUserId(this);
        if (!TextUtils.isEmpty(userid)) {
            LogUploadUtils.setLogFileds(Constant.USER_ID, userid);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (disposable_sendok != null) {
            disposable_sendok.dispose();
            disposable_sendok = null;
        }
        if (disposable_sendcode != null) {
            disposable_sendcode.dispose();
            disposable_sendcode = null;
        }
        Log.i(TAG, "PhoneLoginActivity---onDestroy---");
    }

    private void sendSMSCode(String Authorization, String response_type, String client_id, String phone) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .sendSMSCode(Authorization, response_type, client_id, phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable_sendcode = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login Qrcode :" + data.toString());
                                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                JSONObject mJsonObject = new JSONObject(data);
                                String time = mJsonObject.optString("expires_in");
                                mTime = Integer.parseInt(time);
                                btn_refresh.setText(getResources().getString(R.string.phone_login_status1));
                                tv_code_status.setText("请输入6位数验证码");
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessageDelayed(DELAY_MILLIS, 1000);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                mTime = 5 * 60;
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                            if (disposable_sendcode != null) {
                                disposable_sendcode.dispose();
                                disposable_sendcode = null;
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
                            Toast.makeText(PhoneLoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(2);
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (disposable_sendcode != null) {
                                disposable_sendcode.dispose();
                                disposable_sendcode = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken(String Authorization, String response_type, String client_id, String phone, String code) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .verifySMSCode(Authorization, response_type, client_id, phone, code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable_sendok = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login Qrcode :" + data.toString());
                                JSONObject mJsonObject = new JSONObject(data);
                                String accessToken = mJsonObject.optString("access_token");
                                String refreshToken = mJsonObject.optString("refresh_token");
                                String expiresTime = mJsonObject.optString("expires_in");

                                Log.i(TAG, "mVerifySMSCodeSubscriber--onSuccess: refreshToken = " + refreshToken);
                                Log.i(TAG, "mVerifySMSCodeSubscriber--onSuccess: accessToken = " + accessToken);
                                SharePreferenceUtils.saveToken(PhoneLoginActivity.this, accessToken, refreshToken);

                                UserCenterRecordManager.getInstance().getUserBehaviorUtils(getApplicationContext(), UserCenterRecordManager.REQUEST_RECORD_OFFSET, UserCenterRecordManager.REQUEST_RECORD_LIMIT);

                                uploadUserExterLog();
                                uploadUserExter();
                                UserCenterUtils.setLogin(true);
                                if (mHandler != null) {
                                    mTime_success = 3;
                                    mHandler.sendEmptyMessage(CODE_SUCCESS);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                            if (disposable_sendok != null) {
                                disposable_sendok.dispose();
                                disposable_sendok = null;
                            }
                            isSendOK = true;
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
                            Toast.makeText(PhoneLoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            tv_code_status.setText(getResources().getString(R.string.phone_login_status3));
                            mPhoneCodeInput.setText("");
                        }

                        @Override
                        public void onComplete() {
                            if (disposable_sendok != null) {
                                disposable_sendok.dispose();
                                disposable_sendok = null;
                            }
                            isSendOK = true;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isBuy(String productIds, String contentUUID) {
        String token = SharePreferenceUtils.getToken(PhoneLoginActivity.this);

        NetClient.INSTANCE.getUserCenterLoginApi()
                .getBuyFlag("Bearer " + token, productIds, Libs.get().getAppKey(),
                        Libs.get().getChannelId(), contentUUID, "3.1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable_buyFlag = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            LogUtils.i(TAG, result);
                            JSONObject jsonObject = new JSONObject(result);
                            boolean buyFlag = jsonObject.optBoolean("buyFlag");
                            Log.i(TAG, "buyFlag :" + buyFlag);
                            if (!buyFlag) {
                                if (mFlagPay) {
                                    if (mVipFlag != null) {
                                        Intent mIntent = new Intent();
                                        if (mVipFlag.equals(Constant.BUY_ONLY)) {
                                            mIntent.setClass(PhoneLoginActivity.this, PayOrderActivity.class);
                                        } else {
                                            mIntent.setClass(PhoneLoginActivity.this, PayChannelActivity.class);
                                        }
                                        mIntent.putExtra("payBean", mExterPayBean);
                                        startActivity(mIntent);
                                    }
                                }
                            }
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (disposable_buyFlag != null) {
                            disposable_buyFlag.dispose();
                            disposable_buyFlag = null;
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable_buyFlag != null) {
                            disposable_buyFlag.dispose();
                            disposable_buyFlag = null;
                        }
                    }
                });

    }

}
