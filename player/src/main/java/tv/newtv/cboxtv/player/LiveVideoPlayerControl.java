package tv.newtv.cboxtv.player;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.model.VideoDataStruct;
import tv.newtv.cboxtv.player.newtv.NewTVLiveVideoPlayer;

/**
 * Created by wangkun on 2018/1/15.
 */

public class LiveVideoPlayerControl implements IVideoPlayerControlInterface {

    private static final String TAG = "LiveVideoPlayerControl";
    private volatile static LiveVideoPlayerControl mLiveVideoPlayerControl;
    private ILiveVideoPlayerInterface mLiveVideoPlayer;
    private LiveInfo mLiveInfo;
    private iPlayCallBackEvent mCallback;

    private LiveVideoPlayerControl() {}

    public static LiveVideoPlayerControl getInstance() {
        if (mLiveVideoPlayerControl == null) {
            synchronized (LiveVideoPlayerControl.class) {
                if (mLiveVideoPlayerControl == null) {
                    mLiveVideoPlayerControl = new LiveVideoPlayerControl();
                }
            }
        }
        return mLiveVideoPlayerControl;
    }

    public void setLiveInfo(LiveInfo liveInfo) {
        mLiveInfo = liveInfo;
    }

    @Override
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent
            callback, VideoDataStruct videoDataStruct) {
        Log.i(TAG, "playVideo: ");
        mCallback = callback;
        if (videoDataStruct == null) {
            Log.i(TAG, "playVideo: videoDataStruct==null");
            return false;
        }
        int dataSource = videoDataStruct.getDataSource();
        Log.i(TAG, "playVideo: dataSource=" + dataSource);
        switch (dataSource) {
            case PlayerConstants.DATASOURCE_ICNTV:
                mLiveVideoPlayer = NewTVLiveVideoPlayer.getInstance(context);
                break;
            default:
                return false;
        }
        if (mLiveVideoPlayer == null) {
            return false;
        }
        return mLiveVideoPlayer.playVideo(context, frameLayout, callback, videoDataStruct);
    }

    @Override
    public boolean stopVideo() {
        return false;
    }

    @Override
    public boolean pauseVideo() {
        return false;
    }

    @Override
    public boolean startVideo() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isADPlaying() {
        return false;
    }

    @Override
    public int getDuration() {
        if (mLiveInfo != null) {
            return mLiveInfo.getPlayLength();
        }
        return 0;
    }

    @Override
    public void setDataSource(String definition) {

    }

    @Override
    public int getCurrentPosition() {
        if (mLiveInfo != null) {
            return mLiveInfo.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mLiveInfo != null && mCallback != null && mLiveInfo.isTimeShift()) {
//            mLiveInfo.setTimeDelay(position);
            Log.e("Live", "seekTo:" + position);
            mCallback.changePlayWithDelay(position,mLiveInfo.getLiveUrl());
        }
    }

    @Override
    public void releaseVideo() {
        if(mLiveVideoPlayer != null) {
            mLiveVideoPlayer.releaseVideo();
            mLiveVideoPlayer = null;
        }

        mCallback = null;

    }

    @Override
    public void setVideoSize(int sizeType) {

    }

    @Override
    public void setVideoSilent(boolean isSilent) {
        Log.i(TAG, "setVideoSilent");
        if (mLiveVideoPlayer != null) {
            mLiveVideoPlayer.setVideoSilent(isSilent);
        }
    }
}
