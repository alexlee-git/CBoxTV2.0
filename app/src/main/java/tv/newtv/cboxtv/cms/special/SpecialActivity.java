package tv.newtv.cboxtv.cms.special;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.libs.Constant;
import com.newtv.libs.util.KeyEventUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.special.fragment.BaseSpecialContentFragment;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.cms.special.util.ActivityUtils;
import tv.newtv.cboxtv.player.PlayerConfig;

/**
 * 专题页
 */
@BuyGoodsAD
public class SpecialActivity extends BaseActivity implements SpecialContract.ModelResultView {
    private String mPageUUid;
    private String mActionType;
    private String mActionUri;
    private boolean isADEntry = false;

    private BaseSpecialContentFragment mSpecialFragment;
    private SpecialContract.Presenter mSpecialPresenter;

    @Override
    protected void onDestroy() {

        if (mSpecialFragment != null && mSpecialFragment.isAdded()) {
            ActivityUtils.removeFragmentFromActivity(getSupportFragmentManager(), mSpecialFragment);
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
//
//<<<<<<< HEAD
//        if (!getIntent().hasExtra(Constant.CONTENT_UUID)) {
//            ToastUtil.showToast(getApplicationContext(), "UUID为空");
//            finish();
//            return;
//=======
//        mSpecialFragment = (SpecialFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.content);
//        isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY,false);
//        if (mSpecialFragment == null) {
//            mSpecialFragment = SpecialFragment.newInstance();
//            mSpecialFragment.setArguments(getIntent().getExtras());
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
//                    mSpecialFragment, R.id.content);
//>>>>>>> 1.4
//        }

        String contentUUID = getIntent().getStringExtra(Constant.CONTENT_UUID);
        if (TextUtils.isEmpty(contentUUID)) {
            ToastUtil.showToast(getApplicationContext(), "UUID为空");
            finish();
            return;
        }

        mSpecialPresenter = new SpecialContract.SpecialPresenter(getApplicationContext(), this);
        ((SpecialContract.SpecialPresenter) mSpecialPresenter).getPageData(BuildConfig.APP_KEY,
                BuildConfig.CHANNEL_ID,contentUUID);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }

        Log.e("SpecialActivity", "dispatchKeyEvent action=" + event.getAction() + " keycode=" +
                event.getKeyCode());

        if (KeyEventUtils.getEventAction(event)) {
            return super.dispatchKeyEvent(event);
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(isADEntry){
                    startActivity(new Intent(SpecialActivity.this, MainActivity.class));
                    isADEntry = false;
                }
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

        PlayerConfig.getInstance().setTopicId(mPageUUid);
    }

    private void uploadEnterLog() {
        Intent intent = getIntent();
        if (intent != null) {
            mPageUUid = intent.getStringExtra("page_uuid");

            StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            dataBuff.append("0,")
                    .append(mPageUUid + ",")
                    .append("")//专题模板
                    .trimToSize();
            Log.e("SpecialActivity", dataBuff.toString());

//            LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL_PAGE, dataBuff.toString());
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

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showPageContent(ModelResult<ArrayList<Page>> modelResult) {
        mSpecialFragment = SpecialLayoutManager.get().GenerateFragment(R.id.content, getIntent()
                        .getExtras(),
                getSupportFragmentManager(), modelResult);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
