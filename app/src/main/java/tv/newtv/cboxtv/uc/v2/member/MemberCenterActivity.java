package tv.newtv.cboxtv.uc.v2.member;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.MemberInfoBean;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.CodeExChangeActivity;
import tv.newtv.cboxtv.uc.v2.LoginActivity;
import tv.newtv.cboxtv.uc.v2.MyOrderActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.utils.BaseObserver;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.widget.ScrollSpeedLinearLayoutManger;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2.member
 * 类描述：会员中心界面
 * 创建人：wqs
 * 创建时间：11:07
 * 创建日期：2018/9/11
 * 修改人：
 * 修改时间：
 * 修改日期：
 * 修改备注：
 */
public class MemberCenterActivity extends BaseActivity implements OnRecycleItemClickListener<UserCenterPageBean.Bean>, PageContract.View {
    private final String TAG = "MemberCenterActivity";
    public static final int HEAD = 0;
    public static final int RECOMMEND_PROMOTION = 1;//会员促销推荐位
    public static final int RECOMMEND_INTERESTS = 2;//会员权益介绍推荐位
    public static final int RECOMMEND_DRAMA = 3;//会员片库推荐
    public static final int RECOMMEND = 4;//推荐位
    public static final int QR_CODE_INVALID = 5;//推荐位
    private MemberCenterRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<UserCenterPageBean> pageData;
    private MemberCenterAdapter mAdapter;
    private MemberHandler mHandler;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private Disposable mUserInfoDisposable;  //用户信息
    private Disposable mMemberInfoDisposable;//会员信息
    private Disposable mRecommendDisposable;//推荐位
    private Disposable mQrCodeDisposable;//推荐位
    private MemberInfoBean mMemberInfoBean;
    private String mLoginTokenString;//登录token,用于判断登录状态
    private PageContract.ContentPresenter mContentPresenter;
    private String Authorization;
    private QrcodeUtil mQrCodeUtil;
    private String deviceCode;
    private String qrCode;
    private int expires;
    private Bitmap mBitmap;
    private ImageView mFullQrCodeImageView;
    private boolean isBackground;
    private String mobileString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter_member_center_v2);
        isBackground = ActivityStacks.get().isBackGround();
        init();
    }

    private void init() {
        mRecyclerView = (MemberCenterRecyclerView) findViewById(R.id.id_member_center_root);
        mEmptyView = (TextView) findViewById(R.id.id_member_center_empty_view);
        mRecyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(LauncherApplication
                .AppContext));
        mHandler = new MemberHandler(this);
        pageData = new ArrayList<>();
        pageData.add(new UserCenterPageBean(""));
        pageData.add(new UserCenterPageBean(""));
        pageData.add(new UserCenterPageBean(""));
        mAdapter = new MemberCenterAdapter(this, this);
        mAdapter.setHasStableIds(true);
        mAdapter.appendToList(pageData);
        mRecyclerView.setAdapter(mAdapter);
        mPopupView = LayoutInflater.from(this).inflate(R.layout.activity_usercenter_member_center_qr_code_full_screen_v2, null);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mFullQrCodeImageView = mPopupView.findViewById(R.id.id_member_center_full_screen_qr_code);
        mPopupWindow = new PopupWindow(mPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
        String idPageNumber = BootGuide.getBaseUrl(BootGuide.PAGE_MEMBER);
        if (!TextUtils.isEmpty(idPageNumber)) {
            //获取推荐位数据
            mContentPresenter = new PageContract.ContentPresenter(getApplicationContext(), this);
            mContentPresenter.getPageContent(idPageNumber);
        } else {
            Log.d(TAG, "wqs:ID_PAGE_MEMBER==null");
        }
        mQrCodeUtil = new QrcodeUtil();
    }


    private void getUserInfo(String Authorization) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getUser(Authorization)
                    .subscribe(new BaseObserver<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.i(TAG, "onSubscribe: ");
                            unUserInfoSubscribe();
                            mUserInfoDisposable = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                Log.i(TAG, "onNext: ");
                                String result = responseBody.string();
                                JSONObject jsonObject = new JSONObject(result);
                                mobileString = jsonObject.optString("mobile");
                                if (mobileString.length() == 11) {
                                    mobileString = mobileString.substring(0, 3) + "xxxx" + mobileString.substring(7, 11);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mobileString = "";
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "onError: ");
                            super.onError(e);
                            mobileString = "";
                            unUserInfoSubscribe();
                        }

                        @Override
                        public void dealwithUserOffline() {
                            Log.i(TAG, "dealwithUserOffline: ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserCenterUtils.userOfflineStartLoginActivity(MemberCenterActivity.this);

                                }
                            });
                        }

                        @Override
                        public void onComplete() {
                            unUserInfoSubscribe();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取用户登录状态
    private void requestUserInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(MemberCenterActivity.this);
                Log.d(TAG, "wqs:isTokenRefresh:status:" + status);
                //获取登录状态
                mLoginTokenString = SharePreferenceUtils.getToken(getApplicationContext());
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    getUserInfo("Bearer " + mLoginTokenString);
                    if (!TextUtils.isEmpty(mobileString)) {
                        e.onNext(mLoginTokenString);
                    } else {
                        e.onNext("");
                    }
                } else {
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(String value) throws Exception {
                        if (!TextUtils.isEmpty(value)) {
                            //用户已登录
                            Log.d(TAG, "wqs:requestUserInfo:loginStatus:true:requestMemberInfo");
                            //获取用户会员信息
                            requestMemberInfo();
                        } else {
                            //用户未登录
                            Log.d(TAG, "wqs:requestUserInfo:loginStatus:false:not requestMemberInfo");
                        }
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(HEAD);
                        } else {
                            Log.d(TAG, "wqs:requestUserInfo:mHandler == null");
                        }
                    }
                });
    }

    //读取用户会员信息
    private void requestMemberInfo() {
        try {
            NetClient.INSTANCE.getUserCenterMemberInfoApi().getMemberInfo("Bearer " + mLoginTokenString, "", Libs.get().getAppKey(), "").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {

                @Override
                public void onSubscribe(Disposable d) {
                    unMemberInfoSubscribe();
                    mMemberInfoDisposable = d;
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    String memberInfo = null;
                    try {
                        memberInfo = responseBody.string();
                        Log.d(TAG, "wqs:requestMemberInfo:onNext:" + memberInfo);
                        JSONArray jsonArray = new JSONArray(memberInfo);
                        if (jsonArray != null && jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            mMemberInfoBean = new MemberInfoBean();
                            mMemberInfoBean.setId(jsonObject.optInt("id"));
                            mMemberInfoBean.setAppKey(jsonObject.optString("appKey"));
                            mMemberInfoBean.setUserId(jsonObject.optInt("userId"));
                            mMemberInfoBean.setProductId(jsonObject.optInt("productId"));
                            mMemberInfoBean.setExpireTime(jsonObject.optString("expireTime"));
                        } else {
                            Log.d(TAG, "wqs:requestMemberInfo:next:memberInfo==null");
                        }
                        unMemberInfoSubscribe();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(HEAD);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "wqs:requestMemberInfo:onError");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(HEAD);
                    } else {
                        Log.d(TAG, "wqs:requestMemberInfo:mHandler == null");
                    }
                    unMemberInfoSubscribe();
                }

                @Override
                public void onComplete() {
                    unMemberInfoSubscribe();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            unMemberInfoSubscribe();
            Log.e(TAG, "wqs:requestMemberInfo:Exception:" + e.toString());
        }
    }

    /**
     * 获取M站购买二维码信息
     *
     * @param Authorization
     * @param response_type
     * @param client_id
     */
    private void requestQrCodeInfo(String Authorization, String response_type, String client_id) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getMemberQRCode(Authorization, response_type, client_id, Libs.get().getChannelId(), "vipInfo")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            unQrCodeSubscribe();
                            mQrCodeDisposable = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login Qrcode :" + data.toString());
                                JSONObject mJsonObject = new JSONObject(data);
                                deviceCode = mJsonObject.optString("device_code");
                                qrCode = mJsonObject.optString("veriﬁcation_uri_complete");
                                expires = mJsonObject.optInt("expires_in");
                                if (mAdapter != null) {
                                    mAdapter.setQrCodeImageView(qrCode);
                                }
                                if (!TextUtils.isEmpty(qrCode)) {
                                    mQrCodeUtil.createQRImage(qrCode, getResources().getDimensionPixelOffset(R.dimen.height_617px),
                                            getResources().getDimensionPixelOffset(R.dimen.height_617px), mBitmap, mFullQrCodeImageView);
                                } else {
                                    mFullQrCodeImageView.setBackgroundResource(R.drawable.default_member_center_full_screen_qr_code_v2);
                                }
                                if (expires > 0) {
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessageDelayed(QR_CODE_INVALID, expires * 1000);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                            unQrCodeSubscribe();
                            if (mAdapter != null) {
                                mAdapter.setQrCodeImageView("");
                            }
                            if (mFullQrCodeImageView != null) {
                                mFullQrCodeImageView.setBackgroundResource(R.drawable.default_member_center_full_screen_qr_code_v2);
                            }
                        }

                        @Override
                        public void onComplete() {
                            unQrCodeSubscribe();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adapter刷新头部数据
     *
     * @param
     */
    private void updateHeadInfo(int position) {
        Log.d(TAG, "wqs:updateHeadInfo");
        try {
            if (mAdapter != null) {
                //传递用户会员信息数据
                mAdapter.setMemberStatus(mobileString, mMemberInfoBean);
//                mAdapter.notifyItemChanged(position);
//                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "wqs:bindData:mAdapter == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:bindData:Exception:" + e.toString());
        }
    }

    /**
     * 解除获取会员信息绑定
     */
    private void unMemberInfoSubscribe() {
        if (mMemberInfoDisposable != null && !mMemberInfoDisposable.isDisposed()) {
            mMemberInfoDisposable.dispose();
            mMemberInfoDisposable = null;
        }
    }

    /**
     * 解除获取用户信息绑定
     */
    private void unUserInfoSubscribe() {
        if (mUserInfoDisposable != null && !mUserInfoDisposable.isDisposed()) {
            mUserInfoDisposable.dispose();
            mUserInfoDisposable = null;
        }
    }

    /**
     * 解除获取推荐位数据绑定
     */
    private void unRecommendSubscribe() {
        if (mRecommendDisposable != null && !mRecommendDisposable.isDisposed()) {
            mRecommendDisposable.dispose();
            mRecommendDisposable = null;
        }
    }

    /**
     * 解除获取二维码图片数据绑定
     */
    private void unQrCodeSubscribe() {
        if (mQrCodeDisposable != null && !mQrCodeDisposable.isDisposed()) {
            mQrCodeDisposable.dispose();
            mQrCodeDisposable = null;
        }
    }

    /**
     * adapter填充数据
     *
     * @param
     */
    private void inflateRecommendData(List<Page> pageList) {
        Log.d(TAG, "wqs:inflateRecommendData");
        UserCenterPageBean UserCenterPageBean = null;
        UserCenterPageBean.Bean mProgramInfo = null;
        String title = "";
        List<UserCenterPageBean.Bean> mPromotionRecommendBean = new ArrayList<>();//会员促销推荐数据
        List<UserCenterPageBean.Bean> mInterestsRecommendBean = new ArrayList<>();//会员权益介绍推荐数据
        List<UserCenterPageBean.Bean> mDramaRecommendBean = new ArrayList<>();//会员片库推荐数据
        try {
            if (pageList == null && pageList.size() <= 0) {
                return;
            }
            for (int i = 0; i < pageList.size(); i++) {
                List<Program> programInfoList = null;
                if (i == 0) {
                    if (pageList.get(i) != null) {
                        if (pageList.get(i).getPrograms() != null && pageList.get(i).getPrograms().size() > 0) {
                            programInfoList = pageList.get(i).getPrograms();
                            if (programInfoList != null && programInfoList.size() > 0) {
                                mProgramInfo = new UserCenterPageBean.Bean();
                                mProgramInfo.set_title_name(programInfoList.get(0).getTitle());
                                mProgramInfo.setContentId(programInfoList.get(0).getL_id());
                                mProgramInfo.set_contentuuid(programInfoList.get(0).getL_uuid());
                                mProgramInfo.set_contenttype(programInfoList.get(0).getL_contentType());
                                mProgramInfo.set_imageurl(programInfoList.get(0).getImg());
                                mProgramInfo.set_actiontype(programInfoList.get(0).getL_actionType());
                                mProgramInfo.setGrade(programInfoList.get(0).getGrade());
//                                mProgramInfo.setSuperscript(programInfoList.get(0).getRSuperScript());
                                mPromotionRecommendBean.add(mProgramInfo);
                            }
                        } else {
                            Log.d(TAG, "wqs:inflateRecommendData：i == 0:page.getPrograms().get(0).getDatas()== null");
                        }
                    } else {
                        Log.d(TAG, "wqs:inflateRecommendData：i == 0:page.getPrograms().get(0) == null");
                    }
                } else if (i == 1) {
                    if (pageList.get(i) != null) {
                        if (pageList.get(i).getPrograms() != null && pageList.get(i).getPrograms().size() > 0) {
                            programInfoList = pageList.get(i).getPrograms();
                            mProgramInfo = new UserCenterPageBean.Bean();
                            mProgramInfo.set_title_name(programInfoList.get(0).getTitle());
                            mProgramInfo.setContentId(programInfoList.get(0).getL_id());
                            mProgramInfo.set_contentuuid(programInfoList.get(0).getL_uuid());
                            mProgramInfo.set_contenttype(programInfoList.get(0).getL_contentType());
                            mProgramInfo.set_imageurl(programInfoList.get(0).getImg());
                            mProgramInfo.set_actiontype(programInfoList.get(0).getL_actionType());
                            mProgramInfo.setGrade(programInfoList.get(0).getGrade());
//                            mProgramInfo.setSuperscript(programInfoList.get(0).getRSuperScript());
                            mInterestsRecommendBean.add(mProgramInfo);
                        } else {
                            Log.d(TAG, "wqs:inflateRecommendData：i == 1:page.getPrograms().get(0).getDatas()== null");
                        }
                    } else {
                        Log.d(TAG, "wqs:inflateRecommendData：i == 1:page.getPrograms().get(0) == null");
                    }
                } else if (i == 2) {
                    if (pageList.get(i) != null) {
                        if (pageList.get(i).getPrograms() != null && pageList.get(i).getPrograms().size() > 0) {
                            programInfoList = pageList.get(i).getPrograms();
                            title = pageList.get(i).getBlockTitle();
                            for (int j = 0; j < programInfoList.size(); j++) {
                                mProgramInfo = new UserCenterPageBean.Bean();
                                mProgramInfo.set_title_name(programInfoList.get(j).getTitle());
                                mProgramInfo.setContentId(programInfoList.get(j).getL_id());
                                mProgramInfo.set_contentuuid(programInfoList.get(j).getL_uuid());
                                mProgramInfo.set_contenttype(programInfoList.get(j).getL_contentType());
                                mProgramInfo.set_imageurl(programInfoList.get(j).getImg());
                                mProgramInfo.set_actiontype(programInfoList.get(j).getL_actionType());
                                mProgramInfo.setGrade(programInfoList.get(j).getGrade());
//                                mProgramInfo.setSuperscript(programInfoList.get(j).getRSuperScript());
                                mDramaRecommendBean.add(mProgramInfo);
                            }
                        } else {
                            Log.d(TAG, "wqs:inflateRecommendData：i == 2:page.getPrograms().get(0).getDatas()== null");
                        }
                    } else {
                        Log.d(TAG, "wqs:inflateRecommendData：i == 2:page.getPrograms().get(0) == null");
                    }
                } else {
                    Log.d(TAG, "wqs:只取三组数据，多余数据不取");
                    break;
                }
            }

            if (mAdapter != null) {
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_PROMOTION - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mPromotionRecommendBean;
                } else {
                    Log.d(TAG, "wqs:inflateRecommendData：PromotionRecommend==null");
                }
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_INTERESTS - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mInterestsRecommendBean;
                } else {
                    Log.d(TAG, "wqs:inflateRecommendData：mInterestsRecommendBean==null");
                }
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_DRAMA - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mDramaRecommendBean;
                    UserCenterPageBean.title = title;
                } else {
                    Log.d(TAG, "wqs:inflateRecommendData：mDramaRecommendBean==null");
                }
            } else {
                Log.d(TAG, "wqs:inflateRecommendData：mAdapter == null");
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessage(RECOMMEND);
            } else {
                Log.d(TAG, "wqs:inflateRecommendData：mHandler == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:inflateRecommendData:Exception:" + e.toString());
        }
    }

    /**
     * adapter刷新数据
     *
     * @param
     */

    private void bindData() {
        Log.d(TAG, "wqs:bindData");
        try {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "wqs:bindData:mAdapter == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:bindData:Exception:" + e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "wqs:onResume");
            //会员中心首页上报日志
            LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "5,");
            requestUserInfo();
            //获取M站二维码信息
            if (TextUtils.isEmpty(Authorization)) {
                Authorization = Utils.getAuthorization(MemberCenterActivity.this);
                if (!TextUtils.isEmpty(Authorization)) {
                    requestQrCodeInfo(Authorization, Constant.RESPONSE_TYPE, Constant.CLIENT_ID);
                }
            } else {
                requestQrCodeInfo(Authorization, Constant.RESPONSE_TYPE, Constant.CLIENT_ID);
            }
            Log.d(TAG, "wqs:Authorization?null:" + TextUtils.isEmpty(Authorization));
        } catch (Exception e) {
            Log.e(TAG, "wqs:onResume:Exception:" + e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "wqs:onDestroy");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mAdapter != null) {
            mAdapter.release();
            mAdapter = null;
        }
        unMemberInfoSubscribe();
        unRecommendSubscribe();
        unQrCodeSubscribe();
        unUserInfoSubscribe();
    }


    @Override
    public void onItemClick(View view, int Position, UserCenterPageBean.Bean entity) {
        try {
            Intent intent = new Intent();
            Class mPageClass = null;
            switch (view.getId()) {
                case R.id.id_member_center_btn_login:
                    mPageClass = LoginActivity.class;
                    break;
                case R.id.id_member_center_btn_open:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = PayChannelActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    break;
                case R.id.id_member_center_btn_exchange:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = CodeExChangeActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    //mPageClass = CodeExChangeActivity.class;
                    //Toast.makeText(this, "此页面正在开发中", Toast.LENGTH_LONG).show();
                    break;
                case R.id.id_member_center_btn_order:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = MyOrderActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    break;
                case R.id.id_member_center_btn_drama_library:
                    String jumpParam = BootGuide.getBaseUrl(BootGuide.MEMBER_CENTER_PARAMS);
                    if (!TextUtils.isEmpty(jumpParam)) {
                        intent.putExtra("action", "panel");
                        intent.putExtra("params", jumpParam);
                        Log.d(TAG, "wqs:MEMBER_CENTER_PARAMS:action:panelwqs:-params:" + jumpParam);
                        mPageClass = MainActivity.class;
                    } else {
                        Toast.makeText(this, "请配置跳转参数", Toast.LENGTH_LONG).show();
                    }


                    break;
                case R.id.id_member_center_qr_root:
                    mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                    break;
                case R.id.id_member_center_promotion_recommend:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = PayChannelActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    break;
                case R.id.id_member_center_interests_introduce:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = PayChannelActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    break;
                case R.id.id_module_8_view1:
                case R.id.id_module_8_view2:
                case R.id.id_module_8_view3:
                case R.id.id_module_8_view4:
                case R.id.id_module_8_view5:
                case R.id.id_module_8_view6:
                    if (entity != null) {
                        JumpUtil.activityJump(this, entity.get_actiontype(), entity.get_contenttype(),
                                entity.getContentId(), "");
                    }
                    break;
                default:
                    break;
            }
            if (mPageClass == null) {
                return;
            }
            intent.setClass(this, mPageClass);
            startActivity(intent);
            if (mPageClass == MainActivity.class) {
                if (!isBackground) {
                    ActivityStacks.get().finishAllActivity();
                }
                this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemClick:Exception:" + e.toString());
        }
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, UserCenterPageBean.Bean object) {
        try {
            if (view != null && view.getId() == R.id.id_member_center_promotion_recommend && hasFocus) {
                if (mRecyclerView.canScrollVertically(-1)) {
                    Log.d(TAG, "wqs:onItemFocusChange:promotion_recommend:top");
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemFocusChange:Exception:" + e.toString());

        }
    }

    @Override
    public void onPageResult(@Nullable List<Page> page) {
        inflateRecommendData(page);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }


    public static class MemberHandler extends Handler {
        private WeakReference<MemberCenterActivity> mReference;

        public MemberHandler(MemberCenterActivity memberCenterActivity) {
            mReference = new WeakReference<MemberCenterActivity>(memberCenterActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MemberCenterActivity activity = mReference.get();
            switch (msg.what) {
                case HEAD:
                    if (activity != null) {
                        activity.updateHeadInfo(msg.what);
                    }
                    break;
                case RECOMMEND_PROMOTION:
                    break;
                case RECOMMEND_INTERESTS:
                    break;
                case RECOMMEND_DRAMA:
                    break;
                case RECOMMEND:
                    if (activity != null) {
                        activity.bindData();
                    }
                    break;
                case QR_CODE_INVALID:
                    activity.requestQrCodeInfo(activity.Authorization, Constant.RESPONSE_TYPE, Constant.CLIENT_ID);
                    break;
                default:
                    break;
            }

        }
    }
}
