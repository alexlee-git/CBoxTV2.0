package tv.newtv.cboxtv.player.videoview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.MainLooper;
import com.newtv.libs.util.LogUtils;

import tv.newtv.cboxtv.player.PlayerErrorCode;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.player.R;

/**
 * Created by gaoleichao on 2018/4/10.
 * 视频播放器
 */

public class VideoPlayerView extends NewTVLauncherPlayerView {
    private static final String TAG = "VideoPlayerView";

    private ImageView detailPlayIv;
    private ProgressBar mPlayerProgress;
    private PlayerCallback mPlayerCallback;
    private ExitVideoFullCallBack videoFullCallBack;
    private View mFocusView;
    private boolean repeatPlay = false;
    private TextView HintTextView;
    private TextView TipTextView;

    private View defaultFocusView;
    private boolean KeyIsDown = false;
    private ImageView isPlaying;

    private VideoExitFullScreenCallBack videoExitFullScreenCallBack;

    private Boolean mOuterControl = false;
    private TextView videoTitle;
    private ImageView full_screen;
    private boolean isEnd;



    public void destory() {
        super.destroy();

        removeAllViews();
        defaultFocusView = null;
        detailPlayIv = null;
        mPlayerProgress = null;
        mPlayerCallback = null;
        videoFullCallBack = null;
        mFocusView = null;
        videoExitFullScreenCallBack = null;

        if (NewTVLauncherPlayerViewManager.getInstance().equalsPlayer(this)) {
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
    }

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

    @Override
    public void updateDefaultConfig(PlayerViewConfig config) {
        super.updateDefaultConfig(config);

        if (config != null) {
            defaultFocusView = config.defaultFocusView;
            mPlayerCallback = config.playerCallback;
            videoFullCallBack = config.videoFullCallBack;
            videoExitFullScreenCallBack = config.videoExitFullScreenCallBack;
        }
    }

    @Override
    protected boolean NeedRepeat() {
        return repeatPlay;
    }

    @Override
    protected void onError(String code, String messgae) {
        super.onError(code, messgae);
        String hint = null;
        switch (code) {
            case PlayerErrorCode.USER_NOT_LOGIN:
            case PlayerErrorCode.USER_TOKEN_IS_EXPIRED:
            case PlayerErrorCode.USER_NOT_BUY:
                hint = "付费内容需购买后才能观看";
                break;
            default:
                hint = String.format("%s 错误码:%s", messgae, code);
                break;
        }
        setHintText(hint);
    }

    //退出全屏
    @Override
    public void ExitFullScreen() {
        if (defaultFocusView != null) {
            if (defaultFocusView instanceof VideoPlayerView) {
                VideoPlayerView.this.requestFocus();
                if (videoTitle != null)
                    videoTitle.setVisibility(VISIBLE);
                if (full_screen != null)
                    full_screen.setVisibility(VISIBLE);
            } else {
                defaultFocusView.requestFocus();
            }
            defaultFocusView = null;
        }
        super.ExitFullScreen();

        if (defaultConfig.ProgramIsChange) {
            if (mPlayerCallback != null) {
                mPlayerCallback.ProgramChange();
            }
        }
        defaultConfig.ProgramIsChange = false;


        Log.i("Collection", "退出全屏");

        if (videoExitFullScreenCallBack != null) {
            videoExitFullScreenCallBack.videoEitFullScreen();
        }
    }

    public void setSingleRepeat(boolean value) {
        repeatPlay = value;
    }

    @Override
    public void updateUIPropertys(boolean isFullScreen) {
        super.updateUIPropertys(isFullScreen);

        if (HintTextView != null) {
            HintTextView.setTextSize(getResources().getDimensionPixelSize(!isFullScreen ? R.dimen
                    .height_12sp : R.dimen.height_18sp));
        }
    }

    public void setFocusView(View view, boolean autoLayout) {
        mFocusView = view;
    }

    public void enterFullScreen(final Activity activity) {
        if (!defaultConfig.playCenter.isReady() && !isLiving()) {
            return;
        }
        EnterFullScreen(activity, false);
    }

    @Override
    public PlayerViewConfig getDefaultConfig() {
        PlayerViewConfig playerViewConfig = super.getDefaultConfig();
        if(playerViewConfig != null) {
            playerViewConfig.defaultFocusView = defaultFocusView;
            playerViewConfig.playerCallback = mPlayerCallback;
            playerViewConfig.videoFullCallBack = videoFullCallBack;
            playerViewConfig.videoExitFullScreenCallBack = videoExitFullScreenCallBack;

            LogUtils.d(TAG, playerViewConfig.toString());
        }
        return playerViewConfig;
    }

    public void setTipText(String message) {
        if (TipTextView != null) {
            TipTextView.setVisibility(VISIBLE);
            TipTextView.setText(message);
        }
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

    public boolean showFloatWindow(){
        //获取WindowManager
        WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService
                (Context.WINDOW_SERVICE);
        if(wm == null) return false;
        //设置LayoutParams(全局变量）相关参数
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        wmParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = getContext().getResources().getDimensionPixelSize(R.dimen.width_816px);
        wmParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.height_480px);

        wm.addView(this,wmParams);
        return true;
    }

    @Override
    protected void initView(Context context) {
        defaultConfig.startIsFullScreen = false;
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

        TipTextView = new TextView(getContext());
        TipTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.height_12px));
        TipTextView.setTextColor(Color.WHITE);
        TipTextView.setGravity(Gravity.CENTER);
        LayoutParams tiplayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);
        TipTextView.setVisibility(View.INVISIBLE);
        tiplayoutParams.rightMargin = 10;
        tiplayoutParams.bottomMargin = 10;
        tiplayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        TipTextView.setLayoutParams(tiplayoutParams);

        addView(TipTextView, tiplayoutParams);

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!isFullScreen()) {
                    resetLayoutParams(hasFocus);
                }

            }
        });
    }

    /**
     * 重置 VideoPlayerVIew 布局属性
     *
     * @param hasFocus
     */
    @SuppressWarnings("PointlessNullCheck")
    private void resetLayoutParams(boolean hasFocus) {

        if(getParent() instanceof ViewGroup) {
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

    public void showProgramError() {

    }

    public void beginChange() {
//        setPlayerMessage("正在切换视频...", 0);
        if (isPlaying()) {
            stopPlay();
        }
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

    @Override
    protected void playIndex(int index) {
        super.playIndex(index);
        if (mPlayerCallback != null && !defaultConfig.ProgramIsChange) {
            int toIndex = CmsUtil.translateIndex(defaultConfig.playCenter.getCurrentSeriesInfo(),
                    index);
            mPlayerCallback.onEpisodeChange(toIndex, getCurrentPosition());
        }
    }

    @Override
    protected void AllComplete(boolean isError, String info) {
        super.AllComplete(isError, info);

        if (NeedRepeat()) {
            playSingleOrSeries(getIndex(), 0);
            return;
        }

        if (mOuterControl) {
            if (isEnd) {
                if (isFullScreen()) {
                    ExitFullScreen();
                }
                stopPlay();
                setHintText("播放已结束");
                Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .play_complete),
                        Toast.LENGTH_SHORT).show();
                isEnd = false;
            }
        } else {
            //当视频都播放完的时候，就不在去加载首次进入详情页播放的视频
            defaultConfig.ProgramIsChange = false;
            if (isFullScreen()) {
                ExitFullScreen();
            }
            stopPlay();
            setHintText("播放已结束");
        }

        if (isPlaying != null) {
            isPlaying.setVisibility(GONE);
        }

        if (mPlayerCallback != null) {
            mPlayerCallback.AllPlayComplete(isError, info, this);
        }
    }

    @Override
    public void onChange(String current, String start, String end, boolean isComplete) {
        super.onChange(current, start, end, isComplete);

        if (isComplete) {
            setHintText("直播已结束");
        }
    }

    public void stopPlay() {
        super.stop();
    }

    public void startOrPause() {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setPlayerCallback(PlayerCallback callback) {
        mPlayerCallback = callback;
    }

    public void setVideoExitCallback(VideoExitFullScreenCallBack callback) {
        videoExitFullScreenCallBack = callback;
    }

    public void setView(TextView videoTitle, ImageView full_screen) {
        this.videoTitle = videoTitle;
        this.full_screen = full_screen;
    }

    @Override
    public void playAlternate(String alternateId, String title, String channelId) {
        super.playAlternate(alternateId, title, channelId);
        outerControl();
    }

    public void outerControl() {
        this.mOuterControl = true;
    }

    public void setisEnd(boolean b) {
        this.isEnd = b;
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }
}
