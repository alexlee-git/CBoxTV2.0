package tv.newtv.cboxtv.cms.special;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.newtv.libs.Constant;
import com.newtv.libs.util.KeyEventUtils;
import com.newtv.libs.util.LogUploadUtils;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.special.util.ActivityUtils;
import tv.newtv.cboxtv.cms.special.util.Injection;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.player.PlayerConfig;

/**
 * 专题页
 */
public class SpecialActivity extends BaseActivity {
    private String mPageUUid;
    private String mActionType;
    private String mActionUri;

    private SpecialFragment mSpecialFragment;
    private SpecialPresenter mSpecialPresenter;

    @Override
    protected void onDestroy() {

        if(mSpecialFragment != null && mSpecialFragment.isAdded()){
            ActivityUtils.removeFragmentFromActivity(getSupportFragmentManager(),mSpecialFragment);
        }

        super.onDestroy();

        if (mSpecialPresenter != null) {
            mSpecialPresenter.destroy();
            mSpecialPresenter = null;
        }
        mSpecialFragment = null;

        PlayerConfig.getInstance().setTopicId(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special);

        mSpecialFragment = (SpecialFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (mSpecialFragment == null) {
            mSpecialFragment = SpecialFragment.newInstance();
            mSpecialFragment.setArguments(getIntent().getExtras());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mSpecialFragment, R.id.content);
        }

        mSpecialPresenter = new SpecialPresenter(Injection.provideTasksRepository
                (getApplicationContext()), mSpecialFragment);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }

        Log.e("SpecialActivity", "dispatchKeyEvent action=" + event.getAction() + " keycode=" + event.getKeyCode());

        if (KeyEventUtils.getEventAction(event)) {
            return super.dispatchKeyEvent(event);
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                finish();
                return true;
            }
            return true;
        }

        if (mSpecialFragment != null) {
            if (mSpecialFragment.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onStart() {
        super.onStart();
        uploadEnterLog();

        if (mSpecialFragment != null) {
            mSpecialFragment.setPageUUid(mPageUUid);
        }

        PlayerConfig.getInstance().setTopicId(mPageUUid);
    }

    private void uploadEnterLog() {
        Intent intent = getIntent();
        if (intent != null) {
            mPageUUid = intent.getStringExtra("page_uuid");
            mActionType = intent.getStringExtra("action_type");
            mActionUri = intent.getStringExtra("action_uri");

            StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            dataBuff.append("0,")
                    .append(mPageUUid + ",")
                    .append("")//专题模板
                    .trimToSize();

            LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL_PAGE, dataBuff.toString());
        }
    }

    private void uploadExitLog() {
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append("1,")
                .append(mPageUUid + ",")
                .append("")//专题模板
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL_PAGE, dataBuff.toString());
    }

    @Override
    protected void onStop() {
        uploadExitLog();
        super.onStop();
    }
}
