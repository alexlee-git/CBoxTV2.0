package tv.newtv.cboxtv.uc.v2.aboutmine;

import android.annotation.SuppressLint;
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
import tv.newtv.cboxtv.uc.v2.BaseUCDetailActivity;
import tv.newtv.cboxtv.uc.v2.DetailCallback;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2.about
 * 类描述：关于我的页面二期
 * 创建人：weihaichao
 * 创建时间：16:30
 * 创建日期：2018/8/27
 * 修改人：wqs
 * 修改时间：10:57
 * 修改日期：2018/9/4
 * 修改备注：包含关于我们，使用帮助
 */
public class AboutMineV2Activity extends BaseUCDetailActivity<UserCenterPageBean.Bean>
        implements DetailCallback<UserCenterPageBean.Bean> {

    private static final String TAG = "AboutMineV2Activity";
    private final String aboutUs = "关于我们";
    private final String aboutHelp = "使用帮助";

    @Override
    protected boolean getUsePresenter() {
        return false;
    }

    @Override
    protected String[] getTabList() {
        return new String[]{aboutUs, aboutHelp};
    }

    @Override
    protected String getHeadTitle() {
        return "";
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_usercenter_about_mine_v2;
    }

    @Override
    protected void updateWidgets() {
        //关于页面上报日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER, "9");
    }

    @Override
    protected String getEmptyTipMessage() {
        return "";
    }

    @Override
    protected void onTabChange(String title, View view) {
        Class<? extends Fragment> clz = null;
        if (title.equals(aboutUs)) {
            clz = AboutUsFragment.class;
        } else if (title.equals(aboutHelp)) {
            clz = AboutHelpFragment.class;
        }
        if (clz != null) {
            ShowFragment(clz, R.id.content_container);
        } else {
            Toast.makeText(this, "NotSupported!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected int getDetailType() {
        return DETAIL_TYPE_MINE;
    }

    @Override
    public void onResult(@NotNull List<UserCenterPageBean.Bean> results) {
        super.onResult(results);
        Log.d(TAG, results.toString());
    }
}
