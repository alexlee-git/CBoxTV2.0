package tv.newtv.cboxtv.player;

import android.content.Context;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by wangkun on 2018/1/15.
 */

public interface IVideoPlayerControlInterface {
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent callback, VideoDataStruct videoDataStruct);

    public boolean stopVideo();

    public boolean pauseVideo();

//    public boolean onAdKeyEvent(int keyCode, KeyEvent event);

    public boolean startVideo();

    public boolean isPlaying();

    public boolean isAlive();

    public boolean isADPlaying();

    public int getDuration();

    public void setDataSource(String definition);

    public int getCurrentPosition();

    public void seekTo(int position);

    public void releaseVideo();

    public void setVideoSize(int sizeType);
}
