package tv.newtv.cboxtv.uc.v2.sub;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.BaseDetailSubFragment;
import tv.newtv.cboxtv.uc.v2.CollectionDetailActivity;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午12:24
 * 创建人:           lixin
 * 创建日期:         2018/9/24
 */


public class CollectionProgramSetFragment extends BaseDetailSubFragment implements PageContract.View {
    private final String TAG = "cpsf";
    private RecyclerView mRecyclerView;
    private View mHotRecommendArea;
    private ImageView mHotRecommendTitleIcon;
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

    private int move = -1;
    private Observable<Integer> observable;

    private Observable<Map<String, String>> operationObs;
    private String operationType;
    private String operationId;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_record;
    }

    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        observable = RxBus.get().register("recordPosition");
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        move = integer;
                    }
                });

        operationObs = RxBus.get().register("col_operation_map");
        operationObs.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, String>>() {
                    @Override
                    public void accept(Map<String, String> map) throws Exception {
                        operationType = map.get("col_operation_type");
                        operationId = map.get("col_operation_id");
                        Log.d(TAG, "type : " + operationType + ", id : " + operationId);
                    }
                });

        //获取用户登录状态
        requestUserInfo();
    }

    @Override
    protected void updateUiWidgets(View view) {
    }

    //获取用户登录状态
    private void requestUserInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
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
        if (!TextUtils.isEmpty(mLoginTokenString)) { // 登录用户
            requestDataByDB(DBConfig.REMOTE_COLLECT_TABLE_NAME);
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

    @Override
    protected void checkDataSync() {
        if (remoteDataReqComp && localDataReqComp) {
            removeMessages(MSG_SYNC_DATA_COMP);

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
            sendMessage(msg);
        } else {
            sendEmptyMessageDelayed(MSG_SYNC_DATA_COMP, 100);
        }
    }

    private void requestDataByDB(String tableName) {
        DataSupport.search(tableName)
                .condition()
                .limit(UserCenterRecordManager.REQUEST_LIST_PAGE_RECORD_LIMIT)
                .noteq(DBConfig.CONTENTTYPE, Constant.CONTENTTYPE_LB)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            UserCenterPageBean userCenterUniversalBean = new UserCenterPageBean("");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                            }.getType();
                            List<UserCenterPageBean.Bean> universalBeans = gson.fromJson(result, type);
                            userCenterUniversalBean.data = universalBeans;
                            if (userCenterUniversalBean.data != null && userCenterUniversalBean.data.size() > 0) {
                                Log.d(TAG, "query collection record from : " + tableName + ", size : " + userCenterUniversalBean.data.size());
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


    @Override
    protected void inflate(List<UserCenterPageBean.Bean> datas) {
        inflatePage(datas);
    }

    protected void inflatePage(List<UserCenterPageBean.Bean> datas) {
        if (contentView == null) {
            return;
        }

        if (datas == null || datas.size() == 0) {
            inflatePageWhenNoData();
            return;
        }

        hideView(emptyTextView);
        hideView(mHotRecommendTitle);
        hideView(mHotRecommendTitleIcon);
        hideView(mHotRecommendRecyclerView);

        showView(mRecyclerView);

        for (int i = 0; i < datas.size(); ++i) {
            Log.d(TAG, "collect record name : " + datas.get(i).get_title_name());
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
            if (mAdapter != null) {
                if (TextUtils.equals(operationType, "delete")) {
                    boolean refresh = (datas.size() == mDatas.size());
                    mAdapter.setRefresh(refresh);
                    mDatas.clear();
                    mDatas.addAll(datas);
                    if (!refresh) {
                        mAdapter.notifyItemRemoved(move);
                    }
                } else if (TextUtils.equals(operationType, "add")) {
                    mDatas.clear();
                    mDatas.addAll(datas);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Override
    public void inflatePageWhenNoData() {
//        hideView(mRecyclerView);
        showEmptyTip();
        CollectionDetailActivity parentActivity = (CollectionDetailActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.currentNavFouse();
        }
        String hotRecommendParam = BootGuide.getBaseUrl(BootGuide.PAGE_COLLECTION);
        if (!TextUtils.isEmpty(hotRecommendParam)) {
            mContentPresenter = new PageContract.ContentPresenter(getActivity(), this);
            mContentPresenter.getPageContent(hotRecommendParam);
        } else {
            Log.i(TAG, "wqs:PAGE_SUBSCRIPTION==null");
        }
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            if (contentView == null) {
                return;
            }
            mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
            mRecyclerView.setVisibility(View.GONE);
        }
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
                    mHotRecommendArea = view;
                    mHotRecommendTitle = view.findViewById(R.id.id_hot_recommend_area_title);
                    mHotRecommendTitle.setText(page.get(0).getBlockTitle());
                    mHotRecommendTitleIcon = view.findViewById(R.id.id_hot_recommend_area_icon);
                    mHotRecommendRecyclerView = view.findViewById(R.id.id_hot_recommend_area_rv);
                    mHotRecommendRecyclerView.setHasFixedSize(true);
                    mHotRecommendRecyclerView.setItemAnimator(null);
                    mHotRecommendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
                        @Override
                        public boolean canScrollHorizontally() {
                            return false;
                        }
                    });
                    mHotRecommendRecyclerView.setAdapter(new HotRecommendAreaAdapter(getActivity(), programInfos, 1));
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

            showView(emptyTextView);
            showView(mHotRecommendTitle);
            showView(mHotRecommendTitleIcon);
            showView(mHotRecommendRecyclerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("recordPosition", observable);
        RxBus.get().unregister("col_operation_map", operationObs);
    }
}
