package tv.newtv.cboxtv.uc.v2;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private static final String TAG = "AttentionDetailActivity";

    @Override
    protected String[] getTabList() {
        return new String[]{};
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
        hideView(operationIcon);
        showView(operationText);
        if (operationText != null) {
            operationText.setText(R.string.usercenter_attention_operation_text);
        }

        Class<? extends Fragment> clz = FollowRecordFragment.class;
        if (clz != null) {
            ShowFragment(clz, R.id.content_container);
        } else {
            Toast.makeText(this, "NotSupported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String getEmptyTipMessage() {
        return "您还没有关注人物哦~";
    }

    @Override
    protected void onTabChange(String title, View view) {
        Class<? extends Fragment> clz = FollowRecordFragment.class;
        if (clz != null) {
            ShowFragment(clz, R.id.content_container);
        } else {
            Toast.makeText(this, "NotSupported!", Toast.LENGTH_SHORT).show();
        }
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
    private void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
