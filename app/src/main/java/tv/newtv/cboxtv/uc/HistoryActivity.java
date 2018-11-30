package tv.newtv.cboxtv.uc;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;
import com.newtv.libs.util.XunMaKeyUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.player.model.PlayCheckRequestBean;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.views.GridRecycleView;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public class HistoryActivity extends FragmentActivity implements
        OnRecycleItemClickListener<UserCenterPageBean.Bean>, View.OnFocusChangeListener, View
        .OnKeyListener {

    private static final int UPDATE = 1001;
    private static final int SYNC_DATA_COMPLETE = 1002;

    private final int COLUMN_COUNT = 6;
    private final String TAG = "lx";
    public int action_type;
    public String title;
    @BindView(R.id.id_usercenter_fragment_root)
    GridRecycleView mRecyclerView;
    @BindView(R.id.user_info_title)
    TextView mPageTitle;
    @BindView(R.id.id_operation_icon)
    ImageView operationIcon;
    @BindView(R.id.id_operation_tip)
    TextView operationText;
    private HistoryAdapter mAdapter;
    private BackgroundTipView deleteView;
    private int selectPostion = 0;
    private boolean NeedRefresh = true;
    private Interpolator mSpringInterpolator;
    private List<UserCenterPageBean.Bean> mCollectBean;
    private List<UserCenterPageBean.Bean> historyRecords;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == UPDATE) {
                mAdapter.clear();
                mCollectBean = (List<UserCenterPageBean.Bean>) message.obj;
                if (mCollectBean != null && mCollectBean.size() != 0) {
                    mAdapter.appendToList(mCollectBean);
                    mAdapter.notifyDataSetChanged();
                } else {
                    // 展示无观看记录的提示
                    ViewStub viewStub = findViewById(R.id.id_empty_view_vs);
                    if (viewStub != null) {
                        View emptyView = viewStub.inflate();
                        if (emptyView != null) {
                            TextView textView = emptyView.findViewById(R.id.empty_textview);
                            if (textView != null) {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("您还没有观看任何节目哦~");
                            }
                        }
                    }
                }
            } else if (message.what == SYNC_DATA_COMPLETE) {
                if (remoteDataReqComp && localDataReqComp) {
                    mHandler.removeMessages(SYNC_DATA_COMPLETE);

                    historyRecords = new ArrayList<>();

                    List<UserCenterPageBean.Bean> temp = new ArrayList<>(Constant.BUFFER_SIZE_16);
                    temp.addAll(remoteData);
                    temp.addAll(localData);

                    for (UserCenterPageBean.Bean item : temp) {
                        if (!isSameItem(item, historyRecords)) {
                            historyRecords.add(item);
                        }
                    }

//                    Collections.sort(historyRecords, new Comparator<UserCenterPageBean.Bean>() {
//                        @Override
//                        public int compare(UserCenterPageBean.Bean left, UserCenterPageBean.Bean right) {
//                            long leftUpdateTime = left.getUpdateTime();
//                            long rightUpdateTime = right.getUpdateTime();
//                            if (leftUpdateTime == rightUpdateTime) {
//                                return 0;
//                            }
//
//                            if (leftUpdateTime < rightUpdateTime) {
//                                return 1;
//                            }
//
//                            if (leftUpdateTime > rightUpdateTime) {
//                                return -1;
//                            }
//
//                            return 0;
//                        }
//                    });

                    Message msg = Message.obtain();
                    msg.what = UPDATE;
                    msg.obj = historyRecords;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessageDelayed(SYNC_DATA_COMPLETE, 100);
                }
            }
            return false;
        }
    });
    private String tableName;
    private View defaultFocusView;
    private String userId;
    private String mLoginTokenString;//登录token,用于判断登录状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        mSpringInterpolator = new OvershootInterpolator(2.2f);
        init();
        initView();
        initData();
    }

    private void initView() {
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
//        // layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
// RecyclerView.State state) {
//                int index = parent.getChildLayoutPosition(view);
//                if (index < COLUMN_COUNT) {
//                    outRect.top = 23;
//                }
//
//                outRect.bottom = 72;
//            }
//        });

        if (action_type != UserCenterFragment.HISTORY) {
            if (operationText != null) {
                operationText.setVisibility(View.INVISIBLE);
            }

            if (operationIcon != null) {
                operationIcon.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        XunMaKeyUtils.key(event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (deleteView != null) {
                    return true;
                }
                if (canUseDelete()) {
                    mAdapter.setAllowLostFocus(false);
                    defaultFocusView = mRecyclerView.findFocus();
                    showDeleteDialog();
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (deleteView != null) {
                    deleteView.setFocusable(false);
                    if (defaultFocusView != null) {
                        defaultFocusView.requestFocus();
                    }
                    mAdapter.setAllowLostFocus(true);
                    ViewGroup contentGroup = getWindow().getDecorView().findViewById(android.R.id
                            .content);
                    deleteView.release();
                    contentGroup.removeView(deleteView);
                    deleteView = null;
                    defaultFocusView = null;
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {


            } else {
                if (deleteView != null) {
                    int direction = View.FOCUS_RIGHT;
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                        direction = View.FOCUS_LEFT;
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        direction = View.FOCUS_RIGHT;
                    } else {
                        return true;
                    }
                    View focusView = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                    deleteView,
                            deleteView.findFocus(), direction);
                    if (focusView != null) {
                        focusView.requestFocus();
                    }
                    return true;
                }
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            return super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 是否可以使用删除功能
     *
     * @return
     */
    private boolean canUseDelete() {
        switch (action_type) {
            case UserCenterFragment.HISTORY:
                return true;
            default:
                return false;
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

    private void showDeleteDialog() {
        if (mCollectBean == null || mCollectBean.size() < 0) return;
        if (mAdapter.getSelectPostion() < 0) return;
        mAdapter.setAllowLostFocus(false);

        View focusView = mRecyclerView.findFocus();
        if (focusView == null) return;

        TextView textView = focusView.findViewById(R.id.id_title);
        if (textView != null) {
           // textView.setEllipsize(null);
            textView.setSelected(false);
        }

        focusView.destroyDrawingCache();

        focusView.setDrawingCacheEnabled(true);
        focusView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        focusView.buildDrawingCache(true);
        Bitmap bitmap = focusView.getDrawingCache(true);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap mBitmap = BitmapUtil.zoomImg(bitmap, 1.1f, 1.1f);
        bitmap.recycle();

        Rect visibleRect = new Rect();
        focusView.getGlobalVisibleRect(visibleRect);

        focusView.destroyDrawingCache();
        focusView.setDrawingCacheEnabled(false);

        if (deleteView == null) {
            deleteView = (BackgroundTipView) getLayoutInflater().inflate(R.layout
                            .history_deletetip_layout, null,
                    false);
        }
        if (deleteView != null) {
            if (deleteView.getParent() != null) {
                ((ViewGroup) deleteView.getParent()).removeView(deleteView);
                deleteView.release();
            }
            deleteView.setFocusable(true);
            deleteView.setVisibleRect(mBitmap, visibleRect);
            ViewGroup contentGroup = getWindow().getDecorView().findViewById(android.R.id.content);
            contentGroup.addView(deleteView);
            ImageButton ivDeleteSinle = (ImageButton) deleteView.findViewById(R.id
                    .delete_single_btn);
            ivDeleteSinle.requestFocus();
            ivDeleteSinle.setOnFocusChangeListener(this);
            ivDeleteSinle.setOnKeyListener(this);
            ImageButton ivDeleteAll = (ImageButton) deleteView.findViewById(R.id.delete_all_btn);
            ivDeleteAll.setOnKeyListener(this);
            ivDeleteAll.setOnFocusChangeListener(this);
        }
    }

    private void initData() {

        int defaultICon = 0;
        switch (action_type) {
            case UserCenterFragment.HISTORY:
//                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,1");//历史观看记录
                // tableName = DBConfig.HISTORY_TABLE_NAME;
                defaultICon = R.drawable.uc_no_history;

//                String token = SharePreferenceUtils.getToken(getApplicationContext());
//                if (!TextUtils.isEmpty(token)) {
//                    tableName = DBConfig.HISTORY_TABLE_NAME;
//                    userId = SharePreferenceUtils.getUserId(getApplicationContext());
//                } else {
//                    tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
//                    userId = "";
//                }
//
//                Log.d(TAG, "initData userId : " + userId + ", tableName : " + tableName);

                break;
            case UserCenterFragment.COLLECT:
                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,0");//收藏
                defaultICon = R.drawable.uc_no_collect;
                tableName = DBConfig.COLLECT_TABLE_NAME;
                break;
            case UserCenterFragment.ATTENTION:
                defaultICon = R.drawable.uc_no_attention;
                tableName = DBConfig.ATTENTION_TABLE_NAME;
                break;
            case UserCenterFragment.SUBSCRIBE:
                defaultICon = R.drawable.uc_no_subscribe;
                tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
                break;
        }

        mAdapter = new HistoryAdapter(getApplicationContext(), defaultICon, this);
        mRecyclerView.setAdapter(mAdapter);
        mPageTitle.setText(title);
//        requestData(tableName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //观看记录页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,1");
//        String token = SharePreferenceUtils.getToken(getApplicationContext());
//        if (TextUtils.isEmpty(token)) {
//            tableName = DBConfig.HISTORY_TABLE_NAME;
//            userId = SystemUtils.getDeviceMac(getApplicationContext());
//        } else {
//            tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
//            userId = SharePreferenceUtils.getUserId(getApplicationContext());
//        }

//        Log.d(TAG, "onResume userId : " + userId + ", tableName : " + tableName);
//
//        if (tableName != null) {
//            MainLooper.get().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    requestData(tableName);
//                }
//            }, 400);
//        }
        //获取登录状态
        requestUserInfo();
    }

    //获取用户登录状态
    private void requestUserInfo() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh
                        (getApplicationContext());
                Log.d(TAG, "---isTokenRefresh:status:" + status);
                //获取登录状态
                mLoginTokenString = SharePreferenceUtils.getToken(getApplicationContext());
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    userId = SharePreferenceUtils.getUserId(getApplicationContext());
                    e.onNext(mLoginTokenString);
                } else {
                    userId = SystemUtils.getDeviceMac(getApplicationContext());
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

    private void requestDataByDB(String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, final String result) {
                        Log.e(TAG, "---result = " + result);
                        if (code == 0) {
                            Gson mGson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                            final List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson(result, type);
                            Message msg = Message.obtain();
                            msg.what = UPDATE;
                            msg.obj = mCollectBean;
                            mHandler.sendMessage(msg);
                        }
                    }
                }).excute();
    }

    private List<UserCenterPageBean.Bean> localData;
    private List<UserCenterPageBean.Bean> remoteData;
    private boolean localDataReqComp;
    private boolean remoteDataReqComp;

    private void requestData() {
        if (!TextUtils.isEmpty(mLoginTokenString)) {
            if (SharePreferenceUtils.getSyncStatus(getApplicationContext()) == 0) {
                DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(getApplicationContext()))
                        .OrderBy(DBConfig.ORDER_BY_TIME)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, final String result) {
                                Log.e(TAG, "request local data complete result : " + result);
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                                    localData = mGson.fromJson(result, type);

                                    if (localData == null) {
                                        Log.d(TAG, "localdata is null");
                                        localData = new ArrayList<>();
                                    }
                                }

                                localDataReqComp = true;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(SYNC_DATA_COMPLETE);
                                }
                            }
                        }).excute();

                DataSupport.search(DBConfig.REMOTE_HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, userId)
                        .OrderBy(DBConfig.ORDER_BY_TIME)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, final String result) {
                                Log.e(TAG, "request remote data complete result : " + result);
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {}.getType();
                                    remoteData = mGson.fromJson(result, type);

                                    if (remoteData == null) {
                                        remoteData = new ArrayList<>();
                                    }
                                }

                                remoteDataReqComp = true;
                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(SYNC_DATA_COMPLETE);
                                }
                            }
                        }).excute();
            } else {
                requestDataByDB(DBConfig.REMOTE_HISTORY_TABLE_NAME);
            }
        } else {
            requestDataByDB(DBConfig.HISTORY_TABLE_NAME);
        }
    }

    private void init() {
        title = getIntent().getStringExtra("title");
        action_type = getIntent().getIntExtra("action_type", 0);
    }

//    /**
//     * 设置empty view的可见性, 该emptyview用于提示页面数据获取异常
//     *
//     * @param visibility
//     */
//    private void setTipVisibility(int visibility) {
//        if (mEmptyView != null) {
//            mEmptyView.setVisibility(visibility);
//        }
//    }

    @Override
    public void onItemClick(View view, int Position, UserCenterPageBean.Bean object) {
        JumpUtil.activityJump(getApplicationContext(), object._actiontype, object._contenttype,
                object._contentuuid, "");
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, UserCenterPageBean
            .Bean object) {

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(v);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(v);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {


        if (event.getAction() == KeyEvent.ACTION_UP || event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

                switch (v.getId()) {
                    case R.id.delete_all_btn:
                        mAdapter.setAllowLostFocus(true);
                        if (mCollectBean != null && mCollectBean.size() != 0) {

                            UserCenterRecordManager.getInstance()
                                    .deleteRecord(UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_HISTORY,
                                            getApplicationContext(),
                                            "clean",
                                            null,
                                            null,
                                            new DBCallback<String>() {
                                                @Override
                                                public void onResult(int code, String result) {
                                                    //清空所有历史记录，上传seriesID字段
                                                    StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
                                                    for (int i = 0; i < mCollectBean.size(); i++) {
                                                        if (i < mCollectBean.size() - 1) {
                                                            dataBuff.append(mCollectBean.get(i)
                                                                    ._contentuuid)
                                                                    .append(",");
                                                        }
                                                        if (i == mCollectBean.size() - 1) {
                                                            dataBuff.append(mCollectBean.get(i)
                                                                    ._contentuuid)
                                                                    .trimToSize();
                                                        }
                                                    }

                                                    LogUploadUtils.uploadLog(Constant
                                                            .LOG_NODE_HISTORY, "2," + dataBuff
                                                            .toString());//清空所有历史记录

                                                    mRecyclerView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext
                                                                    (), "删除成功", Toast
                                                                    .LENGTH_SHORT).show();
                                                            if (deleteView != null) {
                                                                ViewGroup contentGroup =
                                                                        getWindow().getDecorView
                                                                                ().findViewById
                                                                                (android.R.id
                                                                                        .content);
                                                                contentGroup.removeView(deleteView);
                                                                deleteView = null;
                                                            }
                                                            RxBus.get().post(Constant
                                                                    .UPDATE_UC_DATA, true);
                                                        }
                                                    });
                                                }
                                            });
                            requestUserInfo();

//                            DataSupport.delete(DBConfig.HISTORY_TABLE_NAME).condition()
//                                    .build()
//                                    .withCallback(new DBCallback<String>() {
//                                        @Override
//                                        public void onResult(int code, final String result) {
//                                            if (code == 0) {
//                                                LogUploadUtils.uploadLog(Constant
// .LOG_NODE_HISTORY, "2," + " ");//清空所有历史记录
//                                                mRecyclerView.post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(getApplicationContext(),
// "删除成功", Toast.LENGTH_SHORT).show();
//                                                        if (deleteView != null) {
//                                                            ViewGroup contentGroup = getWindow
// ().getDecorView().findViewById(android.R.id.content);
//                                                            contentGroup.removeView(deleteView);
//                                                            deleteView = null;
//                                                        }
//                                                        RxBus.get().post(Constant
// .UPDATE_UC_DATA, true);
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }).excute();
//                            String tableName = DBConfig.HISTORY_TABLE_NAME;
//                            requestData();


                        }


                        break;

                    case R.id.delete_single_btn:
                        mAdapter.setAllowLostFocus(true);
                        if (mCollectBean != null && mCollectBean.size() != 0) {
                            selectPostion = mAdapter.getSelectPostion();

                            Log.d(TAG, "单点删除 selection : " + selectPostion + " dataUserId : " + mCollectBean.get(selectPostion).getUser_id());

                            UserCenterRecordManager.getInstance()
                                    .deleteRecord(UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_HISTORY,
                                            getApplicationContext(),
                                            mCollectBean.get(mAdapter.getSelectPostion())
                                                    ._contentuuid,
                                            mCollectBean.get(mAdapter.getSelectPostion())
                                                    ._contenttype,
                                            mCollectBean.get(mAdapter.getSelectPostion())
                                                    .getUser_id(),
                                            new DBCallback<String>() {
                                                @Override
                                                public void onResult(int code, String result) {
                                                    Log.d(TAG, "onResult remove");
                                                    LogUploadUtils.uploadLog(Constant
                                                            .LOG_NODE_HISTORY, "1," +
                                                            mCollectBean.get(mAdapter
                                                                    .getSelectPostion())
                                                                    ._contentuuid);//删除历史记录
                                                    mRecyclerView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext
                                                                    (), "删除成功", Toast
                                                                    .LENGTH_SHORT).show();
                                                            if (deleteView != null) {
                                                                ViewGroup contentGroup =
                                                                        getWindow().getDecorView
                                                                                ().findViewById
                                                                                (android.R.id
                                                                                        .content);
                                                                contentGroup.removeView(deleteView);
                                                                deleteView = null;
                                                            }

                                                            mAdapter.setAllowLostFocus(true);
                                                            if (mAdapter.getItemCount() > 0 && selectPostion >= 1) {
                                                                GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                                                                View focusView = layoutManager.findViewByPosition(selectPostion - 1);

                                                                if (focusView != null) {
                                                                    focusView.requestFocus();
                                                                }

                                                                mAdapter.removeItem(selectPostion);
                                                            } else {
//                                                            requestData(tableName);
                                                                requestUserInfo();
                                                            }
                                                        }
                                                    });
                                                }
                                            });


//                            DataSupport.delete(DBConfig.HISTORY_TABLE_NAME).condition()
//                                    .eq(DBConfig.CONTENTUUID, mCollectBean.get(mAdapter
// .getSelectPostion())._contentuuid)
//                                    .build()
//                                    .withCallback(new DBCallback<String>() {
//                                        @Override
//                                        public void onResult(int code, final String result) {
//                                            if (code == 0) {
//
//                                                LogUploadUtils.uploadLog(Constant
// .LOG_NODE_HISTORY, "1," + mCollectBean.get(mAdapter.getSelectPostion())._contentuuid);//删除历史记录
//                                                mRecyclerView.post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(getApplicationContext(),
// "删除成功", Toast.LENGTH_SHORT).show();
//                                                        if (deleteView != null) {
//                                                            ViewGroup contentGroup = getWindow
// ().getDecorView().findViewById(android.R.id.content);
//                                                            contentGroup.removeView(deleteView);
//                                                            deleteView = null;
//                                                        }
//
//                                                        mAdapter.setAllowLostFocus(true);
//                                                        if (mAdapter.getItemCount() > 0 &&
// selectPostion >= 1) {
//                                                            StaggeredGridLayoutManager
// layoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
//
//                                                            View focusView = layoutManager
// .findViewByPosition(selectPostion - 1);
//
//                                                            if (focusView != null) {
//                                                                focusView.requestFocus();
//                                                            }
//
//                                                            mAdapter.removeItem(selectPostion);
//                                                        } else {
////                                                            requestData(tableName);
//                                                            requestUserInfo();
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }).excute();
                        }

                        break;


                }


            }


        }
        return false;
    }
}
