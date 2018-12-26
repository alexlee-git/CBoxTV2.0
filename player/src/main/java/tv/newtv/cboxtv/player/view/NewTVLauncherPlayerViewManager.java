package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Libs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.listener.ScreenListener;
import tv.newtv.cboxtv.player.model.LiveInfo;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerViewManager {

    private static final String TAG = NewTVLauncherPlayerViewManager.class.getName();
    private static NewTVLauncherPlayerViewManager mNewTVLauncherPlayerViewManager;
    private NewTVLauncherPlayerView mNewTVLauncherPlayerView;
    private Context mPlayerPageContext; //播放activity实例

    private int typeIndex = -1;

    private long mCurrentPlayerID = 0;

    private List<ScreenListener> pendingListener = new ArrayList<>();

    private NewTVLauncherPlayerViewManager() {
    }

    public static NewTVLauncherPlayerViewManager getInstance() {
        if (mNewTVLauncherPlayerViewManager == null) {
            synchronized (NewTVLauncherPlayerViewManager.class) {
                if (mNewTVLauncherPlayerViewManager == null) {
                    mNewTVLauncherPlayerViewManager = new NewTVLauncherPlayerViewManager();
                }
            }
        }
        return mNewTVLauncherPlayerViewManager;
    }

    public NewTVLauncherPlayerView.PlayerViewConfig getDefaultConfig() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getDefaultConfig();
        }
        return null;
    }

    public int getPlayPostion() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getCurrentPosition();
        }
        return 0;
    }

    public void stop() {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.stop();
        }
    }

    public boolean isFullScreen() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.isFullScreen();
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.dispatchKeyEvent(event);
        }
        return false;
    }

    void setPlayerView(NewTVLauncherPlayerView playerView) {
        if (mNewTVLauncherPlayerView != null && mNewTVLauncherPlayerView != playerView) {
            release();
        }
        mCurrentPlayerID = System.currentTimeMillis();
        mNewTVLauncherPlayerView = playerView;
        setPendingListener();
    }

    public void init(Context context) {
        if (mNewTVLauncherPlayerView == null) {
            mNewTVLauncherPlayerView = new NewTVLauncherPlayerView(context);
            setPendingListener();
        }
    }

    public void setPlayerViewContainer(FrameLayout frameLayout, Context context,boolean
            fromFullScreen) {
        if (mNewTVLauncherPlayerView != null) {
            ViewGroup viewGroup = (ViewGroup) mNewTVLauncherPlayerView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mNewTVLauncherPlayerView);
            }
        } else {
            mNewTVLauncherPlayerViewManager.init(context);
        }
        if(fromFullScreen) {
            mNewTVLauncherPlayerView.setFromFullScreen();
            mNewTVLauncherPlayerView.updateUIPropertys(true);
        }
        frameLayout.addView(mNewTVLauncherPlayerView, -1);

        mPlayerPageContext = context;
    }

    public void playVod(Context context, Content content, int index, int position) {
        if (mNewTVLauncherPlayerViewManager != null) {
            mNewTVLauncherPlayerViewManager.init(context);
        }
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.setSeriesInfo(content);
            mNewTVLauncherPlayerView.playSingleOrSeries(index, position);
        } else {
            Log.e(TAG, "playVod: mNewTVLauncherPlayerView==null");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: ");
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.onKeyDown(keyCode, event);
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp: ");
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.onKeyUp(keyCode, event);
        }
        return false;
    }

    public void setShowingView(int showingView) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.setShowingView(showingView);
        }
    }

    public int getShowView() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getShowingView();
        }
        return NewTVLauncherPlayerView.SHOWING_NO_VIEW;
    }

    public boolean equalsPlayer(NewTVLauncherPlayerView playerView) {
        return mNewTVLauncherPlayerView == playerView;
    }

    public void releasePlayer() {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.release();
            mNewTVLauncherPlayerView.destroy();
            if (mNewTVLauncherPlayerView.getParent() != null && mNewTVLauncherPlayerView.getParent()
                    instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) mNewTVLauncherPlayerView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(mNewTVLauncherPlayerView);
                }
            }
            mNewTVLauncherPlayerView = null;
        }
    }

    public void playLive(LiveInfo liveInfo, LiveListener listener) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.playLive(liveInfo, false, listener);
        }
    }

    public void release() {
        Log.i(TAG, "release: ");
        releasePlayer();

        closePlayerActivity();
        mPlayerPageContext = null;
        PlayerConfig.getInstance().cleanColumnId();
    }

    public void setVideoSilent(boolean isSilent) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.setVideoSilent(isSilent);
        }
    }

    @SuppressWarnings("PointlessNullCheck")
    private void closePlayerActivity() {
        Log.i(TAG, "closePlayerActivity: ");
        if (mPlayerPageContext != null && mPlayerPageContext instanceof Activity) {
            ((Activity) mPlayerPageContext).finish();
        }
    }

    public int getCurrentPosition() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getCurrentPosition();
        }
        return -1;
    }

    public int getIndex() {
        Log.i(TAG, "getIndex: ");
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getIndex();
        }
        return -1;
    }

    @Nullable
    public Content getProgramSeriesInfo() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.defaultConfig.programSeriesInfo;
        }
        return null;
    }

    public LiveInfo getLiveInfo(){
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.defaultConfig.liveInfo;
        }
        return null;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void addListener(IPlayProgramsCallBackEvent l) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.addListener(l);
        }
    }

    public boolean isLiving() {
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.defaultConfig.isLiving;
        }
        return false;
    }

    public void changeAlternate(String contentId, String channel, String title) {

        if (mNewTVLauncherPlayerView == null) {
            mNewTVLauncherPlayerViewManager.init(Libs.get().getContext());
        }

        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.changeAlternate(contentId, title, channel);
        }
    }

    public boolean isADPlaying() {
        return mNewTVLauncherPlayerView != null && mNewTVLauncherPlayerView.isADPlaying();
    }

    public boolean registerScreenListener(ScreenListener listener) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.registerScreenListener(listener);
        } else {
            pendingListener.add(listener);
        }
        return true;
    }

    public void unregisterScreenListener(ScreenListener listener) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.unregisterScreenListener(listener);
        } else {
            pendingListener.remove(listener);
        }
    }

    private void setPendingListener() {
        if (mNewTVLauncherPlayerView != null) {
            for (ScreenListener listener : pendingListener) {
                mNewTVLauncherPlayerView.registerScreenListener(listener);
            }
            pendingListener.clear();
        }
    }

}
