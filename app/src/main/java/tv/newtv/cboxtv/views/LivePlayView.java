package tv.newtv.cboxtv.views;

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

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.Navigation;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.model.LivePermissionCheckBean;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.utils.CmsLiveUtil;
import tv.newtv.cboxtv.utils.LivePermissionCheckUtil;
import tv.newtv.cboxtv.utils.LiveTimingUtil;
import tv.newtv.cboxtv.utils.PlayInfoUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         15:00
 * 创建人:           weihaichao
 * 创建日期:          2018/4/29
 */
public class LivePlayView extends RelativeLayout implements Navigation.NavigationChange {
    public static final int MODE_IMAGE = 1;
    public static final int MODE_OPEN_VIDEO = 2;
    public static final int MODE_LIVE = 3;
    private static final String TEST_M3U8 = "http://s003.test.vod06.icntvcdn" +
            ".com/live/sscntv63.m3u8";
    private static final String TimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final int STATE_NOT_STARTED = 0;
    private static final int STATE_PERMISSION = 1;
    private static final int STATE_PERMISSION_FAIL = 2;
    private static final int STATE_LIVING = 3;
    private static String TAG = "LivePlayView";
    private VideoFrameLayout mVideoPlayer;
    private RecycleImageView recycleImageView;
    //    private IcntvLive mIcntvLive;
    private VideoPlayerView mVideoPlayerView;
    private TextView centerTextView;
    private ImageView LoadingView;
    private Animation loadingAnimation;
    private ProgramSeriesInfo mProgramSeriesInfo;
    private PlayInfo mPlayInfo;
    private int currentMode = MODE_IMAGE;
    private ProgramInfo mProgramInfo;

    private boolean isPrepared = false;
    private boolean isFullScreen = false;

    private String mUUID;

    private int mIndex = 0;
    private int mPosition = 0;

    /**
     * 直播鉴权返回结果
     */
    private LivePermissionCheckBean livePermissionCheck;

    /**
     * 直播状态
     * 0.未开始 STATE_NOT_STARTED
     * 1.正在鉴权 STATE_PERMISSION
     * 2.鉴权失败 STATE_PERMISSION_FAIL
     * 3.正在播放 STATE_LIVING
     */
    private int liveState = STATE_NOT_STARTED;
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
                    PlayInfoUtil.getInfo(mPlayInfo.ContentUUID,mPlayInfo.contentType, new
                            PlayInfoUtil
                            .ProgramSeriesInfoCallback() {
                        @Override
                        public void onResult(final ProgramSeriesInfo info) {
                            if (info == null) return;
                            mProgramSeriesInfo = info;
                            MainLooper.get().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mVideoPlayerView != null) {
                                        mVideoPlayerView.setSeriesInfo(info);
                                        mVideoPlayerView.playSingleOrSeries(mIndex, mPosition);
                                    }
                                }
                            });
                        }
                    });
                }

            }
        }
    };
    private Runnable playLiveRunnable = new Runnable() {
        @Override
        public void run() {
            prepareVideoPlayer();
            if (mProgramSeriesInfo != null) {
                mVideoPlayerView.setSeriesInfo(mProgramSeriesInfo);
                mVideoPlayerView.playLiveVideo(mPlayInfo.ContentUUID, mPlayInfo.playUrl, mPlayInfo
                        .title, 0, 0);
            } else {
                PlayInfoUtil.getInfo(mPlayInfo.ContentUUID, mPlayInfo.contentType, new
                        PlayInfoUtil.ProgramSeriesInfoCallback() {
                            @Override
                            public void onResult(final ProgramSeriesInfo info) {
                                if (info == null) return;
                                mProgramSeriesInfo = info;
                                mProgramSeriesInfo.setLiveUrl(mProgramInfo.getPlayUrl());
                                mProgramSeriesInfo.setLiveLoopType(mProgramInfo.getLiveLoopType());
                                mProgramSeriesInfo.setLiveParam(mProgramInfo.getLiveParam());
                                mProgramSeriesInfo.setPlayStartTime(mProgramInfo.getPlayStartTime
                                        ());
                                mProgramSeriesInfo.setPlayEndTime(mProgramInfo.getPlayEndTime());
                                mProgramSeriesInfo.setIsTimeShift(mProgramInfo.getIsTimeShift());
                                MainLooper.get().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mVideoPlayerView != null) {
                                            mVideoPlayerView.setSeriesInfo(info);
                                            mVideoPlayerView.playLiveVideo(mPlayInfo.ContentUUID,
                                                    mPlayInfo.playUrl, mPlayInfo
                                                            .title, 0, 0);
                                        }
                                    }
                                });

                            }
                        });
            }
            timer();
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
            mVideoPlayerView = new VideoPlayerView(getContext());
            mVideoPlayerView.setSingleRepeat(true);
            mVideoPlayerView.setTag("videoPlayer");
            FrameLayout.LayoutParams layoutParams = null;
            layoutParams = new FrameLayout.LayoutParams
                    (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
                            .MATCH_PARENT);
            mVideoPlayerView.setLayoutParams(layoutParams);
            mVideoPlayer.addView(mVideoPlayerView, layoutParams);
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
            if (mVideoPlayerView != null && mVideoPlayer.findViewWithTag("videoPlayer") !=
                    null) {
                isFullScreen = mVideoPlayerView.isFullScreen();
                mVideoPlayerView.release();
                mVideoPlayerView.destory();
                removeView(mVideoPlayerView);
                mVideoPlayerView = null;
            } else {
                mVideoPlayerView = null;
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
    //进入全屏
    public void enterFullScreen() {
        Log.d(TAG, "enterFullScreen");
        if (isLive()) {
            Log.d(TAG, "直播中，特殊处理");
            if (Constant.OPEN_SPECIAL.equals(mPlayInfo.actionType)) {
                JumpUtil.activityJump(getContext(), mPlayInfo.actionType, mPlayInfo.contentType,
                        mPlayInfo.ContentUUID, mProgramInfo.getActionUri());
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
        if (visibility == View.GONE) {
            releaseVideoPlayer();
        } else if (visibility == View.VISIBLE) {
            if (currentMode == MODE_LIVE) {
                if (isLive() && livePermissionCheck != null) {
                    playLiveVideo();
                } else if (isLive()) {
                    startPlayPermissionsCheck(mProgramInfo);
                } else {
                    if (centerTextView != null) {
                        centerTextView.setVisibility(View.VISIBLE);
                        centerTextView.setText("暂无播放");
                        releaseVideoPlayer();
                    }
                }
            } else if (currentMode == MODE_OPEN_VIDEO) {
                playVideo();
            }
        }
    }

    private void init(AttributeSet attrs) {
        setClipChildren(false);
        setClipToPadding(false);

        if (mVideoPlayer == null) {
            mVideoPlayer = new VideoFrameLayout(getContext());
//            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
//                    .MATCH_PARENT);
//            mVideoPlayer.setLayoutParams(layoutParams);
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mVideoPlayer != null) {
            mVideoPlayer.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setPlayInfo(String actionType, String contentUUID, String playUrl) {
        mPlayInfo = new PlayInfo();
        mPlayInfo.actionType = actionType;
        mPlayInfo.playUrl = playUrl;
        mPlayInfo.ContentUUID = contentUUID;

        if (!mPlayInfo.isCanUse()) return;

        if (Constant.OPEN_VIDEO.equals(actionType)) {
            currentMode = MODE_OPEN_VIDEO;
            playVideo();
        } else if (!TextUtils.isEmpty(playUrl)) {
            currentMode = MODE_LIVE;
            playLiveVideo();
        } else {
            currentMode = MODE_IMAGE;
        }
    }

    public void setProgramInfo(ProgramInfo programInfo) {
        if (programInfo == null) return;
        this.mProgramInfo = programInfo;
        mPlayInfo = new PlayInfo();
        mPlayInfo.contentType = programInfo.getContentType();
        mPlayInfo.actionType = programInfo.getActionType();
        mPlayInfo.ContentUUID = programInfo.getContentUUID();
        mPlayInfo.playUrl = programInfo.getPlayUrl();
        mPlayInfo.title = programInfo.getTitle();

        if (!mPlayInfo.isCanUse()) return;

        //2代表视频
        if (mProgramInfo.getRecommendedType().equals("2")) {
            //如果有playurl并且在直播的时间段内，则判断是直播
            if (isLive()) {
                currentMode = MODE_LIVE;
                if (livePermissionCheck == null) {
                    playLiveVideo();
                } else {
                    startPlayPermissionsCheck(mProgramInfo);
                }
            } else if (Constant.OPEN_VIDEO.equals(mProgramInfo.getActionType())) {
                currentMode = MODE_OPEN_VIDEO;
                playVideo();
            } else {
                currentMode = MODE_IMAGE;
                releaseVideoPlayer();
            }
        } else {
            currentMode = MODE_IMAGE;
            releaseVideoPlayer();
        }
    }

    private void playVideo() {
        if (!Navigation.get().isCurrentPage(mUUID)) return;
        removeCallbacks(playRunnable);
        postDelayed(playRunnable, 2000);
    }

    private void playLiveVideo() {
        if (!Navigation.get().isCurrentPage(mUUID)) return;
        removeCallbacks(playLiveRunnable);
        postDelayed(playLiveRunnable, 2000);







    }

    private void startPlayPermissionsCheck(ProgramInfo programInfo) {
        if (!Navigation.get().isCurrentPage(mUUID)) return;
        if (liveState != STATE_NOT_STARTED && liveState != STATE_PERMISSION_FAIL) {
            return;
        }
        liveState = STATE_PERMISSION;

        LivePermissionCheckUtil.startPlayPermissionsCheck(LivePermissionCheckUtil
                        .createPlayCheckRequest(programInfo)
                , new LivePermissionCheckUtil.MyPermissionCheckListener() {
                    @Override
                    public void onSuccess(LivePermissionCheckBean result) {
                        livePermissionCheck = result;
                        playLiveVideo();
                    }

                    @Override
                    public void onFail() {
                        LogUtils.e("LivePermissionCheck onFail->");
                    }
                });
    }

    private boolean isLive() {
        return !TextUtils.isEmpty(mProgramInfo.getPlayUrl()) && CmsLiveUtil.isInPlay(
                mProgramInfo.getLiveLoopType(), mProgramInfo.getLiveParam(), mProgramInfo
                        .getPlayStartTime(), mProgramInfo.getPlayEndTime(), null);
    }

    private void timer() {
        LiveTimingUtil.endTime(mProgramInfo.getPlayEndTime(), new LiveTimingUtil.LiveEndListener() {
            @Override
            public void end() {
                if (centerTextView != null) {
                    centerTextView.setVisibility(View.VISIBLE);
                    centerTextView.setText("暂无播放");
                    releaseVideoPlayer();
                }
                if (getVisibility() == View.VISIBLE) {
                    NewTVLauncherPlayerViewManager.getInstance().release();
                }
            }
        });
    }

    @Override
    public void onChange(String uuid) {
        if (Navigation.get().isCurrentPage(mUUID)) {
            setVisibleChange(VISIBLE);
        } else {
            setVisibleChange(GONE);
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
}
