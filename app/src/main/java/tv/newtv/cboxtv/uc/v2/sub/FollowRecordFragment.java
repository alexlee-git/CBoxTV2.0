package tv.newtv.cboxtv.uc.v2.sub;

import android.annotation.SuppressLint;
import android.graphics.Rect;
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
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
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
        //关注数据表表名
        String TableNameAttention = DBConfig.ATTENTION_TABLE_NAME;
        if (!TextUtils.isEmpty(mLoginTokenString)) {
            TableNameAttention = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
        }
        DataSupport.search(TableNameAttention)
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
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                            }.getType();
                            List<UserCenterPageBean.Bean> universalBeans = gson.fromJson(result, type);
                            userCenterUniversalBean.data = universalBeans;

                            inflate(userCenterUniversalBean);
                        }
                    }
                })
                .excute();
    }

    private void inflate(UserCenterPageBean bean) {
        if (contentView == null) {
            return;
        }
        if (bean == null || bean.data == null || bean.data.size() == 0) {
            inflatePageWhenNoData();
            return;
        }

        if (mDatas == null) {
            mDatas = bean.data;
            mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));
            mAdapter = new UserCenterUniversalAdapter(getActivity(), mDatas, Constant.UC_FOLLOW);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
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
                mDatas.addAll(bean.data);
                mAdapter.notifyDataSetChanged();
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
        //关注页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,4");
        //获取用户登录状态
        requestUserInfo();
    }

}
