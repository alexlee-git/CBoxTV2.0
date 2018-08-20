package tv.newtv.cboxtv.player.videoview;

import android.widget.FrameLayout;

import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by gaoleichao on 2018/4/10.
 */

public class VideoPlayContract {
    interface View {

        void showProgramCheckFailed();

        void showProgramError();

        void showProgramError(String msg);

        void playVideo(VideoDataStruct dataStruct);
        void playLiveVideo(VideoDataStruct dataStruct);

        void startLoading(boolean isShowName);

        void stopLoading();

        void setDuration(int duration);

        void updateProgress(int duration);

        int getDuration();

        int getProgress();

        void playNext();

        void setPlayerMessage(final String message, int delay);

        void setHintTextVisible(int visible);

        void setHintText(String text);
    }

    interface Presenter {

        void startPlayPermissionsCheck(int mSeriesOrSingle, String uuid, String albumId, int position);

        // add by lxf for live streaming
        void noPermissionsCheck(String liveUrl,String title,int index,int position);

        void playVideo(FrameLayout frameLayout, VideoDataStruct dataStruct);

        // add by lxf for live streaming
        void playLiveVideo(FrameLayout frameLayout, VideoDataStruct dataStruct);

        void startPlay();

        void pausePlay();

        void stopPlay();

        void releasePlay();

        boolean isPlaying();

        void destory();

        boolean isAdPlaying();

        int getCurrentPosition();
    }
}
