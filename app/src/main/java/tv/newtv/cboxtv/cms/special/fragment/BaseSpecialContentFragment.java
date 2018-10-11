package tv.newtv.cboxtv.cms.special.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.Constant;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.cms.details.model.VideoPlayInfo;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import com.newtv.libs.util.RxBus;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public abstract class BaseSpecialContentFragment extends Fragment {
    protected boolean UiReady = false;
    protected View contentView;
    protected VideoPlayerView videoPlayerView;
    private Observable<VideoPlayInfo> mUpdateVideoInfoObservable;
    private int mPlayPosition = 0;
    private int mPlayIndex = 0;
    private NewTVLauncherPlayerView.PlayerViewConfig defaultPlayerConfig;

    protected abstract int getLayoutId();

    protected abstract void setUpUI(View view);

    protected VideoPlayerView getVideoPlayer() {
        return videoPlayerView;
    }

    protected int getVideoPlayIndex() {
        return mPlayIndex;
    }

    @Override
    public void onStop() {

        if (videoPlayerView != null) {
            defaultPlayerConfig = videoPlayerView.getDefaultConfig();

            mPlayIndex = videoPlayerView.getIndex();
            mPlayPosition = videoPlayerView.getCurrentPosition();
            videoPlayerView.stopPlay();
            videoPlayerView.release();
            videoPlayerView.destory();
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contentView = null;
        RxBus.get().unregister(Constant.UPDATE_VIDEO_PLAY_INFO, mUpdateVideoInfoObservable);
        mUpdateVideoInfoObservable = null;
    }


    protected boolean isLiveVideo(){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(videoPlayerView != null && videoPlayerView.isReleased()){
            ViewGroup parent = (ViewGroup) videoPlayerView.getParent();
            if (parent != null) {
                parent.removeView(videoPlayerView);
            }
            videoPlayerView = null;
        }

        if (defaultPlayerConfig != null && videoPlayerView == null) {
            videoPlayerView = new VideoPlayerView(defaultPlayerConfig, getContext());
            if(defaultPlayerConfig.defaultFocusView instanceof NewTVLauncherPlayerView){
                videoPlayerView.requestFocus();
            }
        }

        if (videoPlayerView != null && videoPlayerView.isReady() && !isLiveVideo()) {
            videoPlayerView.playSingleOrSeries(mPlayIndex, mPlayPosition);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    protected View getContentView() {
        return contentView;
    }

    public void enterFullScreen() {

    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), container, false);
            mUpdateVideoInfoObservable = RxBus.get().register(Constant.UPDATE_VIDEO_PLAY_INFO);
            mUpdateVideoInfoObservable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<VideoPlayInfo>() {
                        @Override
                        public void accept(VideoPlayInfo data) throws Exception {
                            mPlayIndex = data.index;
                            mPlayPosition = data.position;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            setUpUI(contentView);
        }
        return contentView;
    }

    public abstract void setModuleInfo(ModuleInfoResult infoResult);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiReady = true;
    }
}
