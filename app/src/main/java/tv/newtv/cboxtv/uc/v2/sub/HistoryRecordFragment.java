package tv.newtv.cboxtv.uc.v2.sub;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.RxBus;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.BaseDetailSubFragment;

/**
 * 项目名称:         熊猫ROM-launcher应用
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午1:56
 * 创建人:           lixin
 * 创建日期:         2018/9/6
 */


public class HistoryRecordFragment extends BaseDetailSubFragment {
    private RecyclerView mRecyclerView;
    private List<UserCenterPageBean.Bean> mDatas;

    private TextView emptyTextView;

    private static final int MSG_INFLATE_CONTENT = 10001;

    private int move =-1;
    private Observable<Integer> observable;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_record;
    }

    @Override
    protected void init() {
        super.init();

        observable = RxBus.get().register("recordPosition");
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        move = integer;
                    }
                });

    }

    private void requestData() {

    }

    @Override
    protected void updateUiWidgets(View view) {
        DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build().withCallback(new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    UserCenterPageBean userCenterUniversalBean = new UserCenterPageBean("");
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<UserCenterUniversalBean.Bean>>(){}.getType();
                    List<UserCenterPageBean.Bean> universalBeans = gson.fromJson(result, type);
                    userCenterUniversalBean.data = universalBeans;
                }
            }
        }).excute();
    }

    public void inflateContent(UserCenterPageBean userCenterUniversalBean) {
        if (userCenterUniversalBean == null) {
            return;
        }


        mDatas = userCenterUniversalBean.data;

        if (mDatas == null || mDatas.size() == 0) {
            showHotTip();
            return;
        }


        mRecyclerView = contentView.findViewById(R.id.id_history_record_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        UserCenterUniversalAdapter universalAdapter = new UserCenterUniversalAdapter(getActivity(), mDatas, Constant.UC_HISTORY);
        mRecyclerView.setAdapter(universalAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 72;
                outRect.right = 0;
            }
        });
    }

    private void showHotTip() {
        View emptyView = contentView.findViewById(R.id.empty_container);
        if (emptyView != null) {
            if (emptyTextView == null) {
                emptyTextView = emptyView.findViewById(R.id.empty_textview);
                emptyTextView.setText("您好");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister("recordPosition",observable);
    }
}