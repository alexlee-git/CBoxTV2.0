package tv.newtv.cboxtv.uc.v2.Pay;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import com.google.gson.Gson;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.pay.ExterPayBean;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.v2.NetWorkUtils;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.member.MemberCenterActivity;
import tv.newtv.cboxtv.utils.BaseObserver;
import tv.newtv.cboxtv.utils.UserCenterUtils;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2
 * 创建事件:     下午 4:20
 * 创建人:       caolonghe
 * 创建日期:     2018/9/12 0012
 */
public class PayOrderActivity extends BaseActivity implements View.OnFocusChangeListener, View
        .OnClickListener {

    private final String TAG = "PayOrderActivity";
    private RelativeLayout rel_pay;
    private TextView tv_wx, tv_ap, tv_wx_fouse, tv_ap_fouse;
    private TextView tv_name, tv_price, tv_time;
    private ImageView img_qrcode;
    private String mToken;
    private String mVipProductId;
    private int position;
    private Disposable mDisposable_price, mDisposable_result, mDisposable_order, mDisposable_time;
    private ProductPricesInfo mProductPricesInfo;
    private int payChannelId = 2;
    private long orderId;
    private QrcodeUtil qrcodeUtil;
    private String code;
    private String qrCodeUrl;
    private final String pay_success = "PAY_SUCCESS";
    private String status = "";
    private final int MSG_QRCODE = 1;
    private final int MSG_BUY_ONLY = 2;
    private final int MSG_ERROR = 3;
    private final int MSG_RESULT = 4;
    private final int MSG_RESULT_OK_TIME = 5;
    private final int MSG_RESULT_OK = 6;
    private final int MSG_SETMESSAGE = 7;
    private final int MSG_REFRESHORDER = 8;
    private boolean isFirstQrCode = true;
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
    private String mContentUUID, mContentID, mMAMID, mVipFlag, mTitle, mContentType;
    private boolean isBuyOnly = false;  //true 单点 ，false显示
    private boolean isVip = false;
    private ExterPayBean mExterPayBean;
    private String message_error;
    private final String ACTTYPE = "DISCOUNT";
    private int price;
    private long expireTime_All;
    private long orders[];
    private boolean mIfContinued; //是否是连续包月

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payorder);

        mProductPricesInfo = (ProductPricesInfo) getIntent().getSerializableExtra("data");
        Log.i(TAG, "ProductPricesInfo:" + mProductPricesInfo);
        position = getIntent().getIntExtra("Postion", -1);
        Log.i(TAG, "position:" + position);

        mExterPayBean = (ExterPayBean) getIntent().getSerializableExtra("payBean");
        if (mExterPayBean != null) {
            Log.i(TAG, mExterPayBean.toString());
            mVipProductId = mExterPayBean.getVipProductId();
            mContentUUID = mExterPayBean.getContentUUID();
            mContentID = mExterPayBean.getContentId();
            mContentType = mExterPayBean.getContentType();
            mMAMID = mExterPayBean.getMAMID();
            mTitle = mExterPayBean.getTitle();
            mFlagAction = mExterPayBean.getAction();
            mVipFlag = mExterPayBean.getVipFlag();
        } else {
            mVipProductId = getIntent().getStringExtra("VipProductId");
        }
        init();
        qrcodeUtil = new QrcodeUtil();

        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                long time = 0;
                try {
                    boolean isRefresh = TokenRefreshUtil.getInstance().isTokenRefresh(PayOrderActivity.this);
                    if (isRefresh) {
                        Log.i(TAG, "isToken is ture");
                        mToken = SharePreferenceUtils.getToken(PayOrderActivity.this);
                        time = requestMemberInfo("");
                    } else {
                        Log.i(TAG, "isToken is false");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(time);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mProductPricesInfo == null) {
                            position = 0;
                            isBuyOnly = true;
                            if (mVipProductId == null) {
                                Toast.makeText(PayOrderActivity.this, "产品ID不能为空", Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_BUY_ONLY);
                            }
                        } else {
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "5," + mVipProductId);
                            isBuyOnly = false;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_SETMESSAGE);
                            }
                        }
                    }
                });
    }

    private void init() {
        orders = new long[2];
        tv_wx = findViewById(R.id.paychannel_order_tv_wx);
        tv_ap = findViewById(R.id.paychannel_order_tv_ap);
        tv_wx_fouse = findViewById(R.id.paychannel_order_tv_fouse_wx);
        tv_ap_fouse = findViewById(R.id.paychannel_order_tv_fouse_ap);
        tv_name = findViewById(R.id.paychannel_order_tv_name);
        tv_price = findViewById(R.id.paychannel_order_tv_price);
        tv_time = findViewById(R.id.paychannel_order_tv_time);
        img_qrcode = findViewById(R.id.paychannel_order_img_qrcode);
        rel_pay = findViewById(R.id.paychannel_order_qrcode_root);

        tv_wx.setOnFocusChangeListener(this);
        tv_ap.setOnFocusChangeListener(this);
        rel_pay.setOnClickListener(this);

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
            case R.id.paychannel_order_qrcode_root:
                mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mDisposable_order != null) {
            mDisposable_order.dispose();
            mDisposable_order = null;
        }
//        img_qrcode.setImageDrawable(null);
        switch (v.getId()) {
            case R.id.paychannel_order_tv_wx:
                if (hasFocus) {
                    tv_wx_fouse.setVisibility(View.VISIBLE);
                    payChannelId = 2;
                    if (isFirstQrCode) {
                        isFirstQrCode = false;
                        return;
                    }
                    long orderID = orders[1];
                    if (orderID != 0) {
                        getReFreshOrder(String.valueOf(orderID));
                    } else {
                        getPayQRCode();
                    }

                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_RESULT);
                    }
                } else {
                    tv_wx_fouse.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.paychannel_order_tv_ap:
                if (hasFocus) {
                    tv_ap_fouse.setVisibility(View.VISIBLE);
                    payChannelId = 1;
                    long orderID = orders[0];
                    if (orderID != 0) {
                        getReFreshOrder(String.valueOf(orderID));
                    } else {
                        getPayQRCode();
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_RESULT);
                    }
                } else {
                    tv_ap_fouse.setVisibility(View.INVISIBLE);
                }
                break;
        }

    }

    private void setBuyOnly() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {

                time = requestMemberInfo(mContentUUID);

                emitter.onNext(time);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        if (TextUtils.equals(Constant.BUY_VIPANDONLY, mVipFlag)) {
                            getProductPrice(mVipProductId, Libs.get().getAppKey(), Libs.get().getChannelId());
                        } else {
                            getProductPriceOnly(mVipProductId, Libs.get().getChannelId());
                        }
                    }
                });
    }

    private void getProductPrice(String prdId, String appkey, String channelId) {

        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getProductPrices(prdId, appkey, prdType, channelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_price = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {

                            try {
                                String data = value.string().trim();
                                Gson mGson = new Gson();
                                mProductPricesInfo = mGson.fromJson(data, ProductPricesInfo.class);
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_SETMESSAGE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                mHandler.sendEmptyMessage(3);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (mDisposable_price != null) {
                                mDisposable_price.dispose();
                                mDisposable_price = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_price != null) {
                                mDisposable_price.dispose();
                                mDisposable_price = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "git payurl error");
        }
    }

    private void getProductPriceOnly(String prdId, String channelId) {

        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getProductPrice(prdId, channelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_price = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {

                            try {
                                String data = value.string().trim();
                                Gson mGson = new Gson();
                                mProductPricesInfo = mGson.fromJson(data, ProductPricesInfo.class);
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_SETMESSAGE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                mHandler.sendEmptyMessage(3);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (mDisposable_price != null) {
                                mDisposable_price.dispose();
                                mDisposable_price = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_price != null) {
                                mDisposable_price.dispose();
                                mDisposable_price = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "git payurl error");
        }
    }

    private void getpay(RequestBody requestBody) {

        String Authorization = "Bearer " + mToken;
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getPayResponse_new(Authorization, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_order = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                JSONObject object = new JSONObject(data);
                                orderId = object.getLong("id");
                                code = object.getString("code");
                                qrCodeUrl = object.getString("qrCodeUrl");
                                if (payChannelId == 1) {
                                    orders[0] = orderId;
                                } else if (payChannelId == 2) {
                                    orders[1] = orderId;
                                }
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
                            if (mDisposable_order != null) {
                                mDisposable_order.dispose();
                                mDisposable_order = null;
                            }
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
                            getResources().getDimensionPixelOffset(R.dimen.width_607px),
                            getResources().getDimensionPixelOffset(R.dimen.height_607px));
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_RESULT);
                        mHandler.removeMessages(MSG_REFRESHORDER);
                        mHandler.sendEmptyMessage(MSG_RESULT);
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESHORDER, 2 * 60 * 60 * 1000);
                    }
                    uploadUnPayLog(0);
                    break;
                }
                case MSG_BUY_ONLY:
                    setBuyOnly();
                    break;
                case MSG_ERROR: {
                    Toast.makeText(PayOrderActivity.this, message_error, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
                case MSG_RESULT: {
                    if (status.equals(pay_success)) {
                        UserCenterUtils.initMemberStatus();
                        uploadUnPayLog(1);
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
                    if (Time >= 1) {
                        btn_dialog_ok.setText(getResources().getString(R.string.usercenter_pay_success_ok) + Time + getResources().getString(R.string.usercenter_pay_success_ok1));
                        Time--;
                        mHandler.sendEmptyMessageDelayed(MSG_RESULT_OK_TIME, 1000);
                    } else {
                        btn_dialog_ok.setText(getResources().getString(R.string.usercenter_pay_success_ok2));
                        setStartActivity();
                    }
                    break;
                case MSG_RESULT_OK: {
                    mHandler.sendEmptyMessage(MSG_RESULT_OK_TIME);
                    setExprefresh();
                    dialog.show();
                    UserCenterUtils.initMemberStatus();
                    break;
                }
                case MSG_SETMESSAGE: {
                    if (mProductPricesInfo != null) {
                        if (mProductPricesInfo.getResponse() != null) {
                            if (mProductPricesInfo.getResponse().getPrices() != null && mProductPricesInfo.getResponse().getPrices().size() > 0) {
                                ProductPricesInfo.ResponseBean.PricesBean pricesBean = mProductPricesInfo.getResponse().getPrices().get(position);
                                if (pricesBean == null) {
                                    return true;
                                }
                                //判断是否为连续包月，如是则隐藏支付宝
                                mIfContinued = pricesBean.isIfContinued();
                                Log.i(TAG, "handleMessage: mIfContinued="+mIfContinued);
                                if(mIfContinued){
                                    tv_ap.setVisibility(View.GONE);
                                }

                                ProductPricesInfo.ResponseBean.PricesBean.ActivityBean activityBean = pricesBean.getActivity();

                                try {
                                    if (isBuyOnly) {
                                        tv_name.setText(getResources().getString(R.string.usercenter_pay_product) + mTitle);
                                    } else {
                                        tv_name.setText(getResources().getString(R.string.usercenter_pay_product) + pricesBean.getName());
                                    }
                                    if (activityBean == null) {
                                        if (isBuyOnly) {
                                            if (isVip) {
                                                price = pricesBean.getVipPrice();
                                            } else {
                                                price = pricesBean.getPrice();
                                            }
                                        } else {
                                            price = pricesBean.getPrice();
                                        }
                                    } else {
                                        String actType = activityBean.getActType();
                                        if (TextUtils.equals(actType, ACTTYPE)) {
                                            if (isBuyOnly) {
                                                if (isVip) {
                                                    price = pricesBean.getVipPriceDiscount();
                                                } else {
                                                    price = pricesBean.getPriceDiscount();
                                                }
                                            } else {
                                                price = pricesBean.getPriceDiscount();
                                            }
                                        } else {
                                            if (isBuyOnly) {
                                                if (isVip) {
                                                    price = pricesBean.getVipPrice();
                                                } else {
                                                    price = pricesBean.getPrice();
                                                }
                                            } else {
                                                price = pricesBean.getPrice();
                                            }
                                        }
                                    }


                                    tv_price.setText(getResources().getString(R.string.usercenter_pay_price) + tranPrices(price) + getResources().getString(R.string.usercenter_pay_price_unit));
                                    long duration = pricesBean.getRealDuration() * 60 * 60 * 1000;
                                    Log.i(TAG, "duration :" + duration);
                                    Log.i(TAG, "expireTime :" + expireTime);
                                    expireTime_All = duration + expireTime;
                                    setTime(expireTime_All, tv_time, getResources().getString(R.string.usercenter_pay_time));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return true;
                                }
                            }
                        }
                        getPayQRCode();
                    }
                    break;
                }
                case MSG_REFRESHORDER:
                    getReFreshOrder(String.valueOf(orderId));
                    break;
            }
            return false;
        }
    });

    private String tranPrices(int price) {
        String strprice = BigDecimal.valueOf((long) price).divide(new BigDecimal(100)).toString();
        return strprice;
    }

    long time;

    private void setExprefresh() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> e) throws Exception {
                if (isBuyOnly) {
                    time = requestMemberInfo(mContentUUID);
                } else {
                    time = requestMemberInfo("");
                }
                Log.i(TAG, "setExprefresh : " + time);
                e.onNext(time);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        setTime(aLong, tv_dialog_time, getResources().getString(R.string.usercenter_pay_success_time));
                    }
                });

    }

    private void setTime(long time, TextView tv, String str) {
        Date date2 = new Date(time);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        tv.setText(str + format2.format(date2));
    }

    private void setStartActivity() {
        Intent intent = new Intent();
        Log.i(TAG, "mFlagAction: " + mFlagAction);
        if (TextUtils.isEmpty(mFlagAction)) {
            intent.setClass(PayOrderActivity.this, MemberCenterActivity.class);
        } else {
            intent.setClassName(this,
                    mFlagAction);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//关掉所要到的界面中间的activity
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳转的界面
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> applist = packageManager.queryIntentActivities(intent, 0);
        if (applist == null || applist.isEmpty()) {
            Toast.makeText(this, "no activity", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
        finish();
        Log.d(TAG, "setStartActivity---finish");
    }

    private void getPayQRCode() {

        JSONObject contentDTOForCheckObject = new JSONObject();
        try {
            if (isBuyOnly) {
                contentDTOForCheckObject.put("id", mContentUUID);
                contentDTOForCheckObject.put("name", mTitle);
                contentDTOForCheckObject.put("contentId", mContentID);
                contentDTOForCheckObject.put("contentType", mContentType);
                contentDTOForCheckObject.put("source", mMAMID);
            } else {
                contentDTOForCheckObject.put("id", "");
                contentDTOForCheckObject.put("source", "");
            }
            contentDTOForCheckObject.put("appKey", Libs.get().getAppKey());
            contentDTOForCheckObject.put("channelId", Libs.get().getChannelId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject terminalDTOObject = new JSONObject();
        Log.i(TAG, "IP: " + NetWorkUtils.getClientIP());
        try {
            terminalDTOObject.put("versionNo", DeviceUtil.getAppVersion(PayOrderActivity.this));
            terminalDTOObject.put("mac", SystemUtils.getMac(PayOrderActivity.this));
            terminalDTOObject.put("ip", NetWorkUtils.getClientIP());
            terminalDTOObject.put("sourceFrom", "TV");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mProductPricesInfo == null) {
            return;
        }
        if (mProductPricesInfo.getResponse() == null) {
            return;
        }
        if (mProductPricesInfo.getResponse().getPrices() == null || mProductPricesInfo.getResponse().getPrices().size() <= 0) {
            return;
        }
        JSONObject priceObject = new JSONObject();
        try {
            priceObject.put("id", mProductPricesInfo.getResponse().getPrices().get(position).getId());

            priceObject.put("price", price);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject paymentChannelObject = new JSONObject();
        try {
            paymentChannelObject.put("id", payChannelId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", mProductPricesInfo.getResponse().getId());
            jsonObject.put("type", mProductPricesInfo.getResponse().getPrdType());
            if (isBuyOnly) {
                jsonObject.put("source", mMAMID);
            } else {
                jsonObject.put("source", "");
            }


            jsonObject.put("contentCheckDTO", contentDTOForCheckObject);
            jsonObject.put("paymentChannelDTO", paymentChannelObject);
            jsonObject.put("priceDTO", priceObject);
            jsonObject.put("terminalDTO", terminalDTOObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Log.d("jsonObject.toString()", "----" + jsonObject.toString());
        getpay(requestBody);
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


    private void getReFreshOrder(String order) {

        String Authorization = "Bearer " + mToken;
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getRefreshOrder(Authorization, order)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_order = d;
                        }

                        @Override
                        public void onNext(ResponseBody value) {

                            try {
                                String data = value.string();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constant.TAG, "------onDestroy");
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mDisposable_price != null) {
            mDisposable_price.dispose();
            mDisposable_price = null;
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
        final View dialogView = LayoutInflater.from(PayOrderActivity.this)
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
    private long requestMemberInfo(String mContentUUID) {
        try {
            expireTime = 0;
            NetClient.INSTANCE.getUserCenterMemberInfoApi()
                    .getMemberInfo("Bearer " + mToken, "",
                            Libs.get().getAppKey(), mContentUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_time = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                checkUserOffline(data);
                                JSONArray jsonArray = new JSONArray(data);
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    Exp_time = jsonObject.optString("expireTime");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Exp_time));
                                    expireTime = calendar.getTimeInMillis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "---requestMemberInfo:onError");
                            if (mDisposable_time != null) {
                                mDisposable_time.dispose();
                                mDisposable_time = null;
                            }
                        }

                        @Override
                        public void dealwithUserOffline() {
                            Log.i(TAG, "dealwithUserOffline: ");
                            UserCenterUtils.userOfflineStartLoginActivity(PayOrderActivity.this);
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
        Log.i(TAG, "---expireTime：" + expireTime);
        Log.i(TAG, "---systemTime：" + TimeUtil.getInstance().getCurrentTimeInMillis());

        if (expireTime <= TimeUtil.getInstance().getCurrentTimeInMillis()) {
            expireTime = TimeUtil.getInstance().getCurrentTimeInMillis();
            if (TextUtils.isEmpty(mContentUUID)) {
                isVip = false;
            }

        } else {
            if (TextUtils.isEmpty(mContentUUID)) {
                isVip = true;
            }

        }

        return expireTime;
    }

    private void uploadUnPayLog(int action) {

        StringBuilder dataBuff = new StringBuilder(32);
        dataBuff.append(1 + ",")
                .append(action + ",")
                .append(mVipProductId + ",")
                .append(mProductPricesInfo.getResponse().getPrices().get(0).getPriceDiscount() + ",")
                .append(payChannelId + ",")
                .append(orderId)
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_PAY, dataBuff.toString());

    }

}
