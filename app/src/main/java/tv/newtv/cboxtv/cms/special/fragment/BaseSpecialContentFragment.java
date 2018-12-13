package tv.newtv.cboxtv.cms.special.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;

import io.reactivex.Observable;

import com.newtv.libs.bean.VideoPlayInfo;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public abstract class BaseSpecialContentFragment extends Fragment implements ContentContract.View {
    protected boolean UiReady = false;
    protected View contentView;
    protected VideoPlayerView videoPlayerView;
    private Observable<VideoPlayInfo> mUpdateVideoInfoObservable;
    private int mPlayPosition = 0;
    private int mPlayIndex = 0;
    private ContentContract.Presenter mPresenter;
    private NewTVLauncherPlayerView.PlayerViewConfig defaultPlayerConfig;
    protected boolean activityReady = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReady = true;
    }

    protected abstract int getLayoutId();

    protected abstract void onItemContentResult(String uuid, Content content);

    protected abstract void setUpUI(View view);

    protected VideoPlayerView getVideoPlayer() {
        return videoPlayerView;
    }

    protected int getVideoPlayIndex() {
        return mPlayIndex;
    }

    protected void getContent(String uuid,String contentType){
        if(TextUtils.isEmpty(uuid)){
            onError(getContext(), "" , "播放ID不能为空");
            return;
        }
        mPresenter.getContent(uuid,true,contentType);
    }

    protected void getSubContents(String uuid){
        if(TextUtils.isEmpty(uuid)){
            onError(getContext(), "" , "播放ID不能为空");
            return;
        }
        mPresenter.getSubContent(uuid);
    }

    protected void getContent(String uuid){
        mPresenter.getContent(uuid,true);
    }

    @Override
    public void onContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable Content content) {
        onItemContentResult(uuid, content);
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @org.jetbrains
            .annotations.Nullable String desc) {
        ToastUtil.showToast(context.getApplicationContext(),desc);
    }

    @Override
    public void onStop() {

        if (videoPlayerView != null) {
            defaultPlayerConfig = videoPlayerView.getDefaultConfig();

            mPlayIndex = videoPlayerView.getIndex();
            mPlayPosition = videoPlayerView.getCurrentPosition();


            videoPlayerView.release();
            videoPlayerView.destory();
            videoPlayerView = null;
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPresenter != null){
            mPresenter.destroy();
            mPresenter = null;
        }

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
            mPlayPosition = defaultPlayerConfig.playPosition;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), container, false);

            mPresenter = new ContentContract.ContentPresenter(contentView.getContext(),this);
            setUpUI(contentView);
        }
        return contentView;
    }

    public abstract void setModuleInfo(ModelResult<ArrayList<Page>> infoResult);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiReady = true;
    }
}
