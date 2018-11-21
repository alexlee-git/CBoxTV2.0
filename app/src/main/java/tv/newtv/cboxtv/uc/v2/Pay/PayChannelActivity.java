package tv.newtv.cboxtv.uc.v2.Pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.pay.ExterPayBean;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.member.MemberAgreementActivity;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2
 * 创建事件:     下午 4:20
 * 创建人:       caolonghe
 * 创建日期:     2018/9/12 0012
 */
public class PayChannelActivity extends Activity implements PageContract.View {

    private final String TAG = "PayChannelActivity";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProductAdapter mAdapter;
    private RelativeLayout rel_down;
    private TextView tv_agreement;
    private ImageView img_rights;
    private Disposable mDisposable_price, mDisposable_product, mDisposable_recom, mDisposable_time;
    private ProductPricesInfo mProductPricesInfo;
    private List<ProductPricesInfo.ResponseBean.PricesBean> prices;
    private final int MSG_PRICES = 1;
    private final int MSG_IMAGE = 2;
    private final int MSG_PRODUCT = 3;
    private String mVipProductId;
    private String mFlagAction;
    private ModuleInfoResult moduleInfoResult;
    private final String prdType = "3";
    private String mContentUUID, mMAMID, mVipFlag, mTitle, mContentType;
    private String mToken;
    private ExterPayBean mExterPayBean;
    private PageContract.ContentPresenter mContentPresenter;
    private final String ACTTYPE = "DISCOUNT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paychannel);

        init();
        mExterPayBean = (ExterPayBean) getIntent().getSerializableExtra("payBean");
        if (mExterPayBean != null) {
            Log.e(TAG, mExterPayBean.toString());
            mVipProductId = mExterPayBean.getVipProductId();
            mContentUUID = mExterPayBean.getContentUUID();
            mContentType = mExterPayBean.getContentType();
            mMAMID = mExterPayBean.getMAMID();
            mTitle = mExterPayBean.getTitle();
            mFlagAction = mExterPayBean.getAction();
            mVipFlag = mExterPayBean.getVipFlag();
        }
        Log.e(TAG, "mVipProductId: " + mVipProductId + "---mFlagAction: " + mFlagAction);
        if (!TextUtils.isEmpty(Constant.ID_PAGE_MEMBER)) {
            mContentPresenter = new PageContract.ContentPresenter(getApplicationContext(), this);
            mContentPresenter.getPageContent(Constant.ID_PAGE_MEMBER);
        } else {
            Log.e(TAG, "wqs:ID_PAGE_MEMBER==null");
        }
        //requestRecommendData();
        initAgreemrntView();

        if (mVipProductId == null) {
            requestProductData();
        } else {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "5," + mVipProductId);
            if (TextUtils.equals(Constant.BUY_VIPANDONLY, mVipFlag)) {
                getProductPrice(mVipProductId, Libs.get().getAppKey(), prdType, Libs.get().getChannelId());
            } else {
                getProductPriceOnly(mVipProductId, Libs.get().getChannelId());
            }
        }
    }

    private void init() {

        mRecyclerView = findViewById(R.id.paychannel_recyclerview);
        tv_agreement = findViewById(R.id.paychannel_tv_agreement);
        rel_down = findViewById(R.id.paychannel_rel_down);
        img_rights = findViewById(R.id.paychannel_image_rights);
        mAdapter = new ProductAdapter(PayChannelActivity.this);
        linearLayoutManager = new LinearLayoutManager(PayChannelActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setDescendantFocusability(RecyclerView.FOCUS_AFTER_DESCENDANTS);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);

        tv_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayChannelActivity.this, MemberAgreementActivity.class));
            }
        });
    }

    private void initAgreemrntView() {
        if (TextUtils.equals(Constant.BUY_VIPANDONLY, mVipFlag)) {
            rel_down.setVisibility(View.VISIBLE);
            tv_agreement.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            if (mVipProductId != null) {
                                Intent mIntent = new Intent(PayChannelActivity.this, PayOrderActivity.class);
                                mIntent.putExtra("payBean", mExterPayBean);
                                startActivity(mIntent);
                            }
                        }
                    }
                    return false;
                }
            });
        } else {
            rel_down.setVisibility(View.INVISIBLE);
        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_PRICES:
                    if (mProductPricesInfo != null) {
                        if (mProductPricesInfo.getResponse() != null) {
                            prices = mProductPricesInfo.getResponse().getPrices();
                            if (prices != null && prices.size() > 0) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case MSG_IMAGE:
//                    inflateData();
                    break;
                case MSG_PRODUCT:
                    if (TextUtils.equals(Constant.BUY_VIPANDONLY, mVipFlag)) {
                        getProductPrice(mVipProductId, Libs.get().getAppKey(), prdType, Libs.get().getChannelId());
                    } else {
                        getProductPriceOnly(mVipProductId, Libs.get().getChannelId());
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    public void onPageResult(@org.jetbrains.annotations.Nullable List<Page> page) {
        inflateData(page);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @org.jetbrains.annotations.Nullable String desc) {

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }


    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder> {

        private LayoutInflater mLayoutInflater;

        public ProductAdapter(Context mContext) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public ProductAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item_paychannel_product, null);
            return new ProductAdapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductAdapter.MyHolder holder, final int position) {

            if (prices == null && prices.size() <= 0) {
                return;
            }

            ProductPricesInfo.ResponseBean.PricesBean pricesBean = prices.get(position);
            if (pricesBean == null) {
                return;
            }
            holder.tv_name.setText(pricesBean.getName());
            int price = pricesBean.getPrice();
            ProductPricesInfo.ResponseBean.PricesBean.ActivityBean activityBean = prices.get(position).getActivity();
            Log.i(TAG, "activityBean: " + activityBean);
            if (activityBean == null) {
                holder.img_product_mark.setVisibility(View.INVISIBLE);
                holder.tv_price_discount.setVisibility(View.INVISIBLE);
                holder.tv_discount.setVisibility(View.INVISIBLE);
                holder.img_discount_price.setVisibility(View.INVISIBLE);
                holder.tv_price.setText(tranPrices(price));
            } else {
                String actType = activityBean.getActType();
                if (TextUtils.equals(actType, ACTTYPE)) {
                    holder.img_product_mark.setVisibility(View.VISIBLE);
                    holder.tv_price_discount.setVisibility(View.VISIBLE);
                    holder.tv_discount.setVisibility(View.VISIBLE);
                    holder.img_discount_price.setVisibility(View.VISIBLE);
                    int price_discount = pricesBean.getPriceDiscount();
                    int percentage = activityBean.getPercentage() / 10;
                    holder.tv_price_discount.setText("已省" + (price - price_discount) + "元");
                    holder.tv_discount.setText(percentage + "折");
                    holder.tv_price.setText(tranPrices(price_discount));
                } else {
                    holder.img_product_mark.setVisibility(View.INVISIBLE);
                    holder.tv_price_discount.setVisibility(View.INVISIBLE);
                    holder.tv_discount.setVisibility(View.INVISIBLE);
                    holder.img_discount_price.setVisibility(View.INVISIBLE);
                    holder.tv_price.setText(tranPrices(price));
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PayChannelActivity.this, PayOrderActivity.class);
                    intent.putExtra("data", (Serializable) mProductPricesInfo);
                    intent.putExtra("Postion", position);
                    intent.putExtra("payBean", mExterPayBean);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return prices == null ? 0 : prices.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            private ImageView img_product_mark, img_discount_price;
            private TextView tv_name, tv_price, tv_discount, tv_price_discount;


            public MyHolder(View itemView) {
                super(itemView);

                img_product_mark = itemView.findViewById(R.id.paychannel_item_product_mark);
                img_discount_price = itemView.findViewById(R.id.paychannel_item_discount);
                tv_price = itemView.findViewById(R.id.paychannel_item_tv_price);
                tv_name = itemView.findViewById(R.id.paychannel_item_price_name);
                tv_price_discount = itemView.findViewById(R.id.paychannel_item_price_discount);
                tv_discount = itemView.findViewById(R.id.paychannel_item_discount_price);

            }
        }
    }

    private String tranPrices(int price) {
        String strprice = BigDecimal.valueOf((long) price).divide(new BigDecimal(100)).toString();
        return strprice;
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
                                    mHandler.sendEmptyMessage(MSG_PRICES);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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

    private void getProductPrice(String prdId, String appkey, String prdType, String channelId) {

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
                                    mHandler.sendEmptyMessage(MSG_PRICES);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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


    //获取推荐位数据
    private void requestRecommendData() {
        try {
            NetClient.INSTANCE.getPageDataApi().getPageData(Libs.get().getAppKey(), Libs.get().getChannelId(), Constant.ID_PAGE_MEMBER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {

                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable_recom = d;
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    Gson mGSon = new Gson();
                    try {
                        String value = responseBody.string();
                        Log.d(TAG, "---requestRecommendData:value:" + value);
                        moduleInfoResult = mGSon.fromJson(value, ModuleInfoResult.class);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MSG_IMAGE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (mDisposable_recom != null) {
                        mDisposable_recom.dispose();
                        mDisposable_recom = null;
                    }
                }

                @Override
                public void onComplete() {
                    if (mDisposable_recom != null) {
                        mDisposable_recom.dispose();
                        mDisposable_recom = null;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取产品包
    private void requestProductData() {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi().getProduct(Libs.get().getAppKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable_product = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String value = responseBody.string();
                                Log.d(TAG, "---requestProductData:value:" + value);
                                JSONObject mJsonObject = new JSONObject(value);
                                JSONObject jsonObject = mJsonObject.getJSONObject("response");
                                mVipProductId = String.valueOf(jsonObject.optInt("productId"));
                                Log.e(TAG, "---mVipProductId:value:" + mVipProductId);
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "5," + mVipProductId);
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_PRODUCT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mDisposable_product != null) {
                                mDisposable_product.dispose();
                                mDisposable_product = null;
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mDisposable_product != null) {
                                mDisposable_product.dispose();
                                mDisposable_product = null;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    //读取用户会员信息
//    private long requestMemberInfo() {
//        try {
//            NetClient.INSTANCE.getUserCenterMemberInfoApi()
//                    .getMemberInfo("Bearer " + mToken, "",
//                            Libs.get().getAppKey())
//                    .subscribe(new Observer<ResponseBody>() {
//
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                            mDisposable_time = d;
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            try {
//                                String data = responseBody.string();
//                                JSONArray jsonArray = new JSONArray(data);
//                                if (jsonArray != null && jsonArray.length() > 0) {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
//                                    String Exp_time = jsonObject.optString("expireTime");
//                                    Log.e(TAG, "---expireTime：" + Exp_time);
//                                    Calendar calendar = Calendar.getInstance();
//                                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Exp_time));
//                                    System.out.println("日期[2018-11-12 17:08:12]对应毫秒：" + calendar.getTimeInMillis());
//                                    expireTime = calendar.getTimeInMillis();
//                                    Log.e(TAG, "---expireTime：" + expireTime);
//                                    Log.e(TAG, "---systemTime：" + TimeUtil.getInstance().getCurrentTimeInMillis());
//                                    if (expireTime <= TimeUtil.getInstance().getCurrentTimeInMillis()) {
//                                        isVip = false;
//                                    } else {
//                                        isVip = true;
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.e(TAG, "---requestMemberInfo:onError");
//                            expireTime = TimeUtil.getInstance().getCurrentTimeInMillis();
//                            if (mDisposable_time != null) {
//                                mDisposable_time.dispose();
//                                mDisposable_time = null;
//                            }
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            if (mDisposable_time != null) {
//                                mDisposable_time.dispose();
//                                mDisposable_time = null;
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return expireTime;
//    }


    /**
     * adapter填充数据
     *
     * @param
     */
    private void inflateData(List<Page> pageList) {
        Log.d(TAG, "wqs:inflateData");
        UserCenterPageBean.Bean mProgramInfo = null;
        if (pageList == null && pageList.size() <= 0) {
            return;
        }
        try {
            if (pageList.size() >= 2) {
                List<Program> programInfoList = pageList.get(1).getPrograms();
                mProgramInfo = new UserCenterPageBean.Bean();
                mProgramInfo._title_name = programInfoList.get(0).getTitle();
                mProgramInfo._contentuuid = programInfoList.get(0).getL_id();
                mProgramInfo._contenttype = programInfoList.get(0).getL_contentType();
                mProgramInfo._imageurl = programInfoList.get(0).getImg();
                mProgramInfo._actiontype = programInfoList.get(0).getL_actionType();
                if (!TextUtils.isEmpty(mProgramInfo._imageurl)) {
                    Picasso.get().load(mProgramInfo._imageurl)
                            .error(R.drawable.default_member_center_1680_200_v2)
                            .into(img_rights);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "---inflateData:Exception:" + e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable_time != null) {
            mDisposable_time.dispose();
            mDisposable_time = null;
        }
        if (mDisposable_product != null) {
            mDisposable_product.dispose();
            mDisposable_product = null;
        }
        if (mDisposable_price != null) {
            mDisposable_price.dispose();
            mDisposable_price = null;
        }
        if (mDisposable_recom != null) {
            mDisposable_recom.dispose();
            mDisposable_recom = null;
        }
    }
}
