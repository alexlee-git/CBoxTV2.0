package tv.newtv.cboxtv.uc.v2;

import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.sub.FollowRecordFragment;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
public class AttentionDetailActivity extends BaseUCDetailActivity<UserCenterPageBean.Bean>
        implements DetailCallback<UserCenterPageBean.Bean> {

    private static final String TAG = "lx";

    @Override
    protected String[] getTabList() {
        return null;
    }

    @Override
    protected String getHeadTitle() {
        return "关注的名人";
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_history_detail;
    }

    @Override
    protected void updateWidgets() {
        ShowFragment(FollowRecordFragment.class, R.id.content_container);
        hideView(operationIcon);
        hideView(operationText);
    }

    @Override
    protected String getEmptyTipMessage() {
        return "您还没有关注人物哦~";
    }

    @Override
    protected void onTabChange(String title, View view) {

    }

    @Override
    protected int getDetailType() {
        return DETAIL_TYPE_ATTENTION;
    }

    @Override
    public void onResult(@NotNull List<UserCenterPageBean.Bean> results) {
        super.onResult(results);
        Log.d(TAG, results.toString());
    }

    private void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
