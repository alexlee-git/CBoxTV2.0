package tv.newtv.cboxtv.uc;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.XunMaKeyUtils;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public class HistoryActivity extends FragmentActivity implements
        OnRecycleItemClickListener<UserCenterPageBean.Bean>, View.OnFocusChangeListener, View
        .OnKeyListener {
    private static final int UPDATE = 1001;
    public int action_type;
    public String title;
    @BindView(R.id.id_usercenter_fragment_root)
    SearchRecyclerView mRecyclerView;
    @BindView(R.id.id_empty_view)
    TextView mEmptyView;
    @BindView(R.id.tv_page_title)
    TextView mPageTitle;
    private HistoryAdapter mAdapter;
    private BackgroundTipView deleteView;

    private int selectPostion;

    private boolean NeedRefresh = true;
    private static boolean eatKeyEvent = false;

    private Interpolator mSpringInterpolator;
    private List<UserCenterPageBean.Bean> mCollectBean;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE) {
                mAdapter.clear();
                mCollectBean = (List<UserCenterPageBean.Bean>) msg.obj;
                if (mCollectBean != null && mCollectBean.size() != 0) {
                    mAdapter.appendToList(mCollectBean);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private String tableName;
    private View defaultFocusView;

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
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);

        View menuTipView = findViewById(R.id.tv_right_tips);
        if (action_type != UserCenterFragment.HISTORY) {
            menuTipView.setVisibility(View.INVISIBLE);
        } else {
            menuTipView.setVisibility(View.VISIBLE);
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
                    ViewGroup contentGroup = getWindow().getDecorView().findViewById(android
                            .R.id
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
        if (event.getRepeatCount() % 8 == 0) {
            eatKeyEvent = false;
        }
        if (eatKeyEvent) {
            return true;
        }
        if (event.getRepeatCount() % 4 == 0) {
            eatKeyEvent = true;
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

    private void showDeleteDialog() {
        if (mCollectBean == null || mCollectBean.size() < 0) return;
        if (mAdapter.getSelectPostion() < 0) return;
        mAdapter.setAllowLostFocus(false);
        NeedRefresh = false;

        View focusView = mRecyclerView.findFocus();
        if (focusView == null) return;

        TextView textView = focusView.findViewWithTag("tag_poster_title");
        if (textView != null) {
            textView.setEllipsize(null);
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


        visibleRect.left -= (mBitmap.getWidth() - w) / 2 + 2;
        visibleRect.top -= (mBitmap.getHeight() - h) / 2;
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
                LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,1");//历史观看记录
                tableName = DBConfig.HISTORY_TABLE_NAME;
                defaultICon = R.drawable.uc_no_history;
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
        requestData(tableName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tableName != null) {
            MainLooper.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestData(tableName);
                }
            },400);

        }

    }

    //DBConfig.COLLECT_TABLE_NAME
    private void requestData(String tableName) {
        if (tableName != null) {
            DataSupport.search(tableName)
                    .condition()
                    .OrderBy(DBConfig.ORDER_BY_TIME)
                    .build()
                    .withCallback(new DBCallback<String>() {
                        @Override
                        public void onResult(int code, final String result) {
                            Log.e("MM", "result=" + result);
                            if (code == 0) {
                                Gson mGson = new Gson();
                                Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                }.getType();
                                final List<UserCenterPageBean.Bean> mCollectBean = mGson.fromJson
                                        (result, type);
                                Message msg = new Message();
                                msg.what = UPDATE;
                                msg.obj = mCollectBean;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }).excute();
        }

    }

    private void init() {
        title = getIntent().getStringExtra("title");
        action_type = getIntent().getIntExtra("action_type", 0);
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
                        if (mCollectBean != null && mCollectBean.size() != 0) {
//                            for (int i = 0; i < mCollectBean.size(); i++) {
//                                final int finalI = i;
                            DataSupport.delete(DBConfig.HISTORY_TABLE_NAME).condition()
//                                        .eq(DBConfig.CONTENTUUID, mCollectBean.get(i)
// ._contentuuid)
                                    .build()
                                    .withCallback(new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, final String result) {
                                            if (code == 0) {

                                                LogUploadUtils.uploadLog(Constant
                                                        .LOG_NODE_HISTORY, "2," + " ");//清空所有历史记录
                                                mRecyclerView.post(new Runnable() {
                                                    @Override
                                                    public void run() {
//                                                            if (finalI ==0){
                                                        Toast.makeText(getApplicationContext(),
                                                                "删除成功", Toast.LENGTH_SHORT).show();
//                                                            }
                                                        if (deleteView != null) {
                                                            ViewGroup contentGroup = getWindow()
                                                                    .getDecorView().findViewById
                                                                            (android.R.id
                                                                                    .content);
                                                            contentGroup.removeView(deleteView);
                                                            deleteView = null;
                                                        }
                                                        RxBus.get().post(Constant.UPDATE_UC_DATA,
                                                                true);

                                                    }
                                                });
                                            }

                                        }
                                    }).excute();
//                            }
                            String tableName = DBConfig.HISTORY_TABLE_NAME;
                            requestData(tableName);
                        }


                        break;

                    case R.id.delete_single_btn:
                        if (mCollectBean != null && mCollectBean.size() != 0) {
                            selectPostion = mAdapter.getSelectPostion();
                            DataSupport.delete(DBConfig.HISTORY_TABLE_NAME).condition()
                                    .eq(DBConfig.CONTENTUUID, mCollectBean.get(mAdapter
                                            .getSelectPostion())._contentuuid)
                                    .build()
                                    .withCallback(new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, final String result) {
                                            if (code == 0) {

                                                LogUploadUtils.uploadLog(Constant
                                                        .LOG_NODE_HISTORY, "1," + mCollectBean
                                                        .get(mAdapter
                                                                .getSelectPostion())
                                                        ._contentuuid);//删除历史记录
                                                mRecyclerView.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                "删除成功", Toast.LENGTH_SHORT).show();
                                                        if (deleteView != null) {
                                                            ViewGroup contentGroup = getWindow()
                                                                    .getDecorView().findViewById
                                                                            (android.R.id
                                                                                    .content);
                                                            contentGroup.removeView(deleteView);
                                                            deleteView = null;
                                                        }

                                                        mAdapter.setAllowLostFocus(true);
                                                        if (mAdapter.getItemCount() > 0 &&
                                                                selectPostion >= 1) {
                                                            StaggeredGridLayoutManager
                                                                    layoutManager =
                                                                    (StaggeredGridLayoutManager)
                                                                            mRecyclerView
                                                                                    .getLayoutManager();

                                                            View focusView = layoutManager
                                                                    .findViewByPosition
                                                                            (selectPostion - 1);

                                                            if (focusView != null) {
                                                                focusView.requestFocus();
                                                            }

                                                            mAdapter.removeItem(selectPostion);
                                                        } else {
                                                            requestData(tableName);
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    }).excute();

                        }

                        break;


                }


            }


        }
        return false;
    }
}
