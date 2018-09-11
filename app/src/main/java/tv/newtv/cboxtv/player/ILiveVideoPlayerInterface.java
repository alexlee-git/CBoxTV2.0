package tv.newtv.cboxtv.player;

import android.content.Context;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by wangkun on 2018/1/15.
 */

public interface ILiveVideoPlayerInterface {
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent callBack, VideoDataStruct videoDataStruct);

    public boolean isADPlaying();

    public void setDataSource(String definition);

    public void releaseVideo();

    public void setVideoSilent(boolean isSilent);
}
