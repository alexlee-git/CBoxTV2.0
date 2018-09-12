package tv.newtv.cboxtv;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import tv.newtv.ActivityStacks;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.utils.KeyEventUtils;
import tv.newtv.cboxtv.utils.Utils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         18:22
 * 创建人:           weihaichao
 * 创建日期:          2018/5/7
 */
public abstract class BaseActivity extends RxFragmentActivity {

    protected boolean FrontStage = false;//是否已经进入前台
    private boolean fromOuter = false;//是否是外部跳转进入的

    public boolean isFrontStage() {
        return FrontStage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStacks.get().onCreate(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constant.ACTION_FROM)) {
                fromOuter = intent.getBooleanExtra(Constant.ACTION_FROM, false);
            }
        }
    }

    public boolean hasPlayer() {
        return false;
    }

    public void prepareMediaPlayer() {

    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (hasPlayer()) {
            prepareMediaPlayer();
        }
        super.onResume();
        FrontStage = true;

        if (BuildConfig.FLAVOR.equals(DeviceUtil.XIONG_MAO)) {
            NewTVLauncherPlayerViewManager.getInstance().setVideoSilent(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStacks.get().onDestroy(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStacks.get().onPause(this);

        if (BuildConfig.FLAVOR.equals(DeviceUtil.XIONG_MAO)) {
            if (Utils.isTopActivityIsAiassist()) {
                NewTVLauncherPlayerViewManager.getInstance().setVideoSilent(true);
            }
        }
    }

    public boolean isFullScreen() {
        return NewTVLauncherPlayerViewManager.getInstance().isFullScreen();
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
        if(event.getAction() == KeyEvent.ACTION_UP) {
            if (isBackPressed(event)) {
                if (fromOuter) {
                    startActivityForResult(new Intent().setClass(this, WarningExitActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            , 0x999);
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public boolean isBackPressed(KeyEvent event) {
        return (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE)
                || (!BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getKeyCode() == KeyEvent.KEYCODE_BACK);
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
        return isFullScreen();
    }

}
