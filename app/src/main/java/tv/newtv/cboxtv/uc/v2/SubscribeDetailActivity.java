package tv.newtv.cboxtv.uc.v2;

import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.sub.HistoryRecordFragment;
import tv.newtv.cboxtv.uc.v2.sub.SubscribeFragment;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
public class SubscribeDetailActivity extends BaseUCDetailActivity<UserCenterPageBean.Bean>
        implements DetailCallback<UserCenterPageBean.Bean> {

    private static final String TAG = "SubscribeDetailActivity";

    @Override
    protected String[] getTabList() {
        return null;
    }

    @Override
    protected String getHeadTitle() {
        return "订阅的电视栏目";
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_history_detail;
    }

    @Override
    protected void updateWidgets() {
        ShowFragment(SubscribeFragment.class, R.id.content_container);

        hideView(operationIcon);
        showView(operationText);
        if (operationText != null) {
            operationText.setText(R.string.usercenter_subscription_operation_text);
        }
    }

    @Override
    protected String getEmptyTipMessage() {
        return "您还没有订阅任何节目哦~";
    }

    @Override
    protected void onTabChange(String title, View view) {
    }

    @Override
    protected int getDetailType() {
        return DETAIL_TYPE_SUBSCRIBE;
    }

    @Override
    public void onResult(@NotNull List<UserCenterPageBean.Bean> results) {
        super.onResult(results);
//        if (results == null || results.size() == 0) {
//            hideView(operationText);
//        } else {
//            showView(operationText);
//            if (operationText != null) {
//                operationText.setText(R.string.usercenter_subscription_operation_text);
//            }
//        }
//
//        hideView(operationIcon);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideView(operationText);
        hideView(operationIcon);
    }
}
