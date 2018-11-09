package tv.newtv.cboxtv.uc.v2;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.sub.CollectionProgramSetFragment;
import tv.newtv.cboxtv.uc.v2.sub.CollectionTopicFragment;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
public class CollectionDetailActivity extends BaseUCDetailActivity<UserCenterPageBean.Bean>
        implements DetailCallback<UserCenterPageBean.Bean> {

    private static final String TAG = "user2nd";
    private final String TAB_JIE_MU_JI = "节目集";
    private final String TAB_ZHUAN_TI = "专题";

    @Override
    protected String[] getTabList() {
        return new String[]{/*"节目集", "专题"*/};
    }

    @Override
    protected String getHeadTitle() {
        return "我的收藏";
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
            operationText.setText(R.string.usercenter_collection_operation_text);
        }

        Class<? extends Fragment> clz = CollectionProgramSetFragment.class;
        if (clz != null) {
            ShowFragment(clz, R.id.content_container);
        } else {
            Toast.makeText(this, "NotSupported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String getEmptyTipMessage() {
        return "您还没有收藏任何节目哦~";
    }

    @Override
    protected void onTabChange(String title, View view) {
        // 切换tab

        Class<? extends Fragment> clz = null;
        if (title.equals(TAB_JIE_MU_JI)) {
            clz = CollectionProgramSetFragment.class;
        } else if (title.equals(TAB_ZHUAN_TI)) {
            clz = CollectionTopicFragment.class;
        }

        if (clz != null) {
            ShowFragment(clz, R.id.content_container);
        } else {
            Toast.makeText(this, "NotSupported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getDetailType() {
        return DETAIL_TYPE_COLLECTION;
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

    @Override
    protected void onResume() {
        Log.e(TAG, "---onResume");
        //收藏页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "3,0");
        super.onResume();
    }
}
