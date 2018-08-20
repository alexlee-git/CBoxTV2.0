package tv.newtv.cboxtv.player;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.VideoDataStruct;
import tv.newtv.cboxtv.player.newtv.NewTVVodVideoPlayer;

/**
 * Created by wangkun on 2018/1/15.
 */

public class VodVideoPlayerControl implements IVideoPlayerControlInterface {

    private static final String TAG = "VodVideoPlayerControl";
    private static VodVideoPlayerControl mVodVideoPlayerControl;
    private IVodVideoPlayerInterface mVodVideoPlayer;


    public static VodVideoPlayerControl getInstance(){
        if(mVodVideoPlayerControl==null){
            synchronized (VodVideoPlayerControl.class){
                if(mVodVideoPlayerControl==null){
                    mVodVideoPlayerControl = new VodVideoPlayerControl();
                }
            }
        }
        return mVodVideoPlayerControl;
    }
    @Override
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent callback, VideoDataStruct videoDataStruct) {
        Log.i(TAG, "playVideo: ");
        if(videoDataStruct==null){
            Log.i(TAG, "playVideo: videoDataStruct==null");
            return false;
        }
        int dataSource = videoDataStruct.getDataSource();
        Log.i(TAG, "playVideo: dataSource="+dataSource);
        switch (dataSource){
            case Constants.DATASOURCE_ICNTV:
                mVodVideoPlayer = NewTVVodVideoPlayer.getInstance(context);
                break;
            default:
                return false;
        }
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.playVideo(context,frameLayout,callback,videoDataStruct);
    }

    @Override
    public boolean stopVideo() {
        Log.i(TAG, "stopVideo: ");
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.stopVideo();
    }

    @Override
    public boolean pauseVideo() {
        Log.i(TAG, "pauseVideo: ");
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.pauseVideo();
    }

    @Override
    public boolean startVideo() {
        Log.i(TAG, "startVideo: ");
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.start();
    }

    @Override
    public boolean isPlaying() {
        Log.i(TAG, "isPlaying: ");
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.isPlaying();
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public boolean isADPlaying() {
        Log.i(TAG, "isADPlaying: ");
        if(mVodVideoPlayer==null){
            return false;
        }
        return mVodVideoPlayer.isADPlaying();
    }

    @Override
    public int getDuration() {
//        Log.i(TAG, "getDuration: ");
        if(mVodVideoPlayer==null){
            return 0;
        }
        return mVodVideoPlayer.getDuration();
    }

    @Override
    public void setDataSource(String definition) {
        Log.i(TAG, "setDataSource: ");
        if(mVodVideoPlayer==null){
            return;
        }
        mVodVideoPlayer.setDataSource(definition);
    }

    @Override
    public int getCurrentPosition() {
//        Log.i(TAG, "getCurrentPosition: ");
        if(mVodVideoPlayer==null){
            return 0;
        }
        return mVodVideoPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int position) {
        Log.i(TAG, "seekTo: ");
        if(mVodVideoPlayer!=null){
            mVodVideoPlayer.seekTo(position);
        }
    }

    @Override
    public void releaseVideo() {
        Log.i(TAG, "releaseVideo: ");
        if(mVodVideoPlayer!=null){
            mVodVideoPlayer.releaseVideo();
        }
    }

    @Override
    public void setVideoSize(int sizeType) {
        Log.i(TAG, "setVideoSize: ");
        if(mVodVideoPlayer!=null){
            mVodVideoPlayer.setVideoSize(sizeType);
        }
    }
}
