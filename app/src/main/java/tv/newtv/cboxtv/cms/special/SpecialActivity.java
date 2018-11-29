package tv.newtv.cboxtv.cms.special;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.KeyEventUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
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
public class SpecialActivity extends BaseActivity implements SpecialContract.ModelResultView ,Target {
    private String mPageUUid;
    private String mActionType;
    private String mActionUri;
    private boolean isADEntry = false;

    private BaseSpecialContentFragment mSpecialFragment;
    private SpecialContract.Presenter mSpecialPresenter;
    private AdContract.AdPresenter mAdPresenter;
    private String templateZT="";

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
//
//        if (mSpecialFragment == null) {
//            mSpecialFragment = SpecialFragment.newInstance();
//            mSpecialFragment.setArguments(getIntent().getExtras());
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
//                    mSpecialFragment, R.id.content);
//>>>>>>> 1.4
//        }
        isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY,false);
        String contentUUID = getIntent().getStringExtra(Constant.CONTENT_UUID);
        if (TextUtils.isEmpty(contentUUID)) {
            ToastUtil.showToast(getApplicationContext(), "UUID为空");
            finish();
            return;
        }

        mSpecialPresenter = new SpecialContract.SpecialPresenter(getApplicationContext(), this);
        mAdPresenter = new AdContract.AdPresenter(getApplicationContext(),null);
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

        if (isBackPressed(event)) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
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

        PlayerConfig.getInstance().setTopicId(mPageUUid);
    }

    private void uploadEnterLog() {
        Intent intent = getIntent();
        if (intent != null) {
            mPageUUid = intent.getStringExtra("page_uuid");

            StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            dataBuff.append("0,")
                    .append(mPageUUid + ",")
                    .append(templateZT)//专题模板
                    .trimToSize();
            Log.e("SpecialActivity", dataBuff.toString());

            LogUploadUtils.uploadLog(Constant.LOG_NODE_SPECIAL_PAGE, dataBuff.toString());
        }
    }

    private void uploadExitLog() {
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append("1,")
                .append(mPageUUid + ",")
                .append(templateZT)//专题模板
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
        templateZT = modelResult.getTemplateZT();
        uploadEnterLog();


        initBackground(modelResult);
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


    private void initBackground(final ModelResult modelResult) {
        if (ModelResult.IS_AD_TYPE.equals(modelResult.isAd())) {
            mAdPresenter.getAdByType(Constant.AD_TOPIC, mPageUUid, "", null, new AdContract
                    .Callback() {
                @Override
                public void showAd(@org.jetbrains.annotations.Nullable String type, @org
                        .jetbrains.annotations.Nullable String url, @org.jetbrains.annotations
                        .Nullable HashMap<?, ?> hashMap) {
                    if (TextUtils.isEmpty(url)) {
                        showPosterByCMS(modelResult);
                    } else {
                        if(url.startsWith("file:")) {
                            Picasso.get().load(Uri.parse(url)).into(SpecialActivity.this);
                        }else if(url.startsWith("http://") || url.startsWith("https://")){
                            Picasso.get().load(url).into(SpecialActivity.this);
                        }
                    }
                }
            });

        } else {
            showPosterByCMS(modelResult);
        }
    }

    private void showPosterByCMS(ModelResult moduleInfoResult) {
        if (!TextUtils.isEmpty(moduleInfoResult.getBackground())) {
            Picasso.get().load(moduleInfoResult.getBackground()).into(this);
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        findViewById(R.id.content).setBackground(new BitmapDrawable(bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
