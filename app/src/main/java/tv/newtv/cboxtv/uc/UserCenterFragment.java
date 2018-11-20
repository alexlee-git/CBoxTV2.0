package tv.newtv.cboxtv.uc;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.bean.AdBean;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
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
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.net.AppHeadersInterceptor;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.MemberInfoBean;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.AttentionDetailActivity;
import tv.newtv.cboxtv.uc.v2.CollectionDetailActivity;
import tv.newtv.cboxtv.uc.v2.LoginActivity;
import tv.newtv.cboxtv.uc.v2.MyOrderActivity;
import tv.newtv.cboxtv.uc.v2.SettingActivity;
import tv.newtv.cboxtv.uc.v2.SubscribeDetailActivity;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.UserInfoActivity;
import tv.newtv.cboxtv.uc.v2.VersionUpdateOneActivity;
import tv.newtv.cboxtv.uc.v2.aboutmine.AboutMineV2Activity;
import tv.newtv.cboxtv.uc.v2.member.MemberCenterActivity;
import tv.newtv.cboxtv.views.widget.ScrollSpeedLinearLayoutManger;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2
 * 类描述：我的页面二期
 * 创建人：
 * 创建时间：
 * 创建日期：
 * 修改人：wqs
 * 修改时间：10:07
 * 修改备注：2018/9/4
 * 修改备注：收藏节目集和节目、关注人物、订阅CCTV、订阅CCTV
 */
public class UserCenterFragment extends BaseFragment implements
        OnRecycleItemClickListener<UserCenterPageBean.Bean>, PageContract.View {

    public static final int SUBSCRIBE = 3;
    public static final int COLLECT = 4;
    public static final int HISTORY = 1;
    public static final int ATTENTION = 5;
    public static final int RECOMMEND = 6;
    public static final int BANNER_AD = 2;
    public static final int HEAD = 0;
    public static final int REQUEST_DATA_BASE = 1001;//获取数据库数据
    private static final String TAG = "UserCenterFragment";
    private String param;
    private String contentId;
    private String actionType;
    private AiyaRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private Disposable mMemberInfoDisposable;
    private Disposable mRecommendDisposable;//猜你喜欢
    private List<UserCenterPageBean> pageData;
    private Observable<Boolean> mUpdateDataObservable;
    private UserCenterAdapter mAdapter;
    private MyHandler mHandler;
    private String mLoginTokenString;//登录token,用于判断登录状态
    private AdBean.Material mBannerAdInfo;
    private AdBean.AdspacesItem mAdSpacesItem;
    private MemberInfoBean mMemberInfoBean;
    private String memberStatusString = "member_open_not";
    private String sign_member_open_not = "member_open_not";//未开通会员
    private String sign_member_open_lose = "member_open_lose";//已开通，但失效
    private String sign_member_open_good = "member_open_good";//已开通，有效
    private String userId;//用户ID
    private DataBaseCompleteReceiver mDataBaseCompleteReceiver;//接收登陆成功数据同步结束广播，用于数据更新
    private PageContract.ContentPresenter mContentPresenter;

    public static BaseFragment newInstance(Bundle paramBundle) {
        BaseFragment fragment = new UserCenterFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public boolean isNoTopView() {
        try {
            if (mRecyclerView != null && ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition
                            () == 0) {
                View view = FocusFinder.getInstance().findNextFocus(mRecyclerView,
                        mRecyclerView.findFocus(), View.FOCUS_UP);
                return view == null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:isNoTopView:Exception:" + e.toString());
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        try {
            ScrollSpeedLinearLayoutManger linearLayoutManager = (ScrollSpeedLinearLayoutManger)
                    mRecyclerView.getLayoutManager();
            if (mRecyclerView.computeVerticalScrollOffset() != 0) {
                linearLayoutManager.smoothScrollToPosition(mRecyclerView, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onBackPressed:Exception:" + e.toString());
        }


        return super.onBackPressed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "wqs:onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            param = bundle.getString("nav_text");
            contentId = bundle.getString("content_id");
            actionType = bundle.getString("actionType");
        }
        mHandler = new MyHandler(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "wqs:onCreateView");
        View view = inflater.inflate(R.layout.layout_uc_fragment, container, false);
        init();
        mRecyclerView = (AiyaRecyclerView) view.findViewById(R.id.id_usercenter_fragment_root);
        mEmptyView = (TextView) view.findViewById(R.id.id_empty_view);
        mRecyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(LauncherApplication
                .AppContext));
//        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);

        setAnimRecyclerView(mRecyclerView);
        //获取通栏广告的数据
        getBannerAD();
        Constant.ID_PAGE_USERCENTER = Constant.getBaseUrl(AppHeadersInterceptor.PAGE_USERCENTER);
        if (!TextUtils.isEmpty(Constant.ID_PAGE_USERCENTER)) {
            //获取猜你喜欢推荐位的数据
            mContentPresenter = new PageContract.ContentPresenter(getContext(), this);
            mContentPresenter.getPageContent(Constant.ID_PAGE_USERCENTER);
        } else {
            Log.e(TAG, "wqs:ID_PAGE_USERCENTER==null");
        }
        if (mDataBaseCompleteReceiver == null) {
            mDataBaseCompleteReceiver = new DataBaseCompleteReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDataBaseCompleteReceiver, new IntentFilter("action.uc.data.sync.complete"));
        }
        uploadUserOnline();
        return view;
    }

    private void init() {
        try {
            pageData = new ArrayList<>();
            pageData.add(new UserCenterPageBean("观看记录"));
            pageData.add(new UserCenterPageBean("我的订阅"));
            pageData.add(new UserCenterPageBean("我的收藏"));
            pageData.add(new UserCenterPageBean("我的关注"));
            pageData.add(new UserCenterPageBean("猜你喜欢"));
            mAdapter = new UserCenterAdapter(getActivity(), this);
            mAdapter.appendToList(pageData);

            mAdapter.setHasStableIds(true);
            mUpdateDataObservable = RxBus.get().register(Constant.UPDATE_UC_DATA);
            mUpdateDataObservable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isUpdate) throws Exception {
                            if (isUpdate) {
                                requestDataBase();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:init:Exception:" + e.toString());
        }

    }

    //获取用户登录状态
    private void requestUserInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(getActivity());
                Log.d(TAG, "wqs:isTokenRefresh:status:" + status);
                //获取登录状态
                mLoginTokenString = SharePreferenceUtils.getToken(getActivity().getApplicationContext());
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    userId = SharePreferenceUtils.getUserId(getActivity().getApplicationContext());
                    e.onNext(mLoginTokenString);
                } else {
                    userId = SystemUtils.getDeviceMac(getActivity().getApplicationContext());
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
                            Log.d(TAG, "wqs:requestUserInfo:loginStatus:false:not requestMemberInfo");
                            //用户未登录
                            memberStatusString = sign_member_open_not;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(HEAD);
                            } else {
                                Log.e(TAG, "wqs:requestUserInfo:mHandler == null");
                            }
                        }
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(REQUEST_DATA_BASE);
                        } else {
                            Log.e(TAG, "wqs:requestUserInfo:mHandler == null");
                        }

                    }
                });
    }

    //读取用户会员信息
    private void requestMemberInfo() {
        try {
            NetClient.INSTANCE.getUserCenterMemberInfoApi().getMemberInfo("Bearer " + mLoginTokenString, "", Libs.get().getAppKey()).subscribeOn(Schedulers.io())
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
                            String expireTimeDate = jsonObject.optString("expireTime");
                            if (!TextUtils.isEmpty(expireTimeDate)) {
                                //有效期截止时间毫秒数
                                long expireTimeInMillis = TimeUtil.getInstance().getSecondsFromDate(expireTimeDate);
                                //与当前时间进行对比，判断会员是否到期
                                long currentTimeInMillis = TimeUtil.getInstance().getCurrentTimeInMillis();
                                Log.d(TAG, "wqs:expireTimeInMillis:" + expireTimeInMillis);
                                Log.d(TAG, "wqs:currentTimeInMillis:" + currentTimeInMillis);
                                if (expireTimeInMillis >= currentTimeInMillis) {
                                    //用户会员有效
                                    memberStatusString = sign_member_open_good;
                                } else {
                                    //用户会员无效
                                    memberStatusString = sign_member_open_lose;
                                }
                            } else {
                                memberStatusString = sign_member_open_not;
                                Log.d(TAG, "wqs:requestMemberInfo:next:expireTime==null");
                            }
                        } else {
                            memberStatusString = sign_member_open_not;
                            Log.d(TAG, "wqs:requestMemberInfo:next:memberInfo==null");
                        }
                        unMemberInfoSubscribe();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(HEAD);
                    } else {
                        Log.d(TAG, "wqs:requestUserInfo:mHandler == null");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "wqs:requestMemberInfo:onError");
                    unMemberInfoSubscribe();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(HEAD);
                    } else {
                        Log.d(TAG, "wqs:requestUserInfo:mHandler == null");
                    }
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

    //读取数据库中数据
    private void requestDataBase() {
        //订阅数据表表名
        String tableNameSubscribe = DBConfig.SUBSCRIBE_TABLE_NAME;
        //收藏数据表表名
        String tableNameCollect = DBConfig.COLLECT_TABLE_NAME;
        //历史记录数据表表名
        String tableNameHistory = DBConfig.HISTORY_TABLE_NAME;
        //关注数据表表名
        String TableNameAttention = DBConfig.ATTENTION_TABLE_NAME;
        if (!TextUtils.isEmpty(mLoginTokenString)) {
            tableNameSubscribe = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
            tableNameCollect = DBConfig.REMOTE_COLLECT_TABLE_NAME;
            tableNameHistory = DBConfig.REMOTE_HISTORY_TABLE_NAME;
            TableNameAttention = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
        }
        try {
            DataSupport.search(tableNameSubscribe)
                    .condition()
                    .eq(DBConfig.USERID, userId)
                    .OrderBy(DBConfig.ORDER_BY_TIME)
                    .build().withCallback(new DBCallback<String>() {
                @Override
                public void onResult(int code, String result) {
                    if (code == 0) {
                        UserCenterPageBean UserCenterPageBean = mAdapter.getItem(1);
                        Gson mGson = new Gson();
                        Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                        }.getType();
                        List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                        Log.d(TAG, "wqs:user_subscribe_info:" + mGson.toJson(mCollectBean));
                        UserCenterPageBean.data = mCollectBean;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(SUBSCRIBE);
                        } else {
                            Log.d(TAG, "wqs:user_subscribe_info:mHandler==null");
                        }

                    }
                }
            }).excute();
            DataSupport.search(tableNameCollect).condition()
                    .eq(DBConfig.USERID, userId)
                    .OrderBy(DBConfig.ORDER_BY_TIME)
                    .build().withCallback(new DBCallback<String>() {
                @Override
                public void onResult(int code, String result) {
                    if (code == 0) {
                        UserCenterPageBean UserCenterPageBean = mAdapter.getItem(2);
                        Gson mGson = new Gson();
                        Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                        }.getType();
                        List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                        Log.d(TAG, "wqs:user_collect_info:" + mGson.toJson(mCollectBean));
                        UserCenterPageBean.data = mCollectBean;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(COLLECT);
                        } else {
                            Log.d(TAG, "wqs:user_collect_info：mHandler==null");
                        }

                    }
                }
            }).excute();
            DataSupport.search(tableNameHistory).condition()
                    .eq(DBConfig.USERID, userId)
                    .OrderBy(DBConfig.ORDER_BY_TIME)
                    .build().withCallback(new DBCallback<String>() {
                @Override
                public void onResult(int code, String result) {
                    if (code == 0) {
                        UserCenterPageBean UserCenterPageBean = mAdapter.getItem(0);
                        Gson mGson = new Gson();
                        Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                        }.getType();
                        List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                        Log.d(TAG, "wqs:user_history_info:" + mGson.toJson(mCollectBean));
                        UserCenterPageBean.data = mCollectBean;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(HISTORY);
                        } else {
                            Log.d(TAG, "wqs:user_history_info:mHandler==null");
                        }
                    }
                }
            }).excute();
            DataSupport.search(TableNameAttention).condition()
                    .eq(DBConfig.USERID, userId)
                    .OrderBy(DBConfig.ORDER_BY_TIME)
                    .build().withCallback(new DBCallback<String>() {
                @Override
                public void onResult(int code, String result) {
                    if (code == 0) {
                        UserCenterPageBean UserCenterPageBean = mAdapter.getItem(3);
                        Gson mGson = new Gson();
                        Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                        }.getType();
                        List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                        Log.d(TAG, "wqs:user_attention_info:" + mGson.toJson(mCollectBean));
                        UserCenterPageBean.data = mCollectBean;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(ATTENTION);
                        } else {
                            Log.d(TAG, "wqs:user_attention_info:mHandler==null");
                        }
                    }
                }
            }).excute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:requestData:Exception:" + e.toString());
        }

    }

    /**
     * 推荐位填充数据
     *
     * @param
     */
    private void inflateRecommendData(List<Page> pageList) {
        try {
            UserCenterPageBean.Bean mProgramInfo = null;
            UserCenterPageBean UserCenterPageBean = null;
            UserCenterPageBean = mAdapter.getItem(4);
            List<UserCenterPageBean.Bean> mRecommendBean = new ArrayList<>();
            List<Program> programInfoList = null;
            if (pageList == null && pageList.size() <= 0) {
                return;
            }
            if (pageList.get(0) != null) {
                if (pageList.get(0).getPrograms() != null && pageList.get(0).getPrograms().size() > 0) {
                    programInfoList = pageList.get(0).getPrograms();
                    UserCenterPageBean.title = pageList.get(0).getBlockTitle();
                    for (int i = 0; i < programInfoList.size(); i++) {
                        mProgramInfo = new UserCenterPageBean.Bean();
                        mProgramInfo.set_title_name(programInfoList.get(i).getTitle());
                        mProgramInfo.set_contentuuid(programInfoList.get(i).getL_id());
                        mProgramInfo.set_contenttype(programInfoList.get(i).getL_contentType());
                        mProgramInfo.set_imageurl(programInfoList.get(i).getImg());
                        mProgramInfo.set_actiontype(programInfoList.get(i).getL_actionType());
                        mProgramInfo.setGrade(programInfoList.get(i).getGrade());
                        mRecommendBean.add(mProgramInfo);
                    }
                } else {
                    Log.d(TAG, "wqs:requestRecommendData：mResult.getDatas().get(0).getDatas()== null");
                }
            } else {
                Log.d(TAG, "wqs:requestRecommendData：mResult.getDatas().get(0) == null");
            }
            UserCenterPageBean.data = mRecommendBean;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(RECOMMEND);
            } else {
                Log.d(TAG, "wqs:requestRecommendData:mHandler==null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:inflateRecommendData:Exception:" + e.toString());
        }
    }

    /**
     * 获取通栏广告
     */
    public void getBannerAD() {
        try {
            RxBus.get().post(Constant.INIT_SDK, Constant.INIT_ADSDK);
            final StringBuffer mStringBuffer = new StringBuffer();
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    e.onNext(AdSDK.getInstance().getAD(Constant.AD_DESK, null, null, "cbox_usercenter_banner", null, null, mStringBuffer));
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer result) throws Exception {
                            try {
                                if (result == 0) {
                                    Log.d(TAG, "wqs:getBannerAD:" + mStringBuffer.toString());
                                    Gson mGson = new Gson();
                                    AdBean bean = mGson.fromJson(mStringBuffer.toString(), AdBean.class);

                                    if (bean == null || bean.adspaces == null || bean.adspaces.desk == null || bean.adspaces.desk.size() < 1) {
                                        return;
                                    }
                                    if (bean.adspaces.desk.get(0) == null || bean.adspaces.desk.get(0).materials == null) {
                                        return;
                                    }
                                    if (!TextUtils.equals(bean.adspaces.desk.get(0).materials.get(0).type, "image")) {
                                        return;
                                    }
                                    mAdSpacesItem = bean.adspaces.desk.get(0);
                                    mBannerAdInfo = bean.adspaces.desk.get(0).materials.get(0);
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(BANNER_AD);
                                    }
                                } else {
                                    Log.d(TAG, "wqs:getBannerAD:getAD:result!=0");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "wqs:getBannerAD:accept:Exception:" + e.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:getBannerAD:Exception:" + e.toString());
        }
    }


    /**
     * adapter刷新数据
     *
     * @param
     */
    private void bindData(int position) {
        try {
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "wqs:bindData:mAdapter == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:bindData:Exception:" + e.toString());
        }
    }

    /**
     * adapter刷新头部数据,登录状态，会员状态
     *
     * @param
     */
    private void updateHeadInfo(int position) {
        Log.d(TAG, "wqs:bindData");
        try {
            if (mAdapter != null) {
                mAdapter.setLoginStatus(mLoginTokenString);
                mAdapter.setMemberStatus(memberStatusString);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "wqs:bindData:mAdapter == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:bindData:Exception:" + e.toString());
        }
    }

    /**
     * 刷新广告数据
     *
     * @param position
     */
    private void bindBannerAdData(int position) {
        Log.d(TAG, "wqs:bindData");
        try {
            if (mAdapter != null) {
                mAdapter.setAdData(mBannerAdInfo);
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "wqs:bindData:mAdapter == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:bindData:Exception:" + e.toString());
        }
    }

    /**
     * 上报广告日志
     *
     * @param adspacesItem
     */
    private void reportAdInfo(final AdBean.AdspacesItem adspacesItem) {
        try {
            Log.d(TAG, "wqs:reportAdInfo");
            if (adspacesItem != null) {
                //日志上传sdk初始化
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        e.onNext(AdSDK.getInstance().report(adspacesItem.mid + "",
                                adspacesItem.aid + "", adspacesItem.materials.get(0).id + "", null,
                                null,
                                null, null));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean value) throws Exception {
                                Log.d(TAG, "wqs:reportAdInfo:result=" + value);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:reportAdInfo:Exception:" + e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "wqs:onResume");
        requestUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "wqs:onDestroyView");
    }

    @Override
    protected String getContentUUID() {
        return contentId;
    }

    @Override
    public void onDestroy() {
        try {
            Log.d(TAG, "wqs:onDestroy");
            RxBus.get().unregister(Constant.UPDATE_UC_DATA, mUpdateDataObservable);
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if (mDataBaseCompleteReceiver == null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDataBaseCompleteReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onDestroy:Exception:" + e.toString());
        }

        super.onDestroy();
    }

    /**
     * 解除获取用户会员信息绑定
     */
    private void unMemberInfoSubscribe() {
        if (mMemberInfoDisposable != null && !mMemberInfoDisposable.isDisposed()) {
            mMemberInfoDisposable.dispose();
            mMemberInfoDisposable = null;
        }
    }

    /**
     * 通过获取广告参数跳转相应页面
     *
     * @param eventContentString
     */
    private void toSecondPageFromAd(String eventContentString) {
        Log.i(TAG, "wqs:toSecondPageFromAd");
        try {
            AdEventContent adEventContent = GsonUtil.fromjson(eventContentString, AdEventContent.class);
            JumpUtil.activityJump(getActivity(), adEventContent.actionType, adEventContent.contentType,
                    adEventContent.contentUUID, adEventContent.actionURI);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onItemClick(View view, int position, UserCenterPageBean.Bean entity) {
        try {
            Intent intent = new Intent();
            Class clazz = null;
            switch (view.getId()) {
                case R.id.id_user_btn_history:
                    intent.putExtra("action_type", HISTORY);
                    intent.putExtra("title", "观看记录");
                    clazz = HistoryActivity.class;
                    break;
                case R.id.id_user_btn_subscribe:
                    intent.putExtra("action_type", SUBSCRIBE);
                    intent.putExtra("title", "我的订阅");
                    clazz = SubscribeDetailActivity.class;
                    break;
                case R.id.id_user_btn_collect:
                    intent.putExtra("action_type", COLLECT);
                    intent.putExtra("title", "我的收藏");
                    clazz = CollectionDetailActivity.class;
                    break;
                case R.id.id_user_btn_attention:
                    intent.putExtra("action_type", ATTENTION);
                    intent.putExtra("title", "我的关注");
                    clazz = AttentionDetailActivity.class;
                    break;
                case R.id.id_user_btn_setting:
                    clazz = SettingActivity.class;
                    break;
                case R.id.id_user_btn_version:
                    clazz = VersionUpdateOneActivity.class;
                    break;
                case R.id.id_user_btn_about:
                    clazz = AboutMineV2Activity.class;
                    break;
                case R.id.id_module_8_view1:
                case R.id.id_module_8_view2:
                case R.id.id_module_8_view3:
                case R.id.id_module_8_view4:
                case R.id.id_module_8_view5:
                case R.id.id_module_8_view6:
                    if (entity != null) {
                        Log.e(TAG, "wqs:entity.get_contenttype():" + entity.get_contenttype());
                        JumpUtil.activityJump(getContext(), entity.get_actiontype(), entity.get_contenttype(),
                                entity.get_contentuuid(), "");
                    } else {
                        switch (position) {
                            case SUBSCRIBE:
                                intent.putExtra("action_type", SUBSCRIBE);
                                intent.putExtra("title", "我的订阅");
                                clazz = SubscribeDetailActivity.class;
                                break;
                            case COLLECT:
                                intent.putExtra("action_type", COLLECT);
                                intent.putExtra("title", "我的收藏");
                                clazz = CollectionDetailActivity.class;
                                break;
                            case HISTORY:
                                intent.putExtra("action_type", HISTORY);
                                intent.putExtra("title", "观看记录");
                                clazz = HistoryActivity.class;
                                break;
                            case ATTENTION:
                                intent.putExtra("action_type", ATTENTION);
                                intent.putExtra("title", "我的关注");
                                clazz = AttentionDetailActivity.class;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case R.id.id_user_btn_login:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        intent.putExtra("member_status", memberStatusString);
                        clazz = UserInfoActivity.class;
                    } else {
                        clazz = LoginActivity.class;
                    }
                    break;
                case R.id.id_user_btn_member:
                    clazz = MemberCenterActivity.class;
                    break;
                case R.id.id_user_btn_order:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        clazz = MyOrderActivity.class;
                    } else {
                        clazz = LoginActivity.class;
                    }
                    break;
                case R.id.id_user_ad_banner:
                    if (mAdSpacesItem != null) {
                        if (mBannerAdInfo != null) {
                            reportAdInfo(mAdSpacesItem);
                            toSecondPageFromAd(mBannerAdInfo.eventContent);
                        } else {
                            Log.d(TAG, "wqs:onItemClick:mBannerAdInfo == null");
                        }
                    } else {
                        Log.d(TAG, "wqs:onItemClick:mAdspacesItem == null");
                    }

                    break;
                default:
                    break;
            }
            if (clazz == null) {
                return;
            }
            intent.setClass(getActivity(), clazz);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemClick:Exception:" + e.toString());
        }


    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, UserCenterPageBean
            .Bean object) {
        Log.d(TAG, "wqs:onItemFocusChange:Position:" + Position);


    }

    @Override
    public View getFirstFocusView() {
        if (mAdapter == null)
            return null;
        return mAdapter.getFirstView();
    }

    @Override
    public void onPageResult(@org.jetbrains.annotations.Nullable List<Page> page) {
        //推荐位填充数据
        inflateRecommendData(page);
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


    public static class MyHandler extends Handler {
        private WeakReference<UserCenterFragment> mReference;

        public MyHandler(UserCenterFragment userCenterFragment) {
            mReference = new WeakReference<UserCenterFragment>(userCenterFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserCenterFragment fragment = mReference.get();
            switch (msg.what) {
                case SUBSCRIBE:
                case COLLECT:
                case HISTORY:
                case ATTENTION:
                case RECOMMEND:
                    if (fragment != null) {
                        fragment.bindData(msg.what);
                    }
                    break;
                case BANNER_AD:
                    if (fragment != null) {
                        fragment.bindBannerAdData(msg.what);
                    }
                    break;
                case HEAD:
                    if (fragment != null) {
                        fragment.updateHeadInfo(msg.what);
                    }
                    break;
                case REQUEST_DATA_BASE:
                    if (fragment != null) {
                        fragment.requestDataBase();
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private class DataBaseCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "wqs:receive database request complete broadcast, action : " + action);
            if (TextUtils.equals(action, "action.uc.data.sync.complete")) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(REQUEST_DATA_BASE);
                } else {
                    Log.d(TAG, "wqs:DataBaseCompleteReceiver:mHandler == null");
                }
            }
        }
    }

    private void uploadUserOnline() {

        String userid = SharePreferenceUtils.getUserId(getContext());
        if (!TextUtils.isEmpty(userid)) {
            LogUploadUtils.setLogFileds(Constant.USER_ID, userid);
            StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            dataBuff.append(0 + ",")
                    .append(5)
                    .trimToSize();
            LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, dataBuff.toString());
        }
    }
}
