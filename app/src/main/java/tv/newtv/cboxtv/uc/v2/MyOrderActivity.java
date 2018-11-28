package tv.newtv.cboxtv.uc.v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.BaseActivity;

import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.SplashActivity;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.OrderInfoBean;
import tv.newtv.cboxtv.uc.listener.RecScrollListener;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayRefreshOrderActivity;


/**
 * 项目名称：CBoxTV2.0
 * 包名：tv.newtv.cboxtv.uc.v2
 * 文件描述：我的订单
 * 作者：lxq
 * 创建时间：2018/9/10
 * 更改时间：2018/9/10
 */
public class MyOrderActivity extends BaseActivity {
    @BindView(R.id.user_info_title)
    TextView userInfoTitle;
    @BindView(R.id.menu_group)
    RadioGroup menuGroup;
    @BindView(R.id.ll_order_title)
    LinearLayout llOrderTitle;
    @BindView(R.id.line_single)
    TextView lineSingle;
    @BindView(R.id.recycle_order)
    RecyclerView recycleOrder;
    @BindView(R.id.rl_order_all)
    RelativeLayout rlOrderAll;
    @BindView(R.id.tv_go_buy)
    TextView tvGoBuy;
    @BindView(R.id.ll_empty_view)
    LinearLayout llEmptyView;

    private LinearLayoutManager linearLayoutManager;
    private MyOrderAdapter mAdapter;
    private List<OrderInfoBean.OrdersBean> mDOrdersBeans = new ArrayList<>();
    private static final String TAG = "MyOrderActivity";
    private String mAccessToken;
    private OrderInfoBean orderInfoBean;
    private int totalPages = 0;
    private int pageNum = 50;
    private int offset = 1;//页数
    private final static int REQUEST_CODE = 1; // 返回的结果码
    private boolean isBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        isBackground = ActivityStacks.get().isBackGround();
        initData();
        initView();
    }

    private void initView() {
        userInfoTitle.setText("我的订单");
        linearLayoutManager = new LinearLayoutManager(this);
        recycleOrder.setLayoutManager(linearLayoutManager);
        mAdapter = new MyOrderAdapter();
        mAdapter.setHasStableIds(true);
        recycleOrder.setAdapter(mAdapter);
        recycleOrder.setItemAnimator(null);
        tvGoBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToPayChannel();
            }
        });
        recycleOrder.addOnScrollListener(new RecScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int currentPage) {
                Log.e(TAG, "---onLoadMore---: currentPage=" + currentPage);
                Log.e(TAG, "---onLoadMore---: offset=" + offset);
                if (offset == totalPages) {
                    return;
                }
                offset++;
                getOrderList();
            }
        });
    }

    private void initData() {
        //用观察者模式获取Token值，为调用接口传递参数
        Observable.create(new ObservableOnSubscribe<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(MyOrderActivity.this);
                Log.d(TAG, "---isTokenRefresh:status:" + status);
                //获取登录状态
                mAccessToken = SharePreferenceUtils.getToken(getApplicationContext());
                if (!TextUtils.isEmpty(mAccessToken)) {
                    e.onNext(mAccessToken);
                } else {
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(String value) throws Exception {
                        Log.e(TAG, "====accept:=======TokenValue= " + value);
                        if (!TextUtils.isEmpty(value)) {
                            getOrderList();
                        } else {
                            //Token为空
                            showEmptyView();
                            Log.e(TAG, "---accept----Token为空--showEmptyView()");
                        }

                    }
                });
    }

    /**
     * 我的订单-Adapter
     */
    class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyOrderActivity.this).inflate(R.layout.item_order_layout, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (mDOrdersBeans == null || mDOrdersBeans.size() <= 0) {
                return;
            }
            final OrderInfoBean.OrdersBean mResultListBean = mDOrdersBeans.get(position);
            String status = mResultListBean.getStatus();
            if (status != null && status.length() > 0) {
                if (status.equals("PAY_SUCCESS")) {
                    holder.tvPayResult.setText("已支付");
                } else if (status.equals("ORDER_SUCCESS")) {
                    holder.tvPayResult.setText("未支付");
                }
            }
            holder.tvResumeContent.setText(mResultListBean.getProductName());
            String payTime = mResultListBean.getPayTime();
            String createTime = mResultListBean.getCreateTime();
            String expireTime = mResultListBean.getExpireTime();
            if (TextUtils.isEmpty(payTime)) {
                //购买时间为空时显示订单生成时间
                if (!TextUtils.isEmpty(createTime)){
                    holder.tvBuyDate.setText(transFormatDate(mResultListBean.getCreateTime()));
                }
            } else {
                holder.tvBuyDate.setText(transFormatDate(payTime));
            }
            if (!TextUtils.isEmpty(expireTime)){
                holder.tvVisibleDate.setText(transFormatDate(expireTime));
            }
            if (TextUtils.equals(status, "ORDER_SUCCESS")) {
                //订单为支付且在订单有效期内显示去支付,重新购买不显示不跳转
                if (IsToday(mResultListBean.getTranExpireTime())) {
                    holder.tvOperation.setVisibility(View.VISIBLE);
                    holder.tvOperation.setText("去支付");
                } else {
                    holder.tvOperation.setVisibility(View.INVISIBLE);
                }
            } else if (TextUtils.equals(status, "PAY_SUCCESS")) {
                //已支付的
                holder.tvOperation.setVisibility(View.VISIBLE);
                holder.tvOperation.setText("去观看");
            }
            Log.e(TAG, "onBindViewHolder:position=" + position);
            if (getItemCount() > 6 && position == 5) {
                //初始进入页面时若数据超过6条则当前页显示的最后一条数据为灰色，若用户下移查看过数据后则都恢复常态
                Log.e(TAG, "==当前页后一个==onBindViewHolder:position=" + position);
                changeTextColor(holder.tvResumeContent, 2);
                changeTextColor(holder.tvBuyDate, 2);
                changeTextColor(holder.tvVisibleDate, 2);
                changeTextColor(holder.tvPriceValue, 2);
                changeTextColor(holder.tvPayResult, 2);
                changeTextColor(holder.tvOperation, 2);
            } else {
                changeTextColor(holder.tvResumeContent, 0);
                changeTextColor(holder.tvBuyDate, 0);
                changeTextColor(holder.tvVisibleDate, 0);
                changeTextColor(holder.tvPriceValue, 0);
                changeTextColor(holder.tvPayResult, 0);
                changeTextColor(holder.tvOperation, 0);
            }
            try {
                String price = BigDecimal.valueOf((long) mResultListBean.getAmount()).divide(new BigDecimal(100)).toString();
                holder.tvPriceValue.setText(price + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        changeTextColor(holder.tvResumeContent, 1);
                        changeTextColor(holder.tvBuyDate, 1);
                        changeTextColor(holder.tvVisibleDate, 1);
                        changeTextColor(holder.tvPriceValue, 1);
                        changeTextColor(holder.tvPayResult, 1);
                        changeTextColor(holder.tvOperation, 1);
                        holder.lineItemBottom.setVisibility(View.INVISIBLE);
                        holder.llOrderItem.setSelected(true);
                        Log.e(TAG, "---hasFocus---detail---RecyclerView" + position);

                    } else {
                        holder.llOrderItem.setSelected(false);
                        holder.lineItemBottom.setVisibility(View.VISIBLE);
                        changeTextColor(holder.tvResumeContent, 0);
                        changeTextColor(holder.tvBuyDate, 0);
                        changeTextColor(holder.tvVisibleDate, 0);
                        changeTextColor(holder.tvPriceValue, 0);
                        changeTextColor(holder.tvPayResult, 0);
                        changeTextColor(holder.tvOperation, 0);
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "---setOnClickListener--position=" + position);
                    String payStatus = mResultListBean.getStatus();
                    if (TextUtils.equals(payStatus, "ORDER_SUCCESS") && IsToday(mResultListBean.getTranExpireTime())) {
                        //跳转支付页
                        Log.e(TAG, "---setOnClickListener--payStatus=" + payStatus);
                        jumpToPay(mResultListBean);

                    } else if (TextUtils.equals(payStatus, "PAY_SUCCESS")) {
                        int productType = mResultListBean.getProductType();//1：会员或单点;3：会员VIP;4：单点
                        String contentType = mResultListBean.getContentType();
                        String mediaId = mResultListBean.getMediaId();
                        Log.e(TAG, "---setOnClickListener--productType=" + productType + ";mediaId=" + mediaId + ";contentType =" + contentType);
                        if (productType == 4) {
                            //点播详情页
                            //String contentType = "PS";
                            //String mediaId = "31133";
                            JumpUtil.detailsJumpActivity(MyOrderActivity.this, contentType, mediaId);

                        } else if (productType == 1 || productType == 3) {
                            //与产品商议过1和3跳转到会员Vip列表
                            jumpToMemberVip();
                        }
                    }
                }
            });
        }

        /**
         * 改变文字颜色
         *
         * @param textView
         * @param type     0-常态；1-焦点选中状态；2-当前页最后一项（字体灰色）
         */
        private void changeTextColor(TextView textView, int type) {
            if (type == 0) {
                textView.setTextColor(getResources().getColor(R.color.colorWhite));
            } else if (type == 1) {
                textView.setTextColor(getResources().getColor(R.color.color_order_focus_text));
            } else if (type == 2) {
                textView.setTextColor(getResources().getColor(R.color.color_white_30));
            }
        }

        @Override
        public int getItemCount() {
            return mDOrdersBeans != null ? mDOrdersBeans.size() : 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_resume_content)
            TextView tvResumeContent;
            @BindView(R.id.tv_buy_date)
            TextView tvBuyDate;
            @BindView(R.id.tv_visible_date)
            TextView tvVisibleDate;
            @BindView(R.id.tv_price_value)
            TextView tvPriceValue;
            @BindView(R.id.tv_pay_result)
            TextView tvPayResult;
            @BindView(R.id.tv_operation)
            TextView tvOperation;
            @BindView(R.id.ll_order_item)
            LinearLayout llOrderItem;
            @BindView(R.id.line_item_bottom)
            TextView lineItemBottom;
            @BindView(R.id.rl_item_layout)
            RelativeLayout rlItemLayout;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, itemView);
            }

        }
    }

    /**
     * 跳转到支付页
     */
    private void jumpToPay(OrderInfoBean.OrdersBean ordersBean) {

        int productId = ordersBean.getProductId();
        int productType = ordersBean.getProductType();
        int orderId = ordersBean.getId();
        int payChannelId = ordersBean.getPayChannelId();
        int amount = ordersBean.getAmount();
        int duration = ordersBean.getDuration();
        String mediaId = ordersBean.getMediaId();
        String contentType = ordersBean.getContentType();
        String productName = ordersBean.getProductName();

        Intent intent = new Intent(MyOrderActivity.this, PayRefreshOrderActivity.class);
        intent.putExtra("productId", productId);
        intent.putExtra("productType", productType);
        intent.putExtra("orderId", orderId);
        intent.putExtra("payChannelId", payChannelId);
        intent.putExtra("amount", amount);
        intent.putExtra("duration", duration);
        intent.putExtra("mediaId", mediaId);
        intent.putExtra("contentType", contentType);
        intent.putExtra("productName", productName);
        intent.putExtra("action", "android.intent.action.ORDER");
        startActivityForResult(intent, REQUEST_CODE);
    }

    // 为了获取结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Log.e(TAG, "onActivityResult: 0000");
                String isPay = data.getStringExtra("isPaySuccess");
                Log.e(TAG, "onActivityResult: ======isPay=" + isPay);
                if (TextUtils.equals(isPay, "yes")) {
                    Log.e(TAG, "onActivityResult: ====11111==isPay=" + isPay);
                    offset = 1;
                    getOrderList();
                }

            }
        }
    }

    /**
     * 跳转到会员购买页
     */
    private void jumpToPayChannel() {
        //会员片库
        Intent intent = new Intent();
        Class mPageClass = PayChannelActivity.class;
        if (mPageClass == null) {
            return;
        }
        intent.setClass(MyOrderActivity.this, mPageClass);
        startActivity(intent);
    }

    /**
     * 跳转到会员Vip列表
     */
    private void jumpToMemberVip() {
        //会员片库
        Intent intent = new Intent();
        Class mPageClass = null;
        String centerParams = BootGuide.getBaseUrl(BootGuide.MEMBER_CENTER_PARAMS);
//        Constant.MEMBER_CENTER_PARAMS = Constant.getBaseUrl(AppHeadersInterceptor.MEMBER_CENTER_PARAMS);
        if (!TextUtils.isEmpty(centerParams)) {
            intent.putExtra("action", "panel");
            intent.putExtra("params", centerParams);
            Log.d(TAG, "---MEMBER_CENTER_PARAMS:action:panel----params:" + centerParams);
            mPageClass = MainActivity.class;
        } else {
            Toast.makeText(this, "请配置跳转参数", Toast.LENGTH_LONG).show();
        }
        if (mPageClass == null) {
            return;
        }
        intent.setClass(MyOrderActivity.this, mPageClass);
        startActivity(intent);

        if (mPageClass == MainActivity.class) {
            if (!isBackground){
            ActivityStacks.get().finishAllActivity();
            }
            MyOrderActivity.this.finish();
        }
    }

    /**
     * 获取订单数据
     */
    private void getOrderList() {
        Log.e(TAG, "-----getOrderList: ----offset=" + offset);
        NetClient.INSTANCE.getOrdersApi()
                .getOrders("Bearer " + mAccessToken, Libs.get().getAppKey(), Libs.get().getChannelId(), "", "", offset + "", pageNum + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "====onSubscribe===");
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            String data = value.string();
                            Log.e(TAG, "data=" + data);
                            Gson gson = new Gson();
                            orderInfoBean = gson.fromJson(data, OrderInfoBean.class);
                            if (orderInfoBean != null) {
                                Log.e(TAG, "----------orderInfoBean:" + orderInfoBean.toString());
                                Log.e(TAG, "----------orderCount:" + orderInfoBean.getTotal());
                                if (orderInfoBean.getOrders() != null) {
                                    if (offset == 1) {
                                        mDOrdersBeans.clear();
                                    }
                                    mDOrdersBeans.addAll(orderInfoBean.getOrders());
                                }
                                Log.e(TAG, "----------mDOrdersBeans=" + mDOrdersBeans.toString());
                                if (offset == 1){
                                    totalPages = (orderInfoBean.getTotal() + (pageNum - 1)) / pageNum;
                                    Log.e(TAG, "----------totalPages:" + totalPages);
                                    if (orderInfoBean.getTotal() == 0 || mDOrdersBeans == null || mDOrdersBeans.size() == 0) {
                                        showEmptyView();
                                    }else {
                                        rlOrderAll.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (offset == 1) {
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    int positionStart = pageNum * (offset - 1);
                                    mAdapter.notifyItemRangeChanged(positionStart, positionStart + pageNum);
                                }
                            } else {
                                showEmptyView();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("==onError===", "onError");
                        if (offset == 1) {
                            showEmptyView();
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "====onComplete===");
                    }
                });
    }

    /**
     * 时间格式显示规范
     *
     * @param inTime
     * @return
     */
    private String transFormatDate(String inTime) {
//        if (TextUtils.isEmpty(inTime)) {
//            inTime = "2018-08-23 17:23:02";
//        }
        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat s2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date tempDate = null;
        String outTime = null;
        try {
            tempDate = s1.parse(inTime);
            outTime = s2.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outTime;
    }

    /**
     * 显示空白页
     */
    private void showEmptyView() {
        llEmptyView.setVisibility(View.VISIBLE);
        rlOrderAll.setVisibility(View.GONE);
        tvGoBuy.requestFocus();
    }

    /**
     * 判断当前时间是否在某一时间内
     *
     * @param day
     * @return
     * @throws ParseException
     */
    private boolean IsToday(String day) {
        if (TextUtils.isEmpty(day)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 当前的时刻
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        // 设定的时刻
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            int diffHour = cal.get(Calendar.HOUR_OF_DAY) - pre.get(Calendar.HOUR_OF_DAY);
            int diffMin = cal.get(Calendar.MINUTE) - pre.get(Calendar.MINUTE);
            if (diffDay == 0) {
                if (diffHour == 0) {
                    if (diffMin >= 0) {
                        return true;
                    }
                } else if (diffHour > 0) {
                    return true;
                }
            } else if (diffDay > 0) {
                return true;
            }
        } else if (cal.get(Calendar.YEAR) > (pre.get(Calendar.YEAR))) {
            return true;
        }
        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        //订单页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "1");
    }
}
