package tv.newtv.cboxtv.uc.v2.sub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

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
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.screenList.tablayout.TabLayout;
import tv.newtv.cboxtv.cms.util.ModuleUtils;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.BaseDetailSubFragment;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午12:24
 * 创建人:           lixin
 * 创建日期:         2018/9/24
 */


public class CollectionProgramSetFragment extends BaseDetailSubFragment implements PageContract.View {
    private final String TAG = "CollectionProgramSetFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView mHotRecommendRecyclerView;
    private TextView emptyTextView;
    private TextView mHotRecommendTitle;
    private List<UserCenterPageBean.Bean> mDatas;
    private String mLoginTokenString;//登录token,用于判断登录状态
    private String userId;
    private UserCenterUniversalAdapter mAdapter;
    private final int COLUMN_COUNT = 6;
    private PageContract.ContentPresenter mContentPresenter;

    private List<UserCenterPageBean.Bean> localData;
    private List<UserCenterPageBean.Bean> remoteData;
    private boolean localDataReqComp;
    private boolean remoteDataReqComp;

    private static final int MSG_SYNC_DATA_COMP = 10033;
    private static final int MSG_INFLATE_PAGE = 10034;

    private static CollectionHandler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_record;
    }

    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new CollectionHandler(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取用户登录状态
        requestUserInfo();
    }

    @Override
    protected void updateUiWidgets(View view) {
    }

    //获取用户登录状态
    private void requestUserInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(getActivity());
                Log.d(TAG, "---isTokenRefresh:status:" + status);
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
                        requestData();
                    }
                });
    }

    //读取数据库中数据
    private void requestData() {
        if (!TextUtils.isEmpty(mLoginTokenString)) {
            if (SharePreferenceUtils.getSyncStatus(LauncherApplication.AppContext) == 0) {
                DataSupport.search(DBConfig.COLLECT_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                        .OrderBy(DBConfig.ORDER_BY_TIME)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, final String result) {
                                if (code == 0) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                                    localData = gson.fromJson(result, type);

                                    if (localData == null) {
                                        localData = new ArrayList<>(Constant.BUFFER_SIZE_8);
                                    }

                                    localDataReqComp = true;
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(MSG_SYNC_DATA_COMP);
                                    }
                                }
                            }
                        }).excute();

                DataSupport.search(DBConfig.REMOTE_COLLECT_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                        .OrderBy(DBConfig.ORDER_BY_TIME)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, final String result) {
                                if (code == 0) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                                    remoteData = gson.fromJson(result, type);

                                    if (remoteData == null) {
                                        remoteData = new ArrayList<>(Constant.BUFFER_SIZE_8);
                                    }

                                    remoteDataReqComp = true;
                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(MSG_SYNC_DATA_COMP);
                                    }
                                }
                            }
                        }).excute();
            } else {
                requestDataByDB(DBConfig.REMOTE_COLLECT_TABLE_NAME);
            }
        } else {
            requestDataByDB(DBConfig.COLLECT_TABLE_NAME);
        }
    }


    private boolean isSameItem(UserCenterPageBean.Bean item, List<UserCenterPageBean.Bean> datas) {
        for (UserCenterPageBean.Bean comp : datas) {
            if (TextUtils.equals(comp.get_contentuuid(), item.get_contentuuid())) {
                return true;
            }
        }
        return false;
    }

    private void checkDataSync() {
        if (remoteDataReqComp && localDataReqComp) {
            mHandler.removeMessages(MSG_SYNC_DATA_COMP);

            List<UserCenterPageBean.Bean> collectionRecords = new ArrayList<>();

            List<UserCenterPageBean.Bean> temp = new ArrayList<>(Constant.BUFFER_SIZE_16);
            temp.addAll(remoteData);
            temp.addAll(localData);

            for (UserCenterPageBean.Bean item : temp) {
                if (!isSameItem(item, collectionRecords)) {
                    collectionRecords.add(item);
                }
            }

            Message msg = Message.obtain();
            msg.what = MSG_INFLATE_PAGE;
            msg.obj = collectionRecords;
            mHandler.sendMessage(msg);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_SYNC_DATA_COMP, 100);
        }
    }

    private void requestDataByDB(String tableName) {
        Log.d("lx", "tableName : " + tableName + ", userId : " + userId);
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            UserCenterPageBean userCenterUniversalBean = new UserCenterPageBean("");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                            List<UserCenterPageBean.Bean> universalBeans = gson.fromJson(result, type);
                            userCenterUniversalBean.data = universalBeans;
                            if (userCenterUniversalBean.data != null && userCenterUniversalBean.data.size() > 0) {
                                inflatePage(userCenterUniversalBean.data);
                            } else {
                                inflatePageWhenNoData();
                            }
                        } else {
                            inflatePageWhenNoData();
                        }
                    }
                }).excute();
    }

    private void inflatePage(List<UserCenterPageBean.Bean> datas) {
        if (contentView == null) {
            return;
        }

        if (datas == null || datas.size() == 0) {
            inflatePageWhenNoData();
            return;
        }

        if (mDatas == null) {
            mDatas = datas;
            mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
            mAdapter = new UserCenterUniversalAdapter(getActivity(), mDatas, Constant.UC_COLLECTION);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.bottom = 72;
                    int index = parent.getChildLayoutPosition(view);
                    if (index < COLUMN_COUNT) {
                        outRect.top = 23;
                    }
                }
            });
        } else {
            if (mAdapter != null && mDatas != null) {
                mDatas.clear();
                mDatas.addAll(datas);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    private void inflatePageWhenNoData() {
        mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
        mRecyclerView.setVisibility(View.INVISIBLE);

        showEmptyTip();
        String hotRecommendParam = BootGuide.getBaseUrl(BootGuide.PAGE_COLLECTION);
        if (!TextUtils.isEmpty(hotRecommendParam)) {
            mContentPresenter = new PageContract.ContentPresenter(getActivity(), this);
            mContentPresenter.getPageContent(hotRecommendParam);
        } else {
            Log.e("collectionFragment", "wqs:PAGE_SUBSCRIPTION==null");
        }

//        showHotRecommend();
    }

    /**
     * 展示无数据提示
     */
    private void showEmptyTip() {
        ViewStub emptyViewStub = contentView.findViewById(R.id.id_empty_view_vs);
        if (emptyViewStub != null) {
            View emptyView = emptyViewStub.inflate();
            if (emptyView != null) {
                if (emptyTextView == null) {
                    emptyTextView = emptyView.findViewById(R.id.empty_textview);
                    emptyTextView.setText("您还没有收藏任何节目哦～");
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 展示热门收藏数据
     */
    private void showHotRecommend() {
        String hotRecommendParam = BootGuide.getBaseUrl(BootGuide.PAGE_COLLECTION);
        NetClient.INSTANCE.getHotSubscribeApi()
                .getHotSubscribeInfo(Libs.get().getAppKey(), Libs.get().getChannelId(), hotRecommendParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody result) {
                        try {
                            ModuleInfoResult infoResult = ModuleUtils.getInstance().parseJsonForModuleInfo(result.string());
                            if (infoResult == null) {
                                return;
                            }

                            List<ModuleItem> moduleItems = infoResult.getDatas();
                            List<Program> programInfos = moduleItems.get(0).getDatas();

                            ViewStub viewStub = contentView.findViewById(R.id.id_hot_recommend_area_vs);
                            if (viewStub != null) {
                                View view = viewStub.inflate();

                                if (view != null) {
                                    mHotRecommendRecyclerView = view.findViewById(R.id.id_hot_recommend_area_rv);
                                    mHotRecommendRecyclerView.setHasFixedSize(true);
                                    mHotRecommendRecyclerView.setItemAnimator(null);
                                    mHotRecommendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
                                        @Override
                                        public boolean canScrollHorizontally() {
                                            return false;
                                        }
                                    });
                                    mHotRecommendRecyclerView.setAdapter(new HotRecommendAreaAdapter(getActivity(), programInfos));
                                    mHotRecommendRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                        @Override
                                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                            int index = parent.getChildLayoutPosition(view);
                                            if (index < COLUMN_COUNT) {
                                                outRect.top = 23;
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onPageResult(@Nullable List<Page> page) {
        try {
            if (page == null && page.size() <= 0) {
                return;
            }
            List<Program> programInfos = page.get(0).getPrograms();

            ViewStub viewStub = contentView.findViewById(R.id.id_hot_recommend_area_vs);
            if (viewStub != null) {
                View view = viewStub.inflate();

                if (view != null) {
                    mHotRecommendTitle = view.findViewById(R.id.id_hot_recommend_area_title);
                    mHotRecommendTitle.setText(page.get(0).getBlockTitle());
                    mHotRecommendRecyclerView = view.findViewById(R.id.id_hot_recommend_area_rv);
                    mHotRecommendRecyclerView.setHasFixedSize(true);
                    mHotRecommendRecyclerView.setItemAnimator(null);
                    mHotRecommendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
                        @Override
                        public boolean canScrollHorizontally() {
                            return false;
                        }
                    });
                    mHotRecommendRecyclerView.setAdapter(new HotRecommendAreaAdapter(getActivity(), programInfos));
                    mHotRecommendRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            int index = parent.getChildLayoutPosition(view);
                            if (index < COLUMN_COUNT) {
                                outRect.top = 23;
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CollectionHandler extends android.os.Handler {

        WeakReference<CollectionProgramSetFragment> reference;

        CollectionHandler(CollectionProgramSetFragment setFragment) {
            reference = new WeakReference<>(setFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SYNC_DATA_COMP) {
                checkDataSync();
            } else if (msg.what == MSG_INFLATE_PAGE) {
                List<UserCenterPageBean.Bean> datas = (List<UserCenterPageBean.Bean>) msg.obj;
                if (datas != null && datas.size() > 0) {
                    inflatePage(datas);
                } else {
                    inflatePageWhenNoData();
                }
            } else {
                Log.d("collection", "unresolved msg : " + msg.what);
            }
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }
}
