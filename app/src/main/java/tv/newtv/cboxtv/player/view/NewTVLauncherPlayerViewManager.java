package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.listener.ScreenListener;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerViewManager {

    private static final String TAG = NewTVLauncherPlayerViewManager.class.getName();
    private static NewTVLauncherPlayerViewManager mNewTVLauncherPlayerViewManager;
    private NewTVLauncherPlayerView mNewTVLauncherPlayerView;
    private Context mPlayerPageContext; //播放activity实例

    private ProgramSeriesInfo programSeriesInfo;
    private int typeIndex = -1;

    private long currentPlayer = 0;

    private boolean isLive = false;// 是否是直播？
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

    public long setPlayerView(NewTVLauncherPlayerView playerView) {

        if (mNewTVLauncherPlayerView != null && mNewTVLauncherPlayerView != playerView) {
            mNewTVLauncherPlayerView.buildPlayerViewConfig();
            release();
        }

        currentPlayer = System.currentTimeMillis();
        mNewTVLauncherPlayerView = playerView;
        setPendingListener();
        return currentPlayer;
    }

    public void init(Context context) {
        if (mNewTVLauncherPlayerView == null) {
            mNewTVLauncherPlayerView = new NewTVLauncherPlayerView(null, context);
            setPendingListener();
        }
    }

    public void setPlayerViewContainer(FrameLayout frameLayout, Context context) {
        if (mNewTVLauncherPlayerView != null) {
            ViewGroup viewGroup = (ViewGroup) mNewTVLauncherPlayerView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mNewTVLauncherPlayerView);
            }
            mNewTVLauncherPlayerView.setFromFullScreen();
            mNewTVLauncherPlayerView.updateUIPropertys(true);
            frameLayout.addView(mNewTVLauncherPlayerView, -1);
        }

        mPlayerPageContext = context;
    }


    // add by lxf for living streaming
    public void playLive(String liveUrl, Context context, ProgramSeriesInfo programSeriesInfo, int
            index, int position) {
        isLive = true;
        this.programSeriesInfo = programSeriesInfo;
        this.typeIndex = index;
        playLive(liveUrl, context, programSeriesInfo, true, index, position);
    }


    // add by lxf for living streaming
    public void playLive(String liveUrl, Context context, ProgramSeriesInfo programSeriesInfo,
                         boolean
                                 isNeedStartActivity, int index, int position) {
        playLive(liveUrl, "", context, programSeriesInfo, isNeedStartActivity, index, position);
    }

    public void playLive(String liveUrl, String contentUUid, Context context, ProgramSeriesInfo
            programSeriesInfo, boolean
                                 isNeedStartActivity, int index, int position) {
        Log.i(TAG, "playLive: ");
//        liveUrl = "http://test.live2.cloud.vod02.icntvcdn
// .com/live/fc3d9d396ac14f0f8e8cf8ce66d2b309/9e1e4708e25946508f96d73a96c8e09c.m3u8";
        this.programSeriesInfo = programSeriesInfo;
        this.typeIndex = index;
        if (mNewTVLauncherPlayerViewManager != null) {
            mNewTVLauncherPlayerViewManager.init(context);
        }
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.playLive(liveUrl, contentUUid, programSeriesInfo,
                    isNeedStartActivity,
                    index, position);
        } else {
            Log.i(TAG, "playLive: mNewTVLauncherPlayerView==null");

        }
    }

    public void playProgramSeries(Context context, ProgramSeriesInfo programSeriesInfo, int
            index, int position) {
        playProgramSeries(context, programSeriesInfo, true, index, position);
    }

    public void playProgramSeries(Context context, ProgramSeriesInfo programSeriesInfo, boolean
            isNeedStartActivity, int index, int position) {
        Log.i(TAG, "playProgramSeries: ");
        if (programSeriesInfo == null || programSeriesInfo.getData() == null || programSeriesInfo
                .getData().size() < 1) {
            Log.i(TAG, "playProgramSeries: programSeriesInfo==null");
            return;
        }
        if (isNeedStartActivity) {
            if (mNewTVLauncherPlayerView != null) {
                mNewTVLauncherPlayerView.buildPlayerViewConfig();
                release();
            }
        }
        isLive = false;
        this.programSeriesInfo = programSeriesInfo;
        this.typeIndex = index;
        if (mNewTVLauncherPlayerViewManager != null) {
            mNewTVLauncherPlayerViewManager.init(context);
        }
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.playProgramSeries(programSeriesInfo, isNeedStartActivity,
                    index, position);
        } else {
            Log.i(TAG, "playProgramSeries: mNewTVLauncherPlayerView==null");
        }
    }

    public void playProgramSingle(Context context, ProgramSeriesInfo programDetailInfo, int
            position, boolean openActivity) {
        Log.i(TAG, "playProgramSingle: ");
        if (programDetailInfo == null) {
            Log.i(TAG, "playProgramSingle: programDetailInfo==null");
            return;
        }
        if (openActivity) {
            if (mNewTVLauncherPlayerView != null) {
                mNewTVLauncherPlayerView.buildPlayerViewConfig();
                release();
            }
        }
        isLive = false;
        this.programSeriesInfo = programDetailInfo;
        this.typeIndex = -1;
        if (mNewTVLauncherPlayerViewManager != null) {
            mNewTVLauncherPlayerViewManager.init(context);
        }
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.playProgramSingle(programDetailInfo, position, openActivity);
        } else {
            Log.i(TAG, "playProgramSingle: mNewTVLauncherPlayerView==null");

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
            ViewGroup viewGroup = (ViewGroup) mNewTVLauncherPlayerView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mNewTVLauncherPlayerView);
            }
            mNewTVLauncherPlayerView = null;
        }
    }

    public void release() {
        Log.i(TAG, "release: ");
//        NewTVLauncherPlayer.getInstance().release();

        releasePlayer();

//        mNewTVLauncherPlayerViewManager = null;
//        mContext = null;
        closePlayerActivity();
        mPlayerPageContext = null;
//        VideoDataService.getInstance().stop();
        PlayerConfig.getInstance().cleanColumnId();
    }

    public void setVideoSilent(boolean isSilent) {
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.setVideoSilent(isSilent);
        }
    }

    private void closePlayerActivity() {
        Log.i(TAG, "closePlayerActivity: ");
        if (mPlayerPageContext != null && mPlayerPageContext instanceof Activity) {
            ((Activity) mPlayerPageContext).finish();
        }
    }

    public int getCurrentPosition() {
//        Log.i(TAG, "getCurrentPosition: ");
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getCurrentPosition();
        }
        return -1;
    }

    public boolean isLive() {
        return this.isLive;
    }

    public int getIndex() {
        Log.i(TAG, "getIndex: ");
        if (mNewTVLauncherPlayerView != null) {
            return mNewTVLauncherPlayerView.getIndex();
        }
        return -1;
    }

    public ProgramSeriesInfo getProgramSeriesInfo() {
        return programSeriesInfo;
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
            return mNewTVLauncherPlayerView.isLiving();
        }
        return false;
    }

    public void setContinuePlay(Context context, ProgramSeriesInfo mProgramSeriesInfo,
                                NewTVLauncherPlayerView
                                        .PlayerViewConfig config,
                                int position) {
        Log.i(TAG, "setContinuePlay: ");
        if (mNewTVLauncherPlayerView == null) {
            mNewTVLauncherPlayerView = new NewTVLauncherPlayerView(config, context);
            setPendingListener();
        }
        if (typeIndex == -1) {
            playProgramSingle(context, mProgramSeriesInfo, position, false);
        } else {
            playProgramSeries(context, mProgramSeriesInfo, false, typeIndex,
                    position);
        }
    }

    public boolean isADPlaying() {
        return mNewTVLauncherPlayerView != null && mNewTVLauncherPlayerView.isADPlaying();
    }

    public boolean registerScreenListener(ScreenListener listener){
        if (mNewTVLauncherPlayerView != null) {
            mNewTVLauncherPlayerView.registerScreenListener(listener);
        }else {
            pendingListener.add(listener);
        }
        return true;
    }

    public void unregisterScreenListener(ScreenListener listener){
        if(mNewTVLauncherPlayerView != null){
            mNewTVLauncherPlayerView.unregisterScreenListener(listener);
        }else {
            pendingListener.remove(listener);
        }
    }

    private void setPendingListener(){
        if(mNewTVLauncherPlayerView != null){
            for(ScreenListener listener : pendingListener) {
                mNewTVLauncherPlayerView.registerScreenListener(listener);
            }
            pendingListener.clear();
        }
    }
}
