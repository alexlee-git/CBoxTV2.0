package tv.newtv.cboxtv.uc.v2.member;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.cms.bean.Program;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.SplashActivity;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.net.AppHeadersInterceptor;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.MemberInfoBean;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.LoginActivity;
import tv.newtv.cboxtv.uc.v2.MyOrderActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
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
public class MemberCenterActivity extends Activity implements OnRecycleItemClickListener<UserCenterPageBean.Bean> {
    private final String TAG = "MemberCenterActivity";
    public static final int HEAD = 0;
    public static final int RECOMMEND_PROMOTION = 1;//会员促销推荐位
    public static final int RECOMMEND_INTERESTS = 2;//会员权益介绍推荐位
    public static final int RECOMMEND_DRAMA = 3;//会员片库推荐
    public static final int RECOMMEND = 4;//推荐位
    private MemberCenterRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<UserCenterPageBean> pageData;
    private MemberCenterAdapter mAdapter;
    private MemberHandler mHandler;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private Disposable mMemberInfoDisposable;
    private Disposable mRecommendDisposable;//推荐位
    private MemberInfoBean mMemberInfoBean;
    private String mLoginTokenString;//登录token,用于判断登录状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter_member_center_v2);
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
        pageData.add(new UserCenterPageBean("会员片库"));
        mAdapter = new MemberCenterAdapter(this, this);
        mAdapter.setHasStableIds(true);
        mAdapter.appendToList(pageData);
        mRecyclerView.setAdapter(mAdapter);
        mPopupView = LayoutInflater.from(this).inflate(R.layout.activity_usercenter_member_center_qr_code_full_screen_v2, null);
        mPopupWindow = new PopupWindow(mPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
        Constant.ID_PAGE_MEMBER = Constant.getBaseUrl(AppHeadersInterceptor.PAGE_MEMBER);
        if (!TextUtils.isEmpty(Constant.ID_PAGE_MEMBER)) {
            //获取推荐位数据
            requestRecommendData();
        } else {
            Log.d(TAG, "wqs:ID_PAGE_MEMBER==null");
        }
    }

    //获取推荐位数据
    private void requestRecommendData() {
        try {
            NetClient.INSTANCE.getPageDataApi().getPageData(Libs.get().getAppKey(), Libs.get().getChannelId(), Constant.ID_PAGE_MEMBER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {

                @Override
                public void onSubscribe(Disposable d) {
                    unRecommendSubscribe();
                    mRecommendDisposable = d;
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    ModuleInfoResult moduleInfoResult = null;
                    String value = null;
                    Gson mGSon = new Gson();
                    try {
                        value = responseBody.string();
                        Log.d(TAG, "wqs:requestRecommendData:value:" + value);
                        moduleInfoResult = mGSon.fromJson(value, ModuleInfoResult.class);
                        inflateData(moduleInfoResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    unRecommendSubscribe();
                }

                @Override
                public void onError(Throwable e) {
                    unRecommendSubscribe();
                }

                @Override
                public void onComplete() {
                    unRecommendSubscribe();
                }
            });
        } catch (Exception e) {
            unRecommendSubscribe();
            e.printStackTrace();
            Log.e(TAG, "wqs:requestGuessYouLikeData:Exception:" + e.toString());
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
                    e.onNext(mLoginTokenString);
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
                            long seconds = TimeUtil.getInstance().getSecondsFromDate(jsonObject.optString("expireTime"));
                            String expireTime = TimeUtil.getInstance().getDateFromSeconds(seconds + "");
                            mMemberInfoBean.setExpireTime(expireTime);
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
     * adapter刷新头部数据
     *
     * @param
     */
    private void updateHeadInfo(int position) {
        Log.d(TAG, "wqs:updateHeadInfo");
        try {
            if (mAdapter != null) {
                //传递用户会员信息数据
                mAdapter.setMemberStatus(mMemberInfoBean);
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
     * 解除获取会员信息绑定
     */
    private void unMemberInfoSubscribe() {
        if (mMemberInfoDisposable != null && !mMemberInfoDisposable.isDisposed()) {
            mMemberInfoDisposable.dispose();
            mMemberInfoDisposable = null;
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
     * adapter填充数据
     *
     * @param
     */
    private void inflateData(ModuleInfoResult moduleInfoResult) {
        Log.d(TAG, "wqs:inflateData");
        UserCenterPageBean UserCenterPageBean = null;
        UserCenterPageBean.Bean mProgramInfo = null;
        String title = "";
        List<UserCenterPageBean.Bean> mPromotionRecommendBean = new ArrayList<>();//会员促销推荐数据
        List<UserCenterPageBean.Bean> mInterestsRecommendBean = new ArrayList<>();//会员权益介绍推荐数据
        List<UserCenterPageBean.Bean> mDramaRecommendBean = new ArrayList<>();//会员片库推荐数据
        try {
            if (moduleInfoResult != null) {
                if (moduleInfoResult.getDatas() != null && moduleInfoResult.getDatas().size() > 0) {
                    Log.d(TAG, "wqs:inflateData:moduleInfoResult.getDatas().size():" + moduleInfoResult.getDatas().size());
                    for (int i = 0; i < moduleInfoResult.getDatas().size(); i++) {
                        List<Program> programInfoList = null;
                        if (i == 0) {
                            if (moduleInfoResult.getDatas().get(i) != null) {
                                if (moduleInfoResult.getDatas().get(i).getDatas() != null && moduleInfoResult.getDatas().get(i).getDatas().size() > 0) {
                                    programInfoList = moduleInfoResult.getDatas().get(i).getDatas();
                                    if (programInfoList != null && programInfoList.size() > 0) {
                                        mProgramInfo = new UserCenterPageBean.Bean();
                                        mProgramInfo.set_title_name(programInfoList.get(0).getTitle());
                                        mProgramInfo.set_contentuuid(programInfoList.get(0).getContentId());
                                        mProgramInfo.set_contenttype(programInfoList.get(0).getContentType());
                                        mProgramInfo.set_imageurl(programInfoList.get(0).getImg());
                                        mProgramInfo.set_actiontype(programInfoList.get(0).getL_actionType());
                                        mProgramInfo.setGrade(programInfoList.get(0).getGrade());
                                        mProgramInfo.setSuperscript(programInfoList.get(0).getRSuperScript());
                                        mPromotionRecommendBean.add(mProgramInfo);
                                    }
                                } else {
                                    Log.d(TAG, "wqs:inflateData：i == 0:moduleInfoResult.getDatas().get(0).getDatas()== null");
                                }
                            } else {
                                Log.d(TAG, "wqs:inflateData：i == 0:moduleInfoResult.getDatas().get(0) == null");
                            }
                        } else if (i == 1) {
                            if (moduleInfoResult.getDatas().get(i) != null) {
                                if (moduleInfoResult.getDatas().get(i).getDatas() != null && moduleInfoResult.getDatas().get(i).getDatas().size() > 0) {
                                    programInfoList = moduleInfoResult.getDatas().get(i).getDatas();
                                    mProgramInfo = new UserCenterPageBean.Bean();
                                    mProgramInfo.set_title_name(programInfoList.get(0).getTitle());
                                    mProgramInfo.set_contentuuid(programInfoList.get(0).getContentId());
                                    mProgramInfo.set_contenttype(programInfoList.get(0).getContentType());
                                    mProgramInfo.set_imageurl(programInfoList.get(0).getImg());
                                    mProgramInfo.set_actiontype(programInfoList.get(0).getL_actionType());
                                    mProgramInfo.setGrade(programInfoList.get(0).getGrade());
                                    mProgramInfo.setSuperscript(programInfoList.get(0).getRSuperScript());
                                    mInterestsRecommendBean.add(mProgramInfo);
                                } else {
                                    Log.d(TAG, "wqs:inflateData：i == 1:moduleInfoResult.getDatas().get(0).getDatas()== null");
                                }
                            } else {
                                Log.d(TAG, "wqs:inflateData：i == 1:moduleInfoResult.getDatas().get(0) == null");
                            }
                        } else if (i == 2) {
                            if (moduleInfoResult.getDatas().get(i) != null) {
                                if (moduleInfoResult.getDatas().get(i).getDatas() != null && moduleInfoResult.getDatas().get(0).getDatas().size() > 0) {
                                    programInfoList = moduleInfoResult.getDatas().get(i).getDatas();
                                    title = moduleInfoResult.getDatas().get(i).getBlockTitle();
                                    for (int j = 0; j < programInfoList.size(); j++) {
                                        mProgramInfo = new UserCenterPageBean.Bean();
                                        mProgramInfo.set_title_name(programInfoList.get(j).getTitle());
                                        mProgramInfo.set_contentuuid(programInfoList.get(j).getContentId());
                                        mProgramInfo.set_contenttype(programInfoList.get(j).getContentType());
                                        mProgramInfo.set_imageurl(programInfoList.get(j).getImg());
                                        mProgramInfo.set_actiontype(programInfoList.get(j).getL_actionType());
                                        mProgramInfo.setGrade(programInfoList.get(j).getGrade());
                                        mProgramInfo.setSuperscript(programInfoList.get(j).getRSuperScript());
                                        mDramaRecommendBean.add(mProgramInfo);
                                    }
                                } else {
                                    Log.d(TAG, "wqs:inflateData：i == 2:moduleInfoResult.getDatas().get(0).getDatas()== null");
                                }
                            } else {
                                Log.d(TAG, "wqs:inflateData：i == 2:moduleInfoResult.getDatas().get(0) == null");
                            }
                        } else {
                            Log.d(TAG, "wqs:只取三组数据，多余数据不取");
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "wqs:inflateData：moduleInfoResult.getDatas() == null");
                }
            } else {
                Log.d(TAG, "wqs:inflateData：moduleInfoResult == null");
            }
            if (mAdapter != null) {
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_PROMOTION - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mPromotionRecommendBean;
                } else {
                    Log.d(TAG, "wqs:inflateData：PromotionRecommend==null");
                }
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_INTERESTS - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mInterestsRecommendBean;
                } else {
                    Log.d(TAG, "wqs:inflateData：mInterestsRecommendBean==null");
                }
                UserCenterPageBean = mAdapter.getItem(RECOMMEND_DRAMA - 1);
                if (UserCenterPageBean != null) {
                    UserCenterPageBean.data = mDramaRecommendBean;
                    UserCenterPageBean.title = title;
                } else {
                    Log.d(TAG, "wqs:inflateData：mDramaRecommendBean==null");
                }
            } else {
                Log.d(TAG, "wqs:inflateData：mAdapter == null");
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessage(RECOMMEND);
            } else {
                Log.d(TAG, "wqs:inflateData：mHandler == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:inflateData:Exception:" + e.toString());
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
        Log.d(TAG, "wqs:onResume");
        //会员中心首页上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "5,");
        requestUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "wqs:onDestroy");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        unMemberInfoSubscribe();
        unRecommendSubscribe();
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
                    Toast.makeText(this, "此页面正在开发中", Toast.LENGTH_LONG).show();
                    break;
                case R.id.id_member_center_btn_order:
                    if (!TextUtils.isEmpty(mLoginTokenString)) {
                        mPageClass = MyOrderActivity.class;
                    } else {
                        mPageClass = LoginActivity.class;
                    }
                    break;
                case R.id.id_member_center_btn_drama_library:
                    Constant.MEMBER_CENTER_PARAMS = Constant.getBaseUrl(AppHeadersInterceptor.MEMBER_CENTER_PARAMS);
                    if (!TextUtils.isEmpty(Constant.MEMBER_CENTER_PARAMS)) {
                        intent.putExtra("action", "panel");
                        intent.putExtra("params", Constant.MEMBER_CENTER_PARAMS);
                        Log.d(TAG, "wqs:MEMBER_CENTER_PARAMS:action:panelwqs:-params:" + Constant.MEMBER_CENTER_PARAMS);
                        mPageClass = SplashActivity.class;
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
                                entity.get_contentuuid(), "");
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
            if (mPageClass == SplashActivity.class) {
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
                default:
                    break;
            }

        }
    }
}
