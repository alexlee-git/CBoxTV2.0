package tv.newtv.cboxtv.player;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by wangkun on 2018/1/15.
 */

public class NewTVLauncherPlayer {

    private static final String TAG = "NewTVLauncherPlayer";
    private static NewTVLauncherPlayer mNewTVLauncherPlayer;
    //    private Context mContext;
    private IVideoPlayerControlInterface mVideoPlayerControl;

    public NewTVLauncherPlayer() {
    }

    public static NewTVLauncherPlayer getInstance() {

        if (mNewTVLauncherPlayer == null) {
            synchronized (NewTVLauncherPlayer.class) {
                if (mNewTVLauncherPlayer == null) {
                    mNewTVLauncherPlayer = new NewTVLauncherPlayer();
                }
            }
        }

        return mNewTVLauncherPlayer;
    }

    public boolean playAlive(Context context, FrameLayout frameLayout, LiveInfo liveInfo,
                             iPlayCallBackEvent
                                     callback, VideoDataStruct videoDataStruct) {
        if (mVideoPlayerControl != null) {
            release();
        }
        int type = videoDataStruct.getPlayType();
        Log.i(TAG, "play: type=" + type);
        if (type == PlayerConstants.PLAYTYPE_VOD) {
            mVideoPlayerControl = VodVideoPlayerControl.getInstance();
        } else if (type == PlayerConstants.PLAYTYPE_LIVE) {
            mVideoPlayerControl = LiveVideoPlayerControl.getInstance();
            ((LiveVideoPlayerControl) mVideoPlayerControl).setLiveInfo(liveInfo);
        } else {
            Log.i(TAG, "play: playType is undefined");
            return false;
        }
        return mVideoPlayerControl.playVideo(context, frameLayout, callback, videoDataStruct);
    }

    /*
    * 启动播放流程
    */
    public boolean play(Context context, FrameLayout frameLayout, iPlayCallBackEvent callback, VideoDataStruct videoDataStruct) {
        if (mVideoPlayerControl != null) {
            release();
        }
        int type = videoDataStruct.getPlayType();
        Log.i(TAG, "play: type=" + type);
        if (type == PlayerConstants.PLAYTYPE_VOD) {
            mVideoPlayerControl = VodVideoPlayerControl.getInstance();
        } else if (type == PlayerConstants.PLAYTYPE_LIVE) {
            mVideoPlayerControl = LiveVideoPlayerControl.getInstance();
        } else {
            Log.i(TAG, "play: playType is undefined");
            return false;
        }
        return mVideoPlayerControl.playVideo(context, frameLayout, callback, videoDataStruct);
    }

    /*
    * 继续播放
    */
    public boolean start() {
        Log.i(TAG, "start: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.startVideo();
    }

    /*
    * 暂停
    */
    public boolean pause() {
        Log.i(TAG, "pause: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.pauseVideo();
    }

    /*
    * 停止
    */
    public boolean stop() {
        Log.i(TAG, "stop: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.stopVideo();
    }

    /*
    * 释放
    */
    public void release() {
        Log.i(TAG, "release: ");
        if (mVideoPlayerControl != null) {
            mVideoPlayerControl.releaseVideo();
            mVideoPlayerControl = null;
        } else {
            Log.i(TAG, "release: mVideoPlayerControl==null");
        }
        Log.i(TAG, "release: set media pid start");
        // SystemProperties.set("media.player.pid","");
        Log.i(TAG, "release: set media pid end");
    }

    /*
    * 快进快退
    */
    public void seekTo(int position) {
        Log.i(TAG, "seekTo: position" + position);
        if (mVideoPlayerControl != null) {
            if (!mVideoPlayerControl.isAlive()) {
                if (position < 0) {
                    mVideoPlayerControl.seekTo(0);
                } else if (position >= getDuration() - 10000) {

                    mVideoPlayerControl.seekTo(getDuration() - 10000);
                } else {
                    mVideoPlayerControl.seekTo(position);
                }
            } else {
                mVideoPlayerControl.seekTo(position);
            }
        } else {
            Log.i(TAG, "seekTo: mVideoPlayerControl==null");
        }
    }

    /*
    * 是否正在播放正片
    */
    public boolean isPlaying() {
        Log.i(TAG, "isPlaying: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.isPlaying();
    }

    /*
    * 是否正在播放广告
    */
    public boolean isADPlaying() {
        Log.i(TAG, "isADPlaying: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.isADPlaying();
    }

    /*
    * 获取时长
    */
    public int getDuration() {
//        Log.i(TAG, "getDuration: ");
        if (mVideoPlayerControl == null) {
            return 0;
        }
        return mVideoPlayerControl.getDuration();
    }

    /*
    * 获取当前位置
    */
    public int getCurrentPosition() {
//        Log.i(TAG, "getCurrentPosition: ");
        if (mVideoPlayerControl == null) {
            return 0;
        }
        return mVideoPlayerControl.getCurrentPosition();
    }


    /*
    * 切换视频源
    * */
    public void setDataSource(String definition) {
        Log.i(TAG, "setDataSource: ");
        if (mVideoPlayerControl == null) {
            return;
        }
        mVideoPlayerControl.setDataSource(definition);
    }

    /*
    * 设置视频大小
    * */

    public void setVideoSize(int sizeType) {
        Log.i(TAG, "setVideoSize: ");
        if (mVideoPlayerControl == null) {
            return;
        }
        mVideoPlayerControl.setVideoSize(sizeType);
    }

    public boolean isAlive() {
        Log.i(TAG, "isAlive: ");
        if (mVideoPlayerControl == null) {
            return false;
        }
        return mVideoPlayerControl.isAlive();
    }

    public void setVideoSilent(boolean isSilent) {
        Log.i(TAG, "setVideoSilent:" + isSilent);
        if (mVideoPlayerControl != null) {
            mVideoPlayerControl.setVideoSilent(isSilent);
        }
    }
}
