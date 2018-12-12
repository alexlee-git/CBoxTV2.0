package tv.newtv.cboxtv.uc.v2.Pay;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.v2.MyOrderActivity;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.utils.BaseObserver;
import tv.newtv.cboxtv.utils.UserCenterUtils;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2
 * 创建事件:     下午 4:20
 * 创建人:       caolonghe
 * 创建日期:     2018/9/12 0012
 */
public class PayRefreshOrderActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "PayRefreshOrderActivity";
    private TextView tv_wx, tv_wx_fouse;
    private TextView tv_name, tv_price, tv_time;
    private ImageView img_qrcode;
    private String mToken;
    private String mVipProductId;
    private Disposable mDisposable_result, mDisposable_order, mDisposable_time;
    private ProductPricesInfo mProductPricesInfo;
    private int payChannelId = 2;
    private long orderId;
    private QrcodeUtil qrcodeUtil;
    private String code;
    private String qrCodeUrl;
    private final String pay_success = "PAY_SUCCESS";
    private String status = "";
    private final int MSG_QRCODE = 1;
    private final int MSG_ERROR = 3;
    private final int MSG_RESULT = 4;
    private final int MSG_RESULT_OK_TIME = 5;
    private final int MSG_RESULT_OK = 6;
    private final int MSG_SETMESSAGE = 7;
    private String mFlagAction;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private TextView tv_title_full;
    private ImageView img_qrcode_full;
    private Dialog dialog;
    private TextView tv_dialog_status;
    private TextView tv_dialog_time;
    private Button btn_dialog_ok;
    private long expireTime;
    private String Exp_time;
    private int Time = 5;
    private final String prdType = "1";
    private String mContentUUID, order, mVipFlag, mTitle, mContentType;
    private long amount, orderduration;
    private String message_error = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payrefreshorder);

        mVipProductId = String.valueOf(getIntent().getIntExtra("productId", 0));
        Log.i(TAG, "VipProductId:" + mVipProductId);
        mFlagAction = getIntent().getStringExtra("action");
        mContentUUID = getIntent().getStringExtra("mediaId");
        mContentType = getIntent().getStringExtra("ContentType");
        mTitle = getIntent().getStringExtra("productName");
        order = String.valueOf(getIntent().getIntExtra("orderId", 0));
        mVipFlag = String.valueOf(getIntent().getIntExtra("productType", 0));
        payChannelId = getIntent().getIntExtra("payChannelId", 0);
        amount = getIntent().getIntExtra("amount", 0);
        orderduration = getIntent().getIntExtra("duration", 0);

        Log.i(TAG, "vipFlag:" + mVipFlag);
        Log.i(TAG, "orderduration:" + orderduration);
        Log.i(TAG, "payChannelId:" + payChannelId);
        init();
        qrcodeUtil = new QrcodeUtil();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    boolean isRefresh = TokenRefreshUtil.getInstance().isTokenRefresh(PayRefreshOrderActivity.this);
                    if (isRefresh) {
                        Log.i(TAG, "isToken is ture");
                        mToken = SharePreferenceUtils.getToken(PayRefreshOrderActivity.this);
                        requestMemberInfo();
                    } else {
                        Log.i(TAG, "isToken is false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(mToken);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String value) throws Exception {
                        if (TextUtils.isEmpty(mToken)) {
                            finish();
                        } else {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_SETMESSAGE);
                            }
                            getOrder(order);
                        }
                    }
                });

        if (payChannelId == 2) {
            tv_wx.setText(getResources().getString(R.string.usercenter_pay_channel_wx));
        } else {
            tv_wx.setText(getResources().getString(R.string.usercenter_pay_channel_ap));
        }

    }

    private void init() {
        tv_wx = findViewById(R.id.paychannel_order_tv_wx);
        tv_wx_fouse = findViewById(R.id.paychannel_order_tv_fouse_wx);
        tv_name = findViewById(R.id.paychannel_order_tv_name);
        tv_price = findViewById(R.id.paychannel_order_tv_price);
        tv_time = findViewById(R.id.paychannel_order_tv_time);
        img_qrcode = findViewById(R.id.paychannel_order_img_qrcode);

        tv_wx.setOnClickListener(this);

        mPopupView = LayoutInflater.from(this).inflate(R.layout.layout_usercenter_qr_code_full, null);
        img_qrcode_full = mPopupView.findViewById(R.id.layout_usercenter_img_qrcode);
        tv_title_full = mPopupView.findViewById(R.id.usercenter_full_screen_qr_top_title);
        tv_title_full.setText(getResources().getString(R.string.usercenter_pay));
        mPopupWindow = new PopupWindow(mPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。

        initdialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paychannel_order_tv_wx:
                mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;
            case R.id.paychannel_order_tv_ap:
                mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    private void getOrder(String order) {
        String Authorization = "Bearer " + mToken;
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getRefreshOrder(Authorization, order)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_order = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            Log.i(TAG, "onNext: ");
                            try {
                                String data = value.string();
                                checkUserOffline(data);
                                JSONObject object = new JSONObject(data);
                                orderId = object.getLong("id");
                                code = object.getString("code");
                                qrCodeUrl = object.getString("qrCodeUrl");
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_QRCODE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                mHandler.sendEmptyMessage(3);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (e instanceof HttpException) {
                                HttpException httpException = (HttpException) e;
                                try {
                                    String responseString = httpException.response().errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    message_error = jsonObject.getString("message");
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(MSG_ERROR);
                                    }
                                    Log.i(TAG, "error: " + responseString);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            if (mDisposable_order != null) {
                                mDisposable_order.dispose();
                                mDisposable_order = null;
                            }
                        }

                        @Override
                        public void dealwithUserOffline() {
                            Log.i(TAG, "dealwithUserOffline: ");
                            UserCenterUtils.userOfflineStartLoginActivity(PayRefreshOrderActivity.this);
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_order != null) {
                                mDisposable_order.dispose();
                                mDisposable_order = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "git payurl error");
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_QRCODE: {
                    qrcodeUtil.createQRImage(qrCodeUrl, img_qrcode,
                            getResources().getDimensionPixelOffset(R.dimen.width_390px),
                            getResources().getDimensionPixelOffset(R.dimen.height_390px));
                    qrcodeUtil.createQRImage(qrCodeUrl, img_qrcode_full,
                            getResources().getDimensionPixelOffset(R.dimen.width_407px),
                            getResources().getDimensionPixelOffset(R.dimen.height_407px));
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_RESULT);
                    }
                    break;
                }
                case MSG_ERROR: {
                    Toast.makeText(PayRefreshOrderActivity.this, message_error, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
                case MSG_RESULT: {
                    if (status.equals(pay_success)) {
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_RESULT);
                            mHandler.sendEmptyMessage(MSG_RESULT_OK);
                        }
                    } else {
                        getPayResult();
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(MSG_RESULT, 3000);
                        }
                    }
                    break;
                }
                case MSG_RESULT_OK_TIME:
                    btn_dialog_ok.setText(getResources().getString(R.string.usercenter_pay_success_ok) + Time + getResources().getString(R.string.usercenter_pay_success_ok1));
                    if (Time >= 1) {
                        Time--;
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_RESULT_OK_TIME);
                            mHandler.sendEmptyMessageDelayed(MSG_RESULT_OK_TIME, 1000);
                        }
                    } else {
                        btn_dialog_ok.setText(getResources().getString(R.string.usercenter_pay_success_ok2));
                        setStartActivity();
                    }
                    break;
                case MSG_RESULT_OK: {
                    mHandler.sendEmptyMessage(MSG_RESULT_OK_TIME);
                    setExprefresh();
                    dialog.show();
                    break;
                }
                case MSG_SETMESSAGE: {

                    try {
                        tv_name.setText(getResources().getString(R.string.usercenter_pay_product) + mTitle);
                        String price = BigDecimal.valueOf((long) amount).divide(new BigDecimal(100)).toString();
                        tv_price.setText(getResources().getString(R.string.usercenter_pay_price) + price + getResources().getString(R.string.usercenter_pay_price_unit));
                        long duration = orderduration * 60 * 60 * 1000;
                        Log.e(TAG, "duration :" + duration);
                        Log.e(TAG, "expireTime :" + expireTime);
                        long time = duration + expireTime;
                        Log.e(TAG, "time :" + time);
                        Date date = new Date(time);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
                        tv_time.setText(getResources().getString(R.string.usercenter_pay_time) + format.format(date));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            return false;
        }
    });

    private void setExprefresh() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> e) throws Exception {
                long time = requestMemberInfo();
                Log.e(TAG, "setExprefresh : " + time);
                e.onNext(time);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        Date date2 = new Date(aLong);
                        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
                        tv_dialog_time.setText(getResources().getString(R.string.usercenter_pay_success_time) +
                                format2.format(date2));
                    }
                });
    }

    private void setStartActivity() {
        Intent intent = new Intent(PayRefreshOrderActivity.this, MyOrderActivity.class);
        intent.putExtra("isPaySuccess", "yes");
        setResult(RESULT_OK, intent);
        finish();
        Log.d(TAG, "-----------finish");
    }

    private void getPayResult() {

        String Authorization = "Bearer " + mToken;
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getPayResult(Authorization, orderId + "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_result = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                JSONObject object = new JSONObject(data);
                                status = object.getString("status");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_RESULT);
                            }
                            if (mDisposable_result != null) {
                                mDisposable_result.dispose();
                                mDisposable_result = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_result != null) {
                                mDisposable_result.dispose();
                                mDisposable_result = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(Constant.TAG, "get pay  result error");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mDisposable_order != null) {
            mDisposable_order.dispose();
            mDisposable_order = null;
        }
        if (mDisposable_result != null) {
            mDisposable_result.dispose();
            mDisposable_result = null;
        }

    }

    private void initdialog() {
        dialog = new Dialog(this);
        final View dialogView = LayoutInflater.from(PayRefreshOrderActivity.this)
                .inflate(R.layout.layout_payorder_success, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setBackgroundDrawable(new BitmapDrawable());
//        lp.x = 560; // 新位置X坐标
//        lp.y = 240; // 新位置Y坐标
        lp.width = getResources().getDimensionPixelOffset(R.dimen.width_1494px); // 宽度
        lp.height = getResources().getDimensionPixelOffset(R.dimen.height_603px); // 高度
        lp.alpha = 0.9f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);

        tv_dialog_status = (TextView) dialogView.findViewById(R.id.layout_payorder_tv_status);
        tv_dialog_time = (TextView) dialogView.findViewById(R.id.layout_payorder_tv_time);
        btn_dialog_ok = (Button) dialogView.findViewById(R.id.layout_payorder_btn);
        btn_dialog_ok.setText(getResources().getString(R.string.usercenter_pay_success_ok) +
                Time + getResources().getString(R.string.usercenter_pay_success_ok1));
        btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setStartActivity();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    //读取用户会员信息
    private long requestMemberInfo() {
        try {
            NetClient.INSTANCE.getUserCenterMemberInfoApi()
                    .getMemberInfo("Bearer " + mToken, "",
                            Libs.get().getAppKey(),"")
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_time = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.e(TAG, "---data is " + data);
                                JSONArray jsonArray = new JSONArray(data);
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    Exp_time = jsonObject.optString("expireTime");
                                    Log.e(TAG, "---expireTime：" + Exp_time);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Exp_time));
                                    System.out.println("日期[2018-11-12 17:08:12]对应毫秒：" + calendar.getTimeInMillis());
                                    expireTime = calendar.getTimeInMillis();
                                    Log.e(TAG, "---expireTime：" + expireTime);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "---requestMemberInfo:onError");
                            if (mDisposable_time != null) {
                                mDisposable_time.dispose();
                                mDisposable_time = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_time != null) {
                                mDisposable_time.dispose();
                                mDisposable_time = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (expireTime <= TimeUtil.getInstance().getCurrentTimeInMillis()) {
            expireTime = TimeUtil.getInstance().getCurrentTimeInMillis();
        }
        return expireTime;
    }

}
