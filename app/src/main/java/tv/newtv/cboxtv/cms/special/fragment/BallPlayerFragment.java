package tv.newtv.cboxtv.cms.special.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.model.LivePermissionCheckBean;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.CmsLiveUtil;
import tv.newtv.cboxtv.utils.LivePermissionCheckUtil;
import tv.newtv.cboxtv.utils.LiveTimingUtil;
import tv.newtv.cboxtv.utils.PlayInfoUtil;
import tv.newtv.cboxtv.views.TimeDialog;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class BallPlayerFragment extends BaseSpecialContentFragment {
//    private IcntvLive icntvLive;
//    private FrameLayout mFocusView;
    private TextView textTitle;
    private ImageView mImageView;
    private ProgramInfo mProgramInfo;
    private LivePermissionCheckBean livePermissionCheck;
    private ProgramSeriesInfo info;
    private boolean isDestroyed = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mProgramInfo != null){
            outState.putSerializable("programInfo",mProgramInfo);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("programInfo")){
                mProgramInfo = (ProgramInfo) savedInstanceState.getSerializable("programInfo");
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ball_player_layout;
    }

    @Override
    protected void setUpUI(View view) {
        videoPlayerView = view.findViewById(R.id.video_player);

        textTitle = view.findViewById(R.id.id_title);
        mImageView = view.findViewById(R.id.image);

        videoPlayerView.setPlayerCallback(new PlayerCallback() {
            @Override
            public void onEpisodeChange(int index, int position) {

            }

            @Override
            public void onPlayerClick(VideoPlayerView videoPlayerView) {
                if(!videoPlayerView.isReady()) return;
                videoPlayerView.EnterFullScreen(getActivity(),true);
            }

            @Override
            public void AllPalyComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {

            }

            @Override
            public void ProgramChange() {

            }
        });
        videoPlayerView.requestFocus();

//        mFocusView = view.findViewById(R.id.video_player_focus);
        textTitle = view.findViewById(R.id.id_title);

//        mFocusView.requestFocus();
//        mFocusView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                videoPlayerView.EnterFullScreen(getActivity(), false);
//            }
//        });

        if (mProgramInfo != null) {
            textTitle.setText(mProgramInfo.getSubTitle());
            startPlayPermissionsCheck(mProgramInfo);
            preData();
        }
    }

    @Override
    protected VideoPlayerView getVideoPlayer() {
        return videoPlayerView;
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {
        mProgramInfo = infoResult.getDatas().get(0).getDatas().get(0);
        if (UiReady && mProgramInfo != null) {
            textTitle.setText(mProgramInfo.getSubTitle());
//            startPlayPermissionsCheck(mProgramInfo);
            preData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(livePermissionCheck != null){
            startPlay();
        }else {
            startPlayPermissionsCheck(mProgramInfo);
        }
    }

    private void stopPlay() {
        if (videoPlayerView != null) {
            try {
                videoPlayerView.release();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
    }

    private void startPlay() {
//        stopPlay();
        if(isDestroyed || mProgramInfo == null) return;

        if (!isLive()) {
            ((TextView)contentView.findViewById(R.id.tv_hint)).setText("暂无播放");
            mImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(mProgramInfo.getImg())
                    .into(mImageView);
            return;
        }

        if (!TextUtils.isEmpty(mProgramInfo.getTitle())) {
            textTitle.setText(mProgramInfo.getTitle());
        }

        timer();
        videoPlayerView.playLiveVideo(mProgramInfo.getContentUUID(),mProgramInfo.getPlayUrl(),
                mProgramInfo.getTitle(),0,0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveTimingUtil.clearListener();
    }

    @Override
    protected boolean isLiveVideo() {
        return true;
    }

    private boolean isLive() {
        return !TextUtils.isEmpty(mProgramInfo.getPlayUrl()) && CmsLiveUtil.isInPlay(
                mProgramInfo.getLiveLoopType(), mProgramInfo.getLiveParam(), mProgramInfo
                        .getPlayStartTime(), mProgramInfo.getPlayEndTime(), null);
    }

    private void startPlayPermissionsCheck(ProgramInfo programInfo) {
        LivePermissionCheckUtil.startPlayPermissionsCheck(LivePermissionCheckUtil.createPlayCheckRequest(programInfo)
                , new LivePermissionCheckUtil.PermissionCheck() {
                    @Override
                    public void onSuccess(LivePermissionCheckBean result) {
                        livePermissionCheck = result;
                        startPlay();
                    }
                });
    }

    private void preData(){
        if(mProgramInfo == null || TextUtils.isEmpty(mProgramInfo.getContentUUID()) || info != null)
            return;

        PlayInfoUtil.getPageInfo(mProgramInfo.getContentUUID(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(ProgramSeriesInfo info) {
                if(info != null){
                    videoPlayerView.setSeriesInfo(info);
                    BallPlayerFragment.this.info = info;
                }
            }
        });
    }

    private void timer(){
        LiveTimingUtil.endTime(mProgramInfo.getPlayEndTime(), new LiveTimingUtil.LiveEndListener() {
            @Override
            public void end() {
//                if(videoPlayerView != null){
//                    videoPlayerView.setHintText("播放已结束");
//                    videoPlayerView.setHintTextVisible(View.VISIBLE);
//                }
                if(!TextUtils.isEmpty(mProgramInfo.getImg())){
                    mImageView.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(mProgramInfo.getImg())
                            .into(mImageView);
                }
                TimeDialog.showBuilder(getContext());
                stopPlay();
            }
        });
    }
}
