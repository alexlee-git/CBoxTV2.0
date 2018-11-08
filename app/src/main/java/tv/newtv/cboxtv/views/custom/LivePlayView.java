package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.bean.Video;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LiveTimingUtil;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.Navigation;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.listener.ScreenListener;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.player.view.VideoFrameLayout;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         15:00
 * 创建人:           weihaichao
 * 创建日期:          2018/4/29
 */
public class LivePlayView extends RelativeLayout implements Navigation.NavigationChange,
        ContentContract.View,LiveListener {
    public static final int MODE_IMAGE = 1;
    public static final int MODE_OPEN_VIDEO = 2;
    public static final int MODE_LIVE = 3;
    private static final String M3U8 = "http://s003.test.vod06.icntvcdn.com/live/sscntv63.m3u8";
    private static final String TimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static String TAG = "LivePlayView";
    private VideoFrameLayout mVideoPlayer;
    private RecycleImageView recycleImageView;
    private VideoPlayerView mVideoPlayerView;
    private TextView centerTextView;
    private ImageView LoadingView;
    private Animation loadingAnimation;
    private Content mProgramSeriesInfo;
    private PlayInfo mPlayInfo;
    private LiveInfo mLiveInfo;
    private int currentMode = MODE_IMAGE;
    private Program mProgramInfo;
    private TextView hintText;
    private NewTVLauncherPlayerView.PlayerViewConfig playerViewConfig;

    private ContentContract.Presenter mContentPresenter;

    private NewTVLauncherPlayerView.PlayerViewConfig mPlayerViewConfig;
    private String mUUID;

    private int mIndex = 0;
    private int mPosition = 0;


    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayInfo != null) {
                prepareVideoPlayer();
                if (mProgramSeriesInfo != null) {
                    if (mVideoPlayerView != null) {
                        mVideoPlayerView.setSeriesInfo(mProgramSeriesInfo);
                        mVideoPlayerView.playSingleOrSeries(mIndex, mPosition);
                    }
                } else {
                    mContentPresenter.getContent(mPlayInfo.ContentUUID, true);
                }
            }
        }
    };

    private Runnable playLiveRunnable = new Runnable() {
        @Override
        public void run() {
            prepareVideoPlayer();
            mVideoPlayerView.playLive(mLiveInfo, false,LivePlayView.this);
        }
    };

    public LivePlayView(@NonNull Context context) {
        this(context, null);
    }

    public LivePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LivePlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setUUID(String uuid) {
        mUUID = uuid;
    }


    private void prepareVideoPlayer() {
        if (mVideoPlayerView == null) {
            if (mPlayerViewConfig != null) {
                mVideoPlayerView = new VideoPlayerView(mPlayerViewConfig, getContext());
            } else {
                mVideoPlayerView = new VideoPlayerView(getContext());
                mVideoPlayerView.setSingleRepeat(true);
                mVideoPlayerView.setTag("videoPlayer");
                FrameLayout.LayoutParams layoutParams = null;
                layoutParams = new FrameLayout.LayoutParams
                        (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
                                .MATCH_PARENT);
                mVideoPlayerView.setLayoutParams(layoutParams);
                mVideoPlayer.addView(mVideoPlayerView, layoutParams);
                mVideoPlayerView.registerScreenListener(new MyScreenListener());
            }
        }
    }

    private void releaseVideoPlayer() {
        try {
            int index = NewTVLauncherPlayerViewManager.getInstance().getIndex();
            int position = NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition();

            if (index >= 0 && mProgramSeriesInfo != null && mProgramSeriesInfo.getData() != null &&
                    index < mProgramSeriesInfo.getData().size()) {
                mIndex = index;
            } else {
                mIndex = 0;
            }
            if (position >= 0) {
                mPosition = position;
            }
            removeCallbacks(playLiveRunnable);
            removeCallbacks(playRunnable);
            mVideoPlayer.getViewTreeObserver().removeOnGlobalLayoutListener(null);
            if (mVideoPlayerView != null) {
                mPlayerViewConfig = mVideoPlayerView.getDefaultConfig();
                mVideoPlayerView.release();
                mVideoPlayerView.destory();
                removeView(mVideoPlayerView);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            mVideoPlayerView = null;
            LiveTimingUtil.clearListener();
        }
    }

    public RecycleImageView getPosterImageView() {
        return recycleImageView;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof RecycleImageView) {
            recycleImageView = (RecycleImageView) child;
            super.addView(child, 0, params);
            return;
        }
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        if (child instanceof RecycleImageView) {
            recycleImageView = (RecycleImageView) child;
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        if (child instanceof RecycleImageView) {
            recycleImageView = (RecycleImageView) child;
        }
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        if (child instanceof RecycleImageView) {
            recycleImageView = (RecycleImageView) child;
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child instanceof RecycleImageView) {
            recycleImageView = (RecycleImageView) child;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Navigation.get().attach(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Navigation.get().detach(this);
    }

    public boolean isVideoType() {
        return currentMode != MODE_IMAGE;
    }

    public void dispatchClick() {
        Log.d(TAG, "enterFullScreen");
        if (CmsUtil.isLive(mProgramInfo.getVideo()) != null) {
            Log.d(TAG, "直播中，特殊处理");
            if (Constant.OPEN_SPECIAL.equals(mPlayInfo.actionType)) {
                JumpUtil.activityJump(getContext(), mPlayInfo.actionType, mPlayInfo.contentType,
                        mPlayInfo.ContentUUID, mProgramInfo.getL_actionUri());
                return;
            }
        }
        if (mVideoPlayerView != null) {
            mVideoPlayerView.EnterFullScreen(MainNavManager.getInstance().getCurrentFragment()
                    .getActivity(), true);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        setVisibleChange(visibility);
    }

    private void setVisibleChange(int visibility) {
        if (mPlayInfo == null) return;
        if (visibility == View.GONE) {
            releaseVideoPlayer();
        } else if (visibility == View.VISIBLE) {
            if (currentMode == MODE_LIVE) {
                if (CmsUtil.isLive(mProgramInfo.getVideo()) != null) {
                    playLiveVideo(0);
                } else {
                    if (centerTextView != null) {
                        centerTextView.setVisibility(View.VISIBLE);
                        centerTextView.setText("暂无播放");
                        releaseVideoPlayer();
                    }
                }
            } else if (currentMode == MODE_OPEN_VIDEO) {
                playVideo(0);
            }
        }
    }

    private void init(AttributeSet attrs) {
        setClipChildren(false);
        setClipToPadding(false);

        if (mVideoPlayer == null) {
            mVideoPlayer = new VideoFrameLayout(getContext());
            addView(mVideoPlayer, 0);
            SurfaceView surfaceView = new SurfaceView(getContext());
            FrameLayout.LayoutParams frame = new FrameLayout.LayoutParams(0, 0);
            surfaceView.setLayoutParams(frame);
            mVideoPlayer.addView(surfaceView, frame);
        }

        if (LoadingView == null) {
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.width_40px);
            LoadingView = new ImageView(getContext());
            LayoutParams layoutParams = new LayoutParams(size, size);
            LoadingView.setScaleType(ImageView.ScaleType.FIT_XY);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            LoadingView.setLayoutParams(layoutParams);
            LoadingView.setImageResource(R.drawable.player_loading_drawable);
            addView(LoadingView, layoutParams);
            LoadingView.setVisibility(View.GONE);
        }

        if (loadingAnimation == null) {
            loadingAnimation = AnimationUtils.loadAnimation(LauncherApplication
                    .AppContext, R.anim.rotate_animation);
        }

        if (centerTextView == null) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            centerTextView = new TextView(getContext());
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen
                    .width_5px);
            centerTextView.setPadding(padding, padding, padding, padding);
            centerTextView.setTextSize(getContext().getResources().getDimension(R.dimen
                    .height_34px));
            centerTextView.setTextColor(Color.WHITE);
            centerTextView.setLayoutParams(layoutParams);
            addView(centerTextView, layoutParams);
        }

        if(hintText == null){
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            hintText = new TextView(getContext());
            int padding = getContext().getResources().getDimensionPixelOffset(R.dimen
                    .width_5px);
            hintText.setPadding(padding, padding, padding, padding);
            hintText.setTextSize(getContext().getResources().getDimension(R.dimen
                    .height_12px));
            hintText.setTextColor(Color.WHITE);
            hintText.setLayoutParams(layoutParams);
            addView(hintText, layoutParams);
        }

        mContentPresenter = new ContentContract.ContentPresenter(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mVideoPlayer != null) {
            mVideoPlayer.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setProgramInfo(Program programInfo) {
        if (programInfo == null) return;
        this.mProgramInfo = programInfo;
        mPlayInfo = new PlayInfo();
        mPlayInfo.contentType = programInfo.getContentType();
        mPlayInfo.actionType = programInfo.getL_actionType();
        if (programInfo.getVideo() != null) {
            mPlayInfo.ContentUUID = programInfo.getVideo().getContentId();
            mPlayInfo.playUrl = programInfo.getVideo().getLiveUrl();
        }
        mPlayInfo.title = programInfo.getTitle();

        if (!mPlayInfo.isCanUse()) return;

        //2代表视频
        if (mProgramInfo.getRecommendedType().equals("2")) {
            //如果有playurl并且在直播的时间段内，则判断是直播
            LiveInfo liveInfo = new LiveInfo(mProgramInfo.getTitle(), mProgramInfo.getVideo());
            if (liveInfo.isLiveTime()) {
                mLiveInfo = liveInfo;
                currentMode = MODE_LIVE;
                playLiveVideo(2000);
                return;
            } else if (isVod()) {
                currentMode = MODE_OPEN_VIDEO;
                playVideo(2000);
                return;
            }
        }
        currentMode = MODE_IMAGE;
        releaseVideoPlayer();
    }

    private void playVideo(int delay) {
        if (!Navigation.get().isCurrentPage(mUUID)) return;
        removeCallbacks(playRunnable);
        postDelayed(playRunnable, playerViewConfig != null && playerViewConfig.isFullScreen ? 0 :
                2000);
    }

    private void playLiveVideo(int delay) {
        if (!Navigation.get().isCurrentPage(mUUID)) return;
        removeCallbacks(playLiveRunnable);
        postDelayed(playLiveRunnable, playerViewConfig != null && playerViewConfig.isFullScreen ?
                0 :
                2000);
    }


    private boolean isVod() {
        if (mProgramInfo == null) return false;
        Video video = mProgramInfo.getVideo();
        if (video != null) {
            if ("VIDEO".equals(video.getVideoType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onChange(String uuid) {
        if (Navigation.get().isCurrentPage(mUUID)) {
            setVisibleChange(VISIBLE);
        } else {
            setVisibleChange(GONE);
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable Content content) {
        mProgramSeriesInfo = content;
        if (mVideoPlayerView != null) {
            mVideoPlayerView.setSeriesInfo(mProgramSeriesInfo);
            mVideoPlayerView.playSingleOrSeries(mIndex, mPosition);
        }
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }

    @Override
    public void onTimeChange(String current, String end) {
        mVideoPlayerView.setTipText(String.format("%s/%s",current,end));
    }

    @Override
    public void onComplete() {

    }

    public void setHintText(String message){
        if(hintText != null){
            hintText.setVisibility(View.VISIBLE);
            hintText.setText(message);
        }
    }

    private static class PlayInfo {
        String actionType;
        String playUrl;
        String ContentUUID;
        String title;
        String contentType;

        public boolean isCanUse() {
            return !TextUtils.isEmpty(actionType) && !TextUtils.isEmpty(ContentUUID);
        }
    }

    private class MyScreenListener implements ScreenListener {

        @Override
        public void enterFullScreen() {

        }

        @Override
        public void exitFullScreen() {
            if (getParent() != null && getParent() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = viewGroup.getChildAt(i);
                    if (child instanceof AutoSizeTextView) {
                        child.bringToFront();
                    }
                }
            }
        }
    }
}
