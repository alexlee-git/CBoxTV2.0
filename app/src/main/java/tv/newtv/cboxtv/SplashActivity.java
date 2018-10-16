package tv.newtv.cboxtv;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.player.ActivityStacks;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         11:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/3
 */
public class SplashActivity extends RxFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mIntent = getIntent();
        boolean isBackground = ActivityStacks.get().isBackGround();

        if (mIntent != null) {
            String mExternalAction = mIntent.getStringExtra("action");
            String mExternalParams = mIntent.getStringExtra("params");
            if (mExternalAction != null && !isBackground) {
                ActivityStacks.get().finishAllActivity();

                if (Constant.EXTERNAL_OPEN_NEWS.equals(mExternalAction) || Constant
                        .EXTERNAL_OPEN_PANEL.equals(mExternalAction)) {
                    mIntent.setClass(this, EntryActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    boolean jump = JumpUtil.parseExternalJump(getApplicationContext(),
                            mExternalAction,
                            mExternalParams);
                    // add log
                    LogUploadUtils.uploadEnterAppLog(this.getApplicationContext());
                    // end
                    if (!jump) {
                        mIntent.setClass(this, EntryActivity.class);
                        startActivity(mIntent);
                    }
                    finish();
                }
            } else {
                mIntent.setClass(this, EntryActivity.class);
                startActivity(mIntent);
                finish();
            }
        }else{
            Intent intent = new Intent(this,EntryActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
