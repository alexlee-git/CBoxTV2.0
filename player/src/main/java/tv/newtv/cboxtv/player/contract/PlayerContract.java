package tv.newtv.cboxtv.player.contract;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         12:45
 * 创建人:           weihaichao
 * 创建日期:          2018/11/26
 */
public class PlayerContract {

    public static final int STATE_NORMAL = 0;//默认状态
    public static final int STATE_AD_BUFFERING = 1;//加载广告状态
    public static final int STATE_AD_PLAYING = 2;//广告播放状态
    public static final int STATE_VIDEO_BUFFERING = 3;//正片加载状态
    public static final int STATE_VIDEO_PLAYING = 4;//正片播放状态
    public static final int STATE_VIDEO_SEEK_START = 5;//跳转播放状态
    public static final int STATE_VIDEO_SEEK_END = 6;//跳转播放状态结束

    private static final int TIP_VIEW_CHANGE = 0x1119;

    private int currentShow = 0;                        //当前播放器UI显示状态
    private boolean isFullScreen = false;               //当前播放器是否为全屏状态
    private boolean isVideoPlaying = false;             //当前播放器是否为播放正片状态
    private int currentState = STATE_NORMAL;
    private List<NewTVLauncherPlayerView.OnPlayerStateChange> mOnViewVisibleChangeList;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mOnViewVisibleChangeList != null && mOnViewVisibleChangeList.size() > 0) {
                for(NewTVLauncherPlayerView.OnPlayerStateChange stateChange : mOnViewVisibleChangeList) {
                    if (stateChange.onStateChange(isFullScreen, currentShow, isVideoPlaying)) {
                        break;
                    }
                }
            }
            return false;
        }
    });

    public PlayerContract() {
        mHandler.sendEmptyMessageDelayed(TIP_VIEW_CHANGE, 3000);
    }

    private String getStateType(int state) {
        switch (state) {
            case STATE_NORMAL:
                return "state_normal";
            case STATE_AD_BUFFERING:
                return "STATE_AD_BUFFERING";
            case STATE_AD_PLAYING:
                return "STATE_AD_PLAYING";
            case STATE_VIDEO_BUFFERING:
                return "STATE_VIDEO_BUFFERING";
            case STATE_VIDEO_PLAYING:
                return "STATE_VIDEO_PLAYING";
            case STATE_VIDEO_SEEK_START:
                return "STATE_VIDEO_SEEK_START";
            case STATE_VIDEO_SEEK_END:
                return "STATE_VIDEO_SEEK_END";
            default:
                return "UNKONWN";
        }
    }

    /**
     * 当前播放器播放状态是否与该值相同
     *
     * @param state
     * @return
     */
    public boolean equalsPlayerState(int state) {
        return currentState == state;
    }

    /**
     * 设置播放器当前播放状态
     *
     * @param state
     */
    public void setPlayerState(int state) {
        LogUtils.d("PlayerContract", "set state = " + getStateType(state));
        currentState = state;
        boolean isPlaying = (state == STATE_VIDEO_PLAYING);
        if(isPlaying != isVideoPlaying){
            isVideoPlaying = isPlaying;
            notifyToUI();
        }
    }

    public void screenSizeChange(int width, int height) {
        boolean fullScreen = (width == ScreenUtils.getScreenW() && height ==
                ScreenUtils.getScreenH());
        if (fullScreen != isFullScreen) {
            isFullScreen = fullScreen;
            notifyToUI();
        }
    }

    private void notifyToUI() {
        if (mOnViewVisibleChangeList != null && mOnViewVisibleChangeList.size() > 0) {
            if (mHandler.hasMessages(TIP_VIEW_CHANGE)) {
                mHandler.removeMessages(TIP_VIEW_CHANGE);
            }
            mHandler.sendEmptyMessageDelayed(TIP_VIEW_CHANGE, 100);
        }
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mOnViewVisibleChangeList != null) {
            mOnViewVisibleChangeList.clear();
        }
        mOnViewVisibleChangeList = null;
    }

    public void setCurrentShow(int current) {
        if (currentShow == current) return;
        currentShow = current;
        notifyToUI();
    }

    public boolean processKeyEvent(KeyEvent event) {
        for(NewTVLauncherPlayerView.OnPlayerStateChange stateChange : mOnViewVisibleChangeList) {
            if (stateChange.processKeyEvent(event)) {
                return true;
            }
        }
        return false;
    }

    public void setOnPlayerStateChange(NewTVLauncherPlayerView.OnPlayerStateChange change) {
        if (mOnViewVisibleChangeList == null) {
            mOnViewVisibleChangeList = new ArrayList<>();
        }
        mOnViewVisibleChangeList.add(change);
    }
}
