package tv.newtv.cboxtv.player;

import android.app.Activity;
import android.content.Intent;

import com.newtv.cms.bean.Content;

import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:00
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class Player implements PlayerObserver {

    private static Player instance = null;
    private PlayerObserver mObserver;


    private Player() {

    }

    public static Player get() {
        if (instance == null) {
            synchronized (Player.class) {
                if (instance == null) instance = new Player();
            }
        }
        return instance;
    }

    public boolean isFullScreen() {
        if (mObserver.getCurrentActivity() != null && mObserver.getCurrentActivity() instanceof
                IPlayerActivity) {
            return ((IPlayerActivity) mObserver.getCurrentActivity()).isFullScreenActivity() ||
                    NewTVLauncherPlayerViewManager
                            .getInstance().isFullScreen();
        }
        return false;
    }

    public void attachObserver(PlayerObserver observer) {
        this.mObserver = observer;
    }

    @Override
    public void onFinish(Content playInfo, int index, int position) {

        mObserver.onFinish(playInfo, index, position);
    }

    @Override
    public void onExitApp() {
        mObserver.onExitApp();
    }

    @Override
    public Activity getCurrentActivity() {
        return mObserver.getCurrentActivity();
    }

    @Override
    public Intent getPlayerActivityIntent() {
        return mObserver.getPlayerActivityIntent();
    }

    @Override
    public boolean isVip() {
        return mObserver.isVip();
    }
}
