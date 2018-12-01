package tv.newtv.cboxtv.uc.v2.sub;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.BaseDetailSubFragment;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;

/**
 * 项目名称:         熊猫ROM-launcher应用
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午7:08
 * 创建人:           lixin
 * 创建日期:         2018/9/11
 */


public class FollowRecordFragment extends BaseDetailSubFragment {
    private final String TAG = "FollowRecordFragment";
    private RecyclerView mRecyclerView;
    private List<UserCenterPageBean.Bean> mDatas;
    private String mLoginTokenString;//登录token,用于判断登录状态
    private TextView emptyTextView;
    public static final int INFLATE_DATA_BASE = 1001;//填充数据库数据
    private final int COLUMN_COUNT = 6;
    private String userId;

    private UserCenterUniversalAdapter mAdapter;

    private List<UserCenterPageBean.Bean> localData;
    private List<UserCenterPageBean.Bean> remoteData;
    private boolean localDataReqComp;
    private boolean remoteDataReqComp;

    private static final int MSG_SYNC_DATA_COMP = 10033;
    private static final int MSG_INFLATE_PAGE = 10034;

    private static FollowHandler mHandler;
    private int move =-1;
    private Observable<Integer> observable;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_record;
    }

    @Override
    protected void updateUiWidgets(View view) {
        Log.e(TAG, "---updateUiWidgets");
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
        localDataReqComp = false;
        remoteDataReqComp = false;

        Log.d("follow", "requestData");
        if (!TextUtils.isEmpty(mLoginTokenString)) {
            if (SharePreferenceUtils.getSyncStatus(LauncherApplication.AppContext) == 0) {
                DataSupport.search(DBConfig.ATTENTION_TABLE_NAME)
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
                                }

                                Log.d("follow", "本地数据库查询完毕");
                                localDataReqComp = true;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_SYNC_DATA_COMP);
                                }
                            }
                        }).excute();

                DataSupport.search(DBConfig.REMOTE_ATTENTION_TABLE_NAME)
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
                                }

                                Log.d("follow", "远程数据库查询完毕");

                                remoteDataReqComp = true;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_SYNC_DATA_COMP);
                                }
                            }
                        }).excute();
            } else {
                requestDataByDB(DBConfig.REMOTE_ATTENTION_TABLE_NAME);
            }
        } else {
            requestDataByDB(DBConfig.ATTENTION_TABLE_NAME);
        }
    }

    private void inflate(List<UserCenterPageBean.Bean> bean) {
        if (contentView == null) {
            return;
        }
        if (bean == null || bean.size() == 0) {
            inflatePageWhenNoData();
            return;
        }

        if (mDatas == null) {
            mDatas = bean;
            mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));
            mAdapter = new UserCenterUniversalAdapter(getActivity(), mDatas, Constant.UC_FOLLOW);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);

            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.bottom = 72;
                    outRect.top = 23;
                    int index = parent.getChildLayoutPosition(view);

                    if (index <=COLUMN_COUNT) {

                    }
                }
            });
        } else {
            if (mAdapter != null && mDatas != null) {
                boolean refresh=(bean.size() ==mDatas.size());
                mAdapter.setRefresh(refresh);
                mDatas.clear();
                mDatas.addAll(bean);
                if (!refresh){
                        mAdapter.notifyItemRemoved(move);
                    }

            }
        }
    }
    private void inflatePageWhenNoData() {
        mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
        mRecyclerView.setVisibility(View.INVISIBLE);

        showEmptyTip();
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
                    emptyTextView.setText("您还没有关注任何人物哦～");
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        observable = RxBus.get().register("recordPosition");
        observable.observeOn(AndroidSchedulers .mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        move = integer;
                    }
                });


        //关注页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,4");
        //获取用户登录状态
        requestUserInfo();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new FollowHandler(this);

    }

    class FollowHandler extends android.os.Handler {
        WeakReference<FollowRecordFragment> reference;

        FollowHandler(FollowRecordFragment setFragment) {
            reference = new WeakReference<>(setFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SYNC_DATA_COMP) {
                checkDataSync();
            } else if (msg.what == MSG_INFLATE_PAGE) {
                Log.d("follow", "接收到 MSG_INFLATE_PAGE 消息");
                List<UserCenterPageBean.Bean> datas = (List<UserCenterPageBean.Bean>) msg.obj;


                if (datas != null && datas.size() > 0) {
                    inflate(datas);
                } else {
                    inflatePageWhenNoData();
                }
            } else {
                Log.d("sub", "unresolved msg : " + msg.what);
            }

        }
    }

    private void checkDataSync() {
        Log.d("follow", "checkDataSync");
        if (remoteDataReqComp && localDataReqComp) {
            mHandler.removeMessages(MSG_SYNC_DATA_COMP);

            List<UserCenterPageBean.Bean> followRecords = new ArrayList<>();
            List<UserCenterPageBean.Bean> temp = new ArrayList<>(Constant.BUFFER_SIZE_16);
            temp.addAll(remoteData);
            temp.addAll(localData);

            for (UserCenterPageBean.Bean item : temp) {
                if (!isSameItem(item, followRecords)) {
                    followRecords.add(item);
                }
            }

            Message msg = Message.obtain();
            msg.what = MSG_INFLATE_PAGE;
            msg.obj = followRecords;
            mHandler.sendMessage(msg);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_SYNC_DATA_COMP, 100);
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

    private void requestDataByDB(String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                            List<UserCenterPageBean.Bean> universalBeans = gson.fromJson(result, type);
                            inflate(universalBeans);
                        }
                    }
                })
                .excute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister("recordPosition",observable);
    }
}
