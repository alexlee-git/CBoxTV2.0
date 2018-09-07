package tv.newtv.cboxtv.player.videoview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;

/**
 * Created by gaoleichao on 2018/4/10.
 */

public class VideoPlayerView extends NewTVLauncherPlayerView {
    private static final String TAG = "VideoPlayerView";

    private ImageView detailPlayIv;
    private ProgressBar mPlayerProgress;
    private VPlayCenter playCenter;
    private PlayerCallback mPlayerCallback;
    private View mFocusView;
    private boolean repeatPlay = false;
    private TextView HintTextView;

    private View defaultFocusView;
    private boolean KeyIsDown = false;

    public VideoPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr, PlayerViewConfig config) {
        super(context, attrs, defStyleAttr, config);
    }


    public VideoPlayerView(PlayerViewConfig config, Context context) {
        this(context, null, 0, config);
    }

    //    private boolean isFullScrenn = false;

    @Override
    public void updateDefaultConfig(PlayerViewConfig config) {
        super.updateDefaultConfig(config);

        if (config != null) {
            defaultFocusView = config.defaultFocusView;
            mPlayerCallback = config.playerCallback;
            playCenter = config.playCenter;
        }
    }

    @Override
    protected boolean NeedRepeat() {
        return repeatPlay;
    }

    @Override
    protected void onError(String code, String messgae) {
        super.onError(code, messgae);
        setHintText(String.format("%s 错误码:%s", messgae, code));
    }

    @Override
    public void ExitFullScreen() {
        if (defaultFocusView != null) {
            if (defaultFocusView instanceof VideoPlayerView) {
                VideoPlayerView.this.requestFocus();
            } else {
                defaultFocusView.requestFocus();
            }
            defaultFocusView = null;
        }
        super.ExitFullScreen();
        if (ProgramIsChange) {
            if (mPlayerCallback != null) {
                mPlayerCallback.ProgramChange();
            }
        }
        ProgramIsChange = false;

    }

    public void setSingleRepeat(boolean value) {
        repeatPlay = value;
    }

    @Override
    public void updateUIPropertys(boolean isFullScreen) {
        super.updateUIPropertys(isFullScreen);

        if (HintTextView != null) {
            HintTextView.setTextSize(getResources().getDimensionPixelSize(!isFullScreen ? R.dimen
                    .height_12px : R.dimen.height_18px));
        }
    }

    public void setFocusView(View view, boolean autoLayout) {
        mFocusView = view;
    }

    public void enterFullScreen(final Activity activity, boolean isLive) {
        ProgramSeriesInfo programSeriesInfo = playCenter.getCurrentProgramSeriesInfo();

        if (isLive && programSeriesInfo != null) {
            NewTVLauncherPlayerViewManager.getInstance().playLive(programSeriesInfo.getPlayUrl(),
                    activity, programSeriesInfo, 0, 0);
        } else {
            EnterFullScreen(activity, false);
        }
    }

    @Override
    public PlayerViewConfig getDefaultConfig() {
        PlayerViewConfig playerViewConfig = super.getDefaultConfig();
        playerViewConfig.defaultFocusView = defaultFocusView;
        playerViewConfig.playerCallback = mPlayerCallback;
        playerViewConfig.playCenter = playCenter;
        return playerViewConfig;
    }

    @Override
    public void EnterFullScreen(Activity activity, boolean bringFront) {
        defaultFocusView = activity.getWindow().getDecorView().findFocus();
        super.EnterFullScreen(activity, bringFront);
    }

    public void onComplete() {
        //release();
        if (detailPlayIv != null) {
            detailPlayIv.setVisibility(GONE);
        }
        if (mPlayerProgress != null) {
            mPlayerProgress.setVisibility(GONE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!isFullScreen()) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (KeyIsDown) {
                        KeyIsDown = false;
                        if (mPlayerCallback != null)
                            mPlayerCallback.onPlayerClick(VideoPlayerView.this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    KeyIsDown = true;
                }
            }

            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void initView(Context context) {
        startIsFullScreen = false;
        setFocusable(true);

        HintTextView = new TextView(getContext());
        HintTextView.setBackgroundResource(R.drawable.normalplayer_bg);
        HintTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.height_18px));
        HintTextView.setTextColor(Color.WHITE);
        HintTextView.setGravity(Gravity.CENTER);
        HintTextView.setText("播放结束");
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        HintTextView.setVisibility(View.INVISIBLE);
        layoutParams.gravity = Gravity.CENTER;
        HintTextView.setLayoutParams(layoutParams);

        super.initView(context);
        addView(HintTextView, layoutParams);

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!isFullScreen()) {
                    resetLayoutParams(hasFocus);
                }

            }
        });

        if (playCenter == null) {
            playCenter = new VPlayCenter();
        }
    }

    /**
     * 重置 VideoPlayerVIew 布局属性
     *
     * @param hasFocus
     */
    private void resetLayoutParams(boolean hasFocus) {

        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            if (hasFocus) {
                parent.setBackgroundResource(R.drawable.pos_zui);
            } else {
                parent.setBackground(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null && layoutParams instanceof MarginLayoutParams) {
            ((MarginLayoutParams) layoutParams).leftMargin = 0;
            ((MarginLayoutParams) layoutParams).topMargin = 0;
            ((MarginLayoutParams) layoutParams).rightMargin = 0;
            ((MarginLayoutParams) layoutParams).bottomMargin = 0;
            setLayoutParams(layoutParams);
        }
    }

    public void setSeriesInfo(ProgramSeriesInfo seriesInfo) {
        if (playCenter != null) {
            playCenter.addSeriesInfo(seriesInfo);
        }
    }

    public void playSingleOrSeries(int mIndex, int position) {
        playCenter.setCurrentIndex(mIndex);
        setHintTextVisible(GONE);
        VPlayCenter.DataStruct dataStruct = playCenter.getDataStruct();
        if (dataStruct.playType == VPlayCenter.PLAY_SERIES) {
            NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(getContext(),
                    playCenter.getCurrentSeriesInfo(), false, mIndex, position);
        } else {
            NewTVLauncherPlayerViewManager.getInstance().playProgramSingle(getContext(),
                    playCenter.getCurrentSeriesInfo(), position, false);
        }
    }

    public void setHintText(String text) {
        if (HintTextView != null) {
            HintTextView.setVisibility(View.VISIBLE);
            HintTextView.setText(text);
        }
    }

    public void setHintTextVisible(int visible) {
        if (HintTextView != null) {
            HintTextView.setVisibility(visible);
        }
    }

    public void playLiveVideo(String contentUUID, final String playUrl, final String title, final
    int index, final int position) {
        setHintTextVisible(GONE);
//        NewTVLauncherPlayerViewManager.getInstance().playLive(playUrl, getContext(),
//                getProgramSeriesInfo(), index, position);
//        playLive(playUrl,playCenter.getCurrentSeriesInfo(),false,index,position);
        NewTVLauncherPlayerViewManager.getInstance().playLive(playUrl, getContext(), playCenter
                .getCurrentSeriesInfo(), false, index, position);
    }

    public void showProgramError() {

    }

    public void beginChange() {
//        setPlayerMessage("正在切换视频...", 0);
        stopPlay();
    }

    public boolean isReady() {
        return playCenter != null && playCenter.isReady();
    }


    private void dispatchComplete(final boolean isError, final String desc) {
        MainLooper.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayerCallback != null) {
                    mPlayerCallback.AllPlayComplete(isError, desc, VideoPlayerView.this);
                }
            }
        }, 1000);
    }

    public void destory() {
        super.destroy();

        removeAllViews();

        detailPlayIv = null;
        mPlayerProgress = null;
        playCenter = null;
        mPlayerCallback = null;
        mFocusView = null;

        if (NewTVLauncherPlayerViewManager.getInstance().equalsPlayer(this)) {
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
    }

    @Override
    protected void playIndex(int index) {
        super.playIndex(index);

        if (mPlayerCallback != null && !ProgramIsChange) {
            mPlayerCallback.onEpisodeChange(index, getCurrentPosition());
        }
    }

    @Override
    protected void AllComplete(boolean isError, String info) {
        super.AllComplete(isError, info);

        if (NeedRepeat()) {
            playSingleOrSeries(getIndex(), 0);
            return;
        }

        if (isFullScreen()) {
            ExitFullScreen();
        }

        stopPlay();
        setHintText("播放已结束");

        if (mPlayerCallback != null) {
            mPlayerCallback.AllPlayComplete(isError, info, this);
        }
    }

    public void stopPlay() {
        super.stop();
    }

    public void startOrPause() {

    }

    public boolean hasNext(int index) {
        return playCenter.hasNext(index);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setPlayerCallback(PlayerCallback callback) {
        mPlayerCallback = callback;
    }

    public String getCurrentUuId() {
        return playCenter.getDataStruct().uuid;
    }

}
