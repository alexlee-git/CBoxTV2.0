package tv.newtv.cboxtv.cms.special.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.libs.util.LiveTimingUtil;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.contract.LiveContract;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.model.LivePermissionCheckBean;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class BallPlayerFragment extends BaseSpecialContentFragment implements LiveContract.View {
    private TextView textTitle;
    private ImageView mImageView;
    private Content mProgramInfo;
    private LivePermissionCheckBean livePermissionCheck;
    private Content info;
    private boolean isDestroyed = false;

    private LiveContract.Presenter livePresenter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mProgramInfo != null) {
            outState.putSerializable("programInfo", mProgramInfo);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("programInfo")) {
                mProgramInfo = (Content) savedInstanceState.getSerializable("programInfo");
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ball_player_layout;
    }

    @Override
    protected void onItemContentResult(Content content) {

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
                if (!videoPlayerView.isReady()) return;
                videoPlayerView.EnterFullScreen(getActivity(), true);
            }

            @Override
            public void AllPlayComplete(boolean isError, String info, VideoPlayerView
                    videoPlayerView) {

            }

            @Override
            public void ProgramChange() {

            }
        });
        videoPlayerView.requestFocus();

        textTitle = view.findViewById(R.id.id_title);

        livePresenter = new LiveContract.LivePresenter(getContext(), this);

        if (mProgramInfo != null) {
            textTitle.setText(mProgramInfo.getSubTitle());
            LiveInfo liveInfo = new LiveInfo(mProgramInfo);
            if (liveInfo.isLiveTime()) {
                livePresenter.checkLive(liveInfo);
            }
        } else {

        }
    }

    @Override
    protected VideoPlayerView getVideoPlayer() {
        return videoPlayerView;
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (livePermissionCheck != null) {
            startPlay();
        } else {
//            startPlayPermissionsCheck(mProgramInfo);
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
        if (isDestroyed || mProgramInfo == null) return;

        if (!isLive()) {
            ((TextView) contentView.findViewById(R.id.tv_hint)).setText("暂无播放");
            mImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(mProgramInfo.getHImage())
                    .into(mImageView);
            return;
        }

        if (!TextUtils.isEmpty(mProgramInfo.getTitle())) {
            textTitle.setText(mProgramInfo.getTitle());
        }


//        videoPlayerView.playLiveVideo(mProgramInfo.getContentUUID(),mProgramInfo.getPlayUrl(),
//                mProgramInfo.getTitle(),0,0);

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
//        return !TextUtils.isEmpty(mProgramInfo.getPlayUrl()) && CmsLiveUtil.isInPlay(
//                mProgramInfo.getLiveLoopType(), mProgramInfo.getLiveParam(), mProgramInfo
//                        .getPlayStartTime(), mProgramInfo.getPlayEndTime(), null);

        return false;
    }

    @Override
    public void liveChkResult(LiveInfo liveInfo) {
        videoPlayerView.playLive(liveInfo, false,null);
    }

    @Override
    public void onChkError(String code, String desc) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

//=======
//    private void timer(){
//        LiveTimingUtil.endTime(mProgramInfo.getPlayEndTime(), new LiveTimingUtil
// .LiveEndListener() {
//            @Override
//            public void end() {
////                if(videoPlayerView != null){
////                    videoPlayerView.setHintText("播放已结束");
////                    videoPlayerView.setHintTextVisible(View.VISIBLE);
////                }
//
//                if(!NewTVLauncherPlayerViewManager.getInstance().isLiving()){
//                    Log.e(BallPlayerFragment.class.getSimpleName(), "非直播时间，不结束播放");
//                    return;
//                }
//                if(!TextUtils.isEmpty(mProgramInfo.getImg())){
//                    mImageView.setVisibility(View.VISIBLE);
//                    Picasso.get()
//                            .load(mProgramInfo.getImg())
//                            .into(mImageView);
//                }
//                TimeDialog.showBuilder(getContext());
//                stopPlay();
//            }
//        });
//>>>>>>> 1.4
    }
}
