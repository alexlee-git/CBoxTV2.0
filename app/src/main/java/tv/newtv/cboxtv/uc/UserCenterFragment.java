package tv.newtv.cboxtv.uc;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;
import com.newtv.libs.util.RxBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.views.widget.ScrollSpeedLinearLayoutManger;
//收藏节目集和节目
//关注人物
//订阅CCtv
//历史记录

public class UserCenterFragment extends BaseFragment implements OnRecycleItemClickListener<UserCenterPageBean.Bean> {

    private static final String TAG = UserCenterFragment.class.getName();

    private String param;
    private String contentId;
    private String defaultFocus;
    private String actionType;

    private AiyaRecyclerView mRecyclerView;
    private TextView mEmptyView;

    private Disposable mDisposable;


    private boolean isPrepared = false;

    private List<UserCenterPageBean> pageData;

    public static final int SUBSCRIBE = 2;
    public static final int COLLECT = 3;
    public static final int HISTORY = 1;
    public static final int ATTENTION = 4;

    private Observable<Boolean> mUpdateDataObservable;
    private UserCenterAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SUBSCRIBE:
                case COLLECT:
                case HISTORY:
                case ATTENTION:
                    bindData(msg.what);
                    break;
            }
        }
    };
    private View view;
    private boolean istop = false;
    @Override
    public boolean isNoTopView() {
        if (mRecyclerView != null && ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition
                () == 0) {
            View view = FocusFinder.getInstance().findNextFocus(mRecyclerView,
                    mRecyclerView.findFocus(), View.FOCUS_UP);
            return view == null;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        ScrollSpeedLinearLayoutManger linearLayoutManager = (ScrollSpeedLinearLayoutManger) mRecyclerView.getLayoutManager();
        if (mRecyclerView.computeVerticalScrollOffset() != 0) {
            linearLayoutManager.smoothScrollToPosition(mRecyclerView, 0);
        }

        return super.onBackPressed();
    }

    public static BaseFragment newInstance(Bundle paramBundle) {
        BaseFragment fragment = new UserCenterFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            param = bundle.getString("nav_text");
            contentId = bundle.getString("content_id");
            actionType = bundle.getString("actionType");
        }
        pageData = new ArrayList<>();
        pageData.add(new UserCenterPageBean("观看记录"));
        pageData.add(new UserCenterPageBean("我的订阅"));
        pageData.add(new UserCenterPageBean("我的收藏"));
        pageData.add(new UserCenterPageBean("我的关注"));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_uc_fragment, container, false);
        init();
        mRecyclerView = (AiyaRecyclerView) view.findViewById(R.id.id_usercenter_fragment_root);
        mEmptyView = (TextView) view.findViewById(R.id.id_empty_view);
        mRecyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(LauncherApplication.AppContext));
//        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.appendToList(pageData);

        setAnimRecyclerView(mRecyclerView);
        return view;
    }

    private void init() {
        mAdapter = new UserCenterAdapter(getActivity(), this);
        mAdapter.setHasStableIds(true);
        mUpdateDataObservable = RxBus.get().register(Constant.UPDATE_UC_DATA);
        mUpdateDataObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isUpdate) throws Exception {
                        if (isUpdate) {
                            requestData("");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    //读取某用户下所有数据
    private void requestData(final String userId) {
        DataSupport.search(DBConfig.SUBSCRIBE_TABLE_NAME)
                .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build().withCallback(new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    UserCenterPageBean userCenterPageBean = mAdapter.getItem(1);
                    Gson mGson = new Gson();
                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                    }.getType();
                    List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                    Log.e("UserCenterFragment", "==我的订阅==" + mGson.toJson(mCollectBean));
                    userCenterPageBean.data = mCollectBean;
                    mHandler.sendEmptyMessage(SUBSCRIBE);
                }
            }
        }).excute();
        DataSupport.search(DBConfig.COLLECT_TABLE_NAME) .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build().withCallback(new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    UserCenterPageBean userCenterPageBean = mAdapter.getItem(2);
                    Gson mGson = new Gson();
                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                    }.getType();
                    List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                    Log.e("UserCenterFragment", "==我的收藏==" + mGson.toJson(mCollectBean));
                    userCenterPageBean.data = mCollectBean;
                   mHandler.sendEmptyMessage(COLLECT);

                }
            }
        }).excute();
        DataSupport.search(DBConfig.HISTORY_TABLE_NAME) .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build().withCallback(new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    UserCenterPageBean userCenterPageBean = mAdapter.getItem(0);
                    Gson mGson = new Gson();
                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                    }.getType();
                    List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                    Log.e("UserCenterFragment", "==播放记录==" + mGson.toJson(mCollectBean));
                    userCenterPageBean.data = mCollectBean;
                   mHandler.sendEmptyMessage(HISTORY);
                }
            }
        }).excute();
        DataSupport.search(DBConfig.ATTENTION_TABLE_NAME) .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build().withCallback(new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    UserCenterPageBean userCenterPageBean = mAdapter.getItem(3);
                    Gson mGson = new Gson();
                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                    }.getType();
                    List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                    Log.e("UserCenterFragment", "==我的关注==" + mGson.toJson(mCollectBean));
                    userCenterPageBean.data = mCollectBean;

                    mHandler.sendEmptyMessage(ATTENTION);
                }
            }
        }).excute();



    }

    /**
     * 设置empty view的可见性, 该emptyview用于提示页面数据获取异常
     *
     * @param visibility
     */
    private void setTipVisibility(int visibility) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visibility);
        }
    }

    /**
     * 创建RecyclerView的适配器, 并绑定给RecyclerView
     *
     * @param
     */
    private void bindData(int position) {
        if (mAdapter!=null){
            mAdapter.notifyItemChanged(position);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        requestData(contentId);
    }

    @Override
    public void onDestroy() {
        RxBus.get().unregister(Constant.UPDATE_UC_DATA, mUpdateDataObservable);
        super.onDestroy();
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public void onItemClick(View view, int position, UserCenterPageBean.Bean entity) {
        Intent intent = new Intent();
        Class clazz = null;
        switch (view.getId()) {
            case R.id.id_usercenter_btn_history:
                intent.putExtra("action_type", HISTORY);
                intent.putExtra("title", "观看记录");
                clazz = HistoryActivity.class;
                break;
            case R.id.id_usercenter_btn_subscribe:
                intent.putExtra("action_type", SUBSCRIBE);
                intent.putExtra("title", "我的订阅");
                clazz = HistoryActivity.class;
                break;
            case R.id.id_usercenter_btn_collect:
                intent.putExtra("action_type", COLLECT);
                intent.putExtra("title", "我的收藏");
                clazz = HistoryActivity.class;
                break;
            case R.id.id_usercenter_btn_attention:
                intent.putExtra("action_type", ATTENTION);
                intent.putExtra("title", "我的关注");
                clazz = HistoryActivity.class;

                break;
            case R.id.id_usercenter_btn_version:
               clazz = VersionUpdateActivity.class;
                // clazz = PersonsDetailsActivity.class;
               // clazz = ColumnDetailsPageActivity.class;

                break;
            case R.id.id_usercenter_btn_about:
               // clazz = ProgramDetailsPageActivity.class;
                //  clazz = ProgramListDetailActiviy.class;
              //  clazz = ProgrameSeriesDetailsActivity.class;
               // //  clazz = PersonsDetailsActivity.class;
               //clazz = ColumnDetailsPageActivity.class;
              clazz = AboutMineActivity.class;
               // clazz = DetailsPageActivity.class;
            //   clazz = ProgramsCountSelectActivity.class;
                break;
            case R.id.id_module_8_view1:
            case R.id.id_module_8_view2:
            case R.id.id_module_8_view3:
            case R.id.id_module_8_view4:
            case R.id.id_module_8_view5:
            case R.id.id_module_8_view6:
                if (entity != null) {
                    JumpUtil.activityJump(getContext(), entity._actiontype, entity._contenttype, entity._contentuuid, "");
                }
                return;
        }
        if (clazz == null) {
            return;
        }
        intent.setClass(getActivity(), clazz);
        startActivity(intent);

    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, UserCenterPageBean.Bean object) {


    }

    @Override
    public View getFirstFocusView() {
        if (mAdapter==null)
            return null;
        return mAdapter.getFirstView();
    }
}
