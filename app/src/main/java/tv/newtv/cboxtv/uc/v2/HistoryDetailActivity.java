package tv.newtv.cboxtv.uc.v2;

import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.sub.HistoryRecordFragment;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
public class HistoryDetailActivity extends BaseUCDetailActivity<UserCenterPageBean.Bean>  {

    private static final String TAG = "HistoryDetailActivity";

    private String[] tabArray = new String[]{};


    @Override
    protected String[] getTabList() {
        return tabArray;
    }

    @Override
    protected String getHeadTitle() {
        return "观看记录";
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_history_detail;
    }

    @Override
    protected void updateWidgets() {
        ShowFragment(HistoryRecordFragment.class, R.id.content_container);
    }

    @Override
    protected String getEmptyTipMessage() {
        return "您还没有看过任何节目哦~";
    }

    @Override
    protected void onTabChange(String title, View view) {}

    @Override
    protected int getDetailType() {
        return DETAIL_TYPE_HISTORY;
    }


    @Override
    public void onResult(@NotNull List<UserCenterPageBean.Bean> results) {
        super.onResult(results);
        Log.d(TAG, results.toString());

        if (results == null || results.size() == 0) {
            hideView(operationIcon);
            hideView(operationText);
        } else {
            showView(operationIcon);
            showView(operationText);

            if (operationText != null) {
                operationText.setText(R.string.usercenter_history_operation_text);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideView(operationIcon);
        hideView(operationText);
    }

    private void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
