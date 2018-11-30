package tv.newtv.cboxtv;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.KeyEventUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.newtv.cboxtv.annotation.PopupAD;
import tv.newtv.cboxtv.cms.details.ColumnPageActivity;
import tv.newtv.cboxtv.cms.details.ProgramCollectionActivity;
import tv.newtv.cboxtv.cms.details.ProgrameSeriesAndVarietyDetailActivity;
import tv.newtv.cboxtv.cms.details.SingleDetailPageActivity;
import tv.newtv.cboxtv.player.IPlayerActivity;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.views.AdPopupWindow;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         18:22
 * 创建人:           weihaichao
 * 创建日期:          2018/5/7
 */
public abstract class BaseActivity extends RxFragmentActivity implements IPlayerActivity {

    protected boolean isADEntry = false;//是否点击广告位跳转过来的
    protected boolean FrontStage = false;//是否已经进入前台
    protected boolean fromOuter = false;//是否是外部跳转进入的
    protected boolean isPopup = false;
    private AdContract.Presenter adPresenter;
    private AdPopupWindow adPopupWindow;
    private List<ILifeCycle> lifeCycleList;

    public void lifeCycle(ILifeCycle lifeCycle) {
        lifeCycleList.add(lifeCycle);
    }

    protected boolean isDetail() {
        return false;
    }

    public boolean isFrontStage() {
        return FrontStage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifeCycleList = new ArrayList<>();
        ActivityStacks.get().onCreate(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constant.ACTION_FROM)) {
                fromOuter = intent.getBooleanExtra(Constant.ACTION_FROM, false);
                if (fromOuter) {
                    try {
                        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
                        PackageInfo pckInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        dataBuff.append("0,")
                                .append(pckInfo.versionName)
                                .trimToSize();
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, dataBuff.toString());//进入应用
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (intent.hasExtra(Constant.ACTION_AD_ENTRY)) {
                isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY, false);
            }
        }
    }

    public boolean hasPlayer() {
        return false;
    }

    @Override
    public boolean isFullScreenActivity() {
        return false;
    }

    public void prepareMediaPlayer() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        for (ILifeCycle lifeCycle : lifeCycleList) {
            lifeCycle.onActivityStop();
        }
        ActivityStacks.get().onStop(this);
        FrontStage = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityStacks.get().onStart(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        ActivityStacks.get().onResume(this);
        for (ILifeCycle lifeCycle : lifeCycleList) {
            lifeCycle.onActivityResume();
        }
        if (hasPlayer()) {
            prepareMediaPlayer();
        }
        super.onResume();
        FrontStage = true;

        if (Libs.get().getFlavor().equals(DeviceUtil.XIONG_MAO)
                || Libs.get().getFlavor().equals(DeviceUtil.XUN_MA)) {
            NewTVLauncherPlayerViewManager.getInstance().setVideoSilent(false);
        }

//        setBackgroundAD();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setPopupAD();
    }

    private void setPopupAD() {
        if (isDetail() || hasPopoupAD()) {
            adPopupWindow = new AdPopupWindow();
            adPopupWindow.show(this, findViewById(android.R.id.content));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStacks.get().onDestroy(this);
        for (ILifeCycle lifeCycle : lifeCycleList) {
            lifeCycle.onActivityDestroy();
        }
        if (adPresenter != null) {
            adPresenter.destroy();
        }
        if (adPopupWindow != null && adPopupWindow.isShowing()) {
            adPopupWindow.dismiss();
        }


        lifeCycleList.clear();
        lifeCycleList = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStacks.get().onPause(this);
        for (ILifeCycle lifeCycle : lifeCycleList) {
            lifeCycle.onActivityPause();
        }

        if (Libs.get().getFlavor().equals(DeviceUtil.XIONG_MAO)
                || Libs.get().getFlavor().equals(DeviceUtil.XUN_MA)) {
            if (Utils.isTopActivityIsAiassist()) {
                NewTVLauncherPlayerViewManager.getInstance().setVideoSilent(true);
            }
        }
    }

    public boolean isFullScreen() {
        return isFullScreenActivity() || NewTVLauncherPlayerViewManager.getInstance()
                .isFullScreen();
    }

    protected void checkIsTop(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent
                .KEYCODE_DPAD_UP) {
            View rootView = getWindow().getDecorView();
            if (rootView instanceof ViewGroup) {
                View focusView = rootView.findFocus();
                View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) rootView,
                        focusView, View
                                .FOCUS_UP);
                if (isDetail() && nextFocus == null) {
                    NavPopuView navPopuView = new NavPopuView();
                    navPopuView.showPopup(this, rootView);
                }
            }
        }
    }

    protected boolean processKeyEvent(KeyEvent keyEvent){
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!FrontStage) return true;
        if (isFullScreen()) {
            if (NewTVLauncherPlayerViewManager.getInstance().dispatchKeyEvent(event)) {
                return true;
            }
            return false;
        }

        if (isDetail() && event.getAction() == KeyEvent.ACTION_UP) {
            if (isBackPressed(event)) {
                if (fromOuter) {
                    startActivityForResult(new Intent().setClass(this, WarningExitActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 0x999);
                    return true;
                }
                if (isADEntry) {
                    ActivityStacks.get().finishAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(LauncherApplication.AppContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LauncherApplication.AppContext.getApplicationContext().startActivity(intent);
                    isADEntry = false;
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public boolean isBackPressed(KeyEvent event) {
        return (Libs.get().getFlavor().equals(DeviceUtil.XUN_MA) && event.getKeyCode() ==
                KeyEvent.KEYCODE_ESCAPE)
                || (!Libs.get().getFlavor().equals(DeviceUtil.XUN_MA) && event.getKeyCode() ==
                KeyEvent.KEYCODE_BACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x999 && resultCode == RESULT_OK) {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, "1");//退出应用
            ActivityStacks.get().finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isFullScreen() && KeyEventUtils.FullScreenAllowKey(event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 拦截按键事件，统一处理
     *
     * @return
     */
    protected boolean interruptKeyEvent(KeyEvent event) {
        if (fromOuter && isBackPressed(event)) {
            return true;
        }
        boolean isFullScreen = isFullScreen();
        if (isPopup && fromOuter) {
            checkIsTop(event);
        }
        return isFullScreen;
    }

    protected void setBackgroundAD() {
        if (isDetailActivity()) {
            adPresenter = new AdContract.AdPresenter(getApplicationContext(), null);
            adPresenter.getAdByChannel(Constant.AD_DESK, Constant.AD_DETAILPAGE_BACKGROUND, "",
                    PlayerConfig.getInstance().getFirstChannelId(), PlayerConfig.getInstance()
                            .getSecondChannelId(), PlayerConfig.getInstance().getTopicId(), null,
                    new AdContract.Callback() {
                        @Override
                        public void showAd(@org.jetbrains.annotations.Nullable String type, @org
                                .jetbrains.annotations.Nullable String url, @org.jetbrains
                                .annotations.Nullable HashMap<?, ?> hashMap) {
                            if (!TextUtils.isEmpty(url)) {
                                Picasso.get().load(url).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom
                                            from) {
                                        getWindow().setBackgroundDrawable(new BitmapDrawable
                                                (bitmap));
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable
                                            errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }
                        }
                    });
        }
    }

    private boolean isDetailActivity() {
        Class<? extends BaseActivity> clazz = getClass();
        if (clazz == ProgrameSeriesAndVarietyDetailActivity.class
                || clazz == ColumnPageActivity.class
                || clazz == SingleDetailPageActivity.class
                || clazz == ProgramCollectionActivity.class) {
            return true;
        }
        return false;
    }

    private boolean hasPopoupAD() {
        return hasAnnotation(PopupAD.class);
    }

    private boolean hasAnnotation(Class ann) {
        Class<? extends BaseActivity> clazz = getClass();
        Annotation annotation = clazz.getAnnotation(ann);
        if (annotation != null) {
            return true;
        }
        return false;
    }

}
