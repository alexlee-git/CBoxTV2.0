package tv.newtv.cboxtv.cms.special.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.util.LiveTimingUtil;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.contract.LiveContract;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class BallPlayerFragment extends BaseSpecialContentFragment implements LiveContract.View,
        LiveListener {
    private TextView textTitle;
    private RecycleImageView mImageView;
    private TextView mHintText;
    private boolean isDestroyed = false;
    private LiveInfo mLiveInfo;
    private ModelResult<ArrayList<Page>> mInfoResult;

    private LiveContract.Presenter livePresenter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.ball_player_layout;
    }

    @Override
    protected void onItemContentResult(String uuid, Content content) {

    }

    @Override
    protected void setUpUI(View view) {
        videoPlayerView = view.findViewById(R.id.video_player);

        textTitle = view.findViewById(R.id.id_title);
        mImageView = view.findViewById(R.id.image);
        mHintText = view.findViewById(R.id.tv_hint);

        videoPlayerView.setPlayerCallback(new PlayerCallback() {
            @Override
            public void onEpisodeChange(int index, int position) {

            }

            @Override
            public void onPlayerClick(VideoPlayerView videoPlayerView) {
                videoPlayerView.EnterFullScreen(getActivity(), false);
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
        parseLive(getContext());
    }

    private boolean checkLivePresenterIsNull(Context context) {
        if (livePresenter == null && context != null) {
            livePresenter = new LiveContract.LivePresenter(context, this);
            return true;
        }
        return false;
    }

    @Override
    protected VideoPlayerView getVideoPlayer() {
        return videoPlayerView;
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        mInfoResult = infoResult;
        parseLive(getContext());
    }

    private void parseLive(Context context) {
        if (mInfoResult != null && mInfoResult.getData() != null
                && mInfoResult.getData().size() >= 1 && contentView != null) {
            Page current = mInfoResult.getData().get(0);
            if (current.getPrograms() != null && current.getPrograms().size() >= 1) {
                Program currentProgram = current.getPrograms().get(0);
                if (mImageView != null) {
                    mImageView.load(currentProgram.getImg());
                }
                if (!TextUtils.isEmpty(currentProgram.getTitle())) {
                    textTitle.setText(currentProgram.getTitle());
                }
                LiveInfo liveInfo = new LiveInfo(currentProgram.getTitle(), currentProgram
                        .getVideo());
                if (liveInfo.isLiveTime()) {
                    startPlay(liveInfo);
                } else {
                    onComplete();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlay(mLiveInfo);
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

    private void startPlay(LiveInfo liveInfo) {
        if (mLiveInfo != null) {
            videoPlayerView.playLive(mLiveInfo, false, this);
        } else {
            if (liveInfo != null && liveInfo.isLiveTime() && checkLivePresenterIsNull(getContext
                    ())) {
                livePresenter.checkLive(liveInfo);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayerView = null;
        if (livePresenter != null) {
            livePresenter.destroy();
            livePresenter = null;
        }
        LiveTimingUtil.clearListener();
    }

    @Override
    protected boolean isLiveVideo() {
        return true;
    }

    @Override
    public void liveChkResult(LiveInfo liveInfo) {
        mLiveInfo = liveInfo;
        startPlay(mLiveInfo);
    }

    @Override
    public void onChkError(String code, String desc) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

    }

    @Override
    public void onTimeChange(String current, String end) {
        videoPlayerView.setTipText(String.format("%s/%s", current, end));
    }

    @Override
    public void onComplete() {
        mImageView.setVisibility(View.VISIBLE);
        if (mHintText != null) {
            mHintText.setText("暂时没有直播信息");
        }
    }
}
