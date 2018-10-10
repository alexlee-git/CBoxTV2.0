package tv.newtv.cboxtv.player.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.NewTVLauncherPlayer;
import tv.newtv.cboxtv.player.PlayerTimeUtils;
import tv.newtv.cboxtv.player.model.LiveInfo;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerSeekbar extends FrameLayout implements SeekBar
        .OnSeekBarChangeListener {
    private static final String TAG = NewTVLauncherPlayerSeekbar.class.getName();
    private static final int SEEK_TO = 2000;
    private static final int DISMISS_VIEW = 2001;
    private static final int REFRESH_CURRENTTIME_AND_PROGRESS = 2002;
    private static final int REFRESH_LEFTTIME = 2003;
    private static final long SEEK_TO_DELAY_TIME = 500;

    private static final long DISMISS_VIEW_DELAY_TIME = 5000;
    private static final int SPLIT_DURATION = 100;
    private static final int DEFAULT_SEEK_STEP = 10;
    private static final long REFRESH_CURRENTTIME_AND_PROGRESS_DELAY_TIME = 1000;
    private SeekBar mSeekBar;
    private SeekBarAreaHandler mHandler;
    private AnimationSet mAnimationIn, mAnimationOut;
    private ImageView imageHint;
    private boolean mIsOnlyShowSeekBar = true; //判断是否只摁了一下遥控呼出进度条，true则不seek，false则seek

    private TextView mProgramNameTextView;

    @SuppressWarnings("unused")
    private TextView mLeftTimeTextView, mRightTimeTextView, mCurrentTimeTextView;

    @SuppressWarnings("unused")
    private FrameLayout mSeekBarDownArrowArea;


    private int mSeekStep = DEFAULT_SEEK_STEP;

    private int mSeekBarRealWidth; //进度条实际大小，用以确定快进快退时间
    private int mSeekCount;
    private NewTVLauncherPlayer mNewTVLauncherPlayer;
    private LiveInfo mLiveInfo;
    private ImageView mImgSeekStatus;
    private int currentMinSen;

    @SuppressWarnings("unused")
    private boolean seekSlide = false;//是否正在快进快退

    private int position;
    private boolean seekToEnd = false;
    private Context mContext;


    public NewTVLauncherPlayerSeekbar(@NonNull Context context) {
        this(context, null);
    }

    public NewTVLauncherPlayerSeekbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTVLauncherPlayerSeekbar(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext= context;
        initView(context);
        initData(context);
    }

    public void release() {
        Log.i(TAG, "release: ");
        mImgSeekStatus = null;
        mSeekBarDownArrowArea = null;
        mLeftTimeTextView = null;
        mRightTimeTextView = null;
        mCurrentTimeTextView = null;
        mProgramNameTextView = null;
        mAnimationIn = null;
        mAnimationOut = null;
        mLiveInfo = null;
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(null);
            mSeekBar = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.release();
            mNewTVLauncherPlayer = null;
        }

    }

    public void setLiveInfo(LiveInfo liveInfo) {
        mLiveInfo = liveInfo;
    }

    public void setmNewTVLauncherPlayer(NewTVLauncherPlayer mNewTVLauncherPlayer) {
        this.mNewTVLauncherPlayer = mNewTVLauncherPlayer;
    }

    private void initData(Context context) {

        mHandler = new SeekBarAreaHandler(this);

        mAnimationIn = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.seekbar_in);
        mAnimationOut = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.seekbar_out);
    }

    private void initView(Context context) {
        Log.i(TAG, "initView: ");
        View view = LayoutInflater.from(context).inflate(R.layout.newtv_launcher_player_seekbar,
                this);
        mSeekBar = (SeekBar) view.findViewById(R.id.player_seekbar);
        BitmapDrawable newDrawable = getNewDrawable(R.drawable.seekbar_thumb, 4, 40);
        mSeekBar.setThumb(newDrawable);
        mSeekBar.setThumbOffset(0);
        mProgramNameTextView = (TextView) view.findViewById(R.id.seekbar_program_name);
        mLeftTimeTextView = (TextView) view.findViewById(R.id.seebar_left_time);
        mRightTimeTextView = (TextView) view.findViewById(R.id.seebar_right_time);

        mSeekBarDownArrowArea = (FrameLayout) view.findViewById(R.id.seekbar_down_arrow_area);
        mImgSeekStatus = (ImageView) view.findViewById(R.id.img_seek_status);
        imageHint = view.findViewById(R.id.image_hint);
        checkImageHint();
    }

    private void initseekbar() {
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mNewTVLauncherPlayer.getDuration() / SPLIT_DURATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mSeekBarRealWidth = mSeekBar.getWidth() - mSeekBar.getPaddingStart() - mSeekBar
                    .getPaddingEnd() - 20;
        } else {
            mSeekBarRealWidth = mSeekBar.getWidth() - mSeekBar.getPaddingLeft() - mSeekBar
                    .getPaddingRight() - 20;
        }
        Log.i(TAG, "initseekbar: mSeekBar.getMax=" + mSeekBar.getMax() + " mSeekBarRealWidth=" +
                mSeekBarRealWidth);
        mHandler.sendEmptyMessage(REFRESH_CURRENTTIME_AND_PROGRESS);
        mHandler.sendEmptyMessage(REFRESH_LEFTTIME);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent
                    .KEYCODE_DPAD_RIGHT) {
                mIsOnlyShowSeekBar = false;
                seekSlide = true;
                mSeekCount++;
                if (mSeekCount > 10 && mSeekCount <= 40) {
                    mSeekStep = DEFAULT_SEEK_STEP * 8;//2 8
                } else if (mSeekCount > 40 && mSeekCount < 80) {
                    mSeekStep = DEFAULT_SEEK_STEP * 16;//4 16
                } else {
                    mSeekStep = DEFAULT_SEEK_STEP * 32;//8 32
                }
                mHandler.removeMessages(SEEK_TO);
                mHandler.removeMessages(REFRESH_CURRENTTIME_AND_PROGRESS);
                mHandler.removeMessages(DISMISS_VIEW);

                position = 0;
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    position = mSeekBar.getProgress() - mSeekStep;
                    mImgSeekStatus.setImageResource(R.drawable.seek_fast_back);
                } else {
                    position = mSeekBar.getProgress() + mSeekStep;
                    mImgSeekStatus.setImageResource(R.drawable.seek_fast_forward);
                    if (mLiveInfo != null) {
                        int seekPos = Math.round(mLiveInfo.getCurrentRealPosition() /
                                SPLIT_DURATION);
                        if (seekPos <= position) {
                            seekToEnd = true;
                            return true;
                        }
                    }
                }
                currentMinSen = mNewTVLauncherPlayer.getDuration() / mSeekBar.getMax() *
                        position;
                if (currentMinSen < 0) currentMinSen = 0;
                if (currentMinSen > mNewTVLauncherPlayer.getDuration())
                    currentMinSen = mNewTVLauncherPlayer.getDuration();
                String format = PlayerTimeUtils.getInstance().timeFormat(currentMinSen);
                mLeftTimeTextView.setText(format);
                if (position > mSeekBar.getMax()) position = mSeekBar.getMax();
                mSeekBar.setProgress(position);
                return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent
                    .KEYCODE_DPAD_RIGHT) {
                if(!seekSlide) return true;
                seekSlide = false;
                mImgSeekStatus.setImageResource(R.drawable.seek_pause);
                if (!mIsOnlyShowSeekBar) {
                    mHandler.sendEmptyMessageDelayed(SEEK_TO, SEEK_TO_DELAY_TIME);
                    mHandler.sendEmptyMessageDelayed(REFRESH_CURRENTTIME_AND_PROGRESS,
                            REFRESH_CURRENTTIME_AND_PROGRESS_DELAY_TIME);
                    mHandler.sendEmptyMessageDelayed(DISMISS_VIEW, DISMISS_VIEW_DELAY_TIME);
                }
                mSeekCount = 0;
                mSeekStep = DEFAULT_SEEK_STEP;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setDuration() {
        Log.i(TAG, "setDuration: ");
        int duration = mNewTVLauncherPlayer.getDuration();
        String durationString = PlayerTimeUtils.getInstance().timeFormat(duration);
        mRightTimeTextView.setText(durationString);
        initseekbar();
        show();
    }

    public void setProgramName(String programName, boolean isNeedDownArrow) {
        Log.i(TAG, "setProgramName: ");
        mProgramNameTextView.setText(programName);
    }

    private void refreshLeftTime() {
        if (!seekSlide) {
            int currentTime = mNewTVLauncherPlayer.getCurrentPosition();
            String currentTimeString = PlayerTimeUtils.getInstance().timeFormat(currentTime);
            mLeftTimeTextView.setText(currentTimeString);
        }

        mHandler.sendEmptyMessageDelayed(REFRESH_LEFTTIME,
                REFRESH_CURRENTTIME_AND_PROGRESS_DELAY_TIME);
    }

    /*
     * 定时刷新进度条进度和时间
     */
    private void refreshTimeAndProgress() {
        int duration = mNewTVLauncherPlayer.getDuration();
        int currentPosition = mNewTVLauncherPlayer.getCurrentPosition();
        saveCurrentPosition(currentPosition);
        int progress = 0;
        if (duration != 0) {
            progress = (int) (currentPosition * 1.00000f * mSeekBar.getMax() / duration);
        }
        if (mSeekBar != null) {
            if (getVisibility() == View.VISIBLE) {
                mSeekBar.setProgress(progress);
            } else {
                mSeekBar.setProgress(progress);

            }
        }
        mHandler.sendEmptyMessageDelayed(REFRESH_CURRENTTIME_AND_PROGRESS,
                REFRESH_CURRENTTIME_AND_PROGRESS_DELAY_TIME);
    }

    private void saveCurrentPosition(int currentPosition) {
        SharedPreferences sp = mContext.getSharedPreferences("positionConfig", Context.MODE_PRIVATE);
        sp.edit().putInt("position",currentPosition).commit();

    }


    public void show() {
        if (NewTVLauncherPlayerViewManager.getInstance().getShowView() != NewTVLauncherPlayerView
                .SHOWING_NO_VIEW) {
            return;
        }
        Log.i(TAG, "show: ");
        if (mNewTVLauncherPlayer.getDuration() <= 0) {
            return;
        }
        mIsOnlyShowSeekBar = true;
        if (getVisibility() == View.VISIBLE) {
            mHandler.removeMessages(DISMISS_VIEW);
            mHandler.sendEmptyMessageDelayed(DISMISS_VIEW, DISMISS_VIEW_DELAY_TIME);
            return;
        }
        setVisibility(View.VISIBLE);
        bringToFront();
        startAnimation(mAnimationIn);
        mHandler.sendEmptyMessageDelayed(DISMISS_VIEW, DISMISS_VIEW_DELAY_TIME);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView
                .SHOWING_SEEKBAR_VIEW);
        checkImageHint();
    }

    public void showPauseIcon() {
        Log.i(TAG, "pauseShowIcon: ");
        if (mNewTVLauncherPlayer.getDuration() <= 0) {
            return;
        }
        if (getVisibility() == View.VISIBLE) {
            mHandler.removeMessages(DISMISS_VIEW);
            return;
        }

        setVisibility(View.VISIBLE);
        bringToFront();
        show();
        startAnimation(mAnimationIn);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView
                .SHOWING_SEEKBAR_VIEW);
    }

    public void hidePauseIcon() {
        dismiss();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if (visibility == VISIBLE) {

        } else if (visibility == GONE) {

        }
    }

    public void dismiss() {
        Log.i(TAG, "dismiss: ");
        mHandler.removeMessages(DISMISS_VIEW);
        setVisibility(View.INVISIBLE);
        startAnimation(mAnimationOut);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView
                .SHOWING_NO_VIEW);
    }


    private void refreshCurrentTimeTextView(int progress) {
//        add by lxf 注释
//        int duration = mNewTVLauncherPlayer.getDuration();
//        int currentTime = duration / mSeekBar.getMax() * progress;
//        mCurrentTimeTextView.setText(PlayerTimeUtils.getInstance().timeFormat(currentTime));
//        float currentTimeLeftMargin = mSeekBarRealWidth * 1.00000f * progress / (mSeekBar
// .getMax() * 1.0000000f)
//                + mSeekBar.getX() + mSeekBar.getPaddingStart() + 10 - mCurrentTimeTextView
// .getWidth() / 2;
//        Log.i(TAG, "refreshCurrentTimeTextView: progress=" + progress);
//        Log.i(TAG, "refreshCurrentTimeTextView: currentTimeLeftMargin=" + currentTimeLeftMargin);
//        LayoutParams currentTimeLayoutParams = (LayoutParams) mCurrentTimeTextView
// .getLayoutParams();
//        currentTimeLayoutParams.leftMargin = (int) currentTimeLeftMargin;
//        mCurrentTimeTextView.setLayoutParams(currentTimeLayoutParams);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        refreshCurrentTimeTextView(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStartTrackingTouch: ");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStopTrackingTouch: ");
    }


    private void setSeekTo() {
        int duration = mNewTVLauncherPlayer.getDuration();
        int position = 0;
        if (duration == 0) {
            Log.i(TAG, "handleMessage: SEEK_TO duration==0");
        } else {
            if (mLiveInfo == null) {
                position = duration / mSeekBar.getMax() * mSeekBar.getProgress();
                Log.i(TAG, "handleMessage: SEEK_TO mSeekBar.getProgress()=" + mSeekBar
                        .getProgress());
            } else {
                if (!seekToEnd) {
                    int current = mSeekBar.getProgress() * SPLIT_DURATION;
                    position = current - mLiveInfo.getCurrentPosition();
                } else {
                    seekToEnd = false;
                    position = 0;
                }
            }
        }
        mNewTVLauncherPlayer.seekTo(position);
    }

    public void setSeekPauseIcon() {
        if (mImgSeekStatus != null)
            mImgSeekStatus.setImageResource(R.drawable.seek_pause);
    }

    public void setSeekPlayIcon() {
        mImgSeekStatus.setImageResource(R.drawable.seek_play);
    }

    public BitmapDrawable getNewDrawable(int restId, int dstWidth, int dstHeight) {
        Bitmap Bmp = BitmapFactory.decodeResource(
                LauncherApplication.AppContext.getResources(), restId);
        Bitmap bmp = Bmp.createScaledBitmap(Bmp, dstWidth, dstHeight, true);
        BitmapDrawable d = new BitmapDrawable(bmp);
        Bitmap bitmap = d.getBitmap();
        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
            d.setTargetDensity(LauncherApplication.AppContext.getResources().getDisplayMetrics());
        }
        return d;
    }

    private void checkImageHint() {
        if (mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isAlive()) {
            imageHint.setVisibility(View.GONE);
        } else {
            imageHint.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("HandlerLeak")
    private static class SeekBarAreaHandler extends Handler {

        private WeakReference<NewTVLauncherPlayerSeekbar> weakReference;

        private SeekBarAreaHandler(NewTVLauncherPlayerSeekbar seekbar) {
            weakReference = new WeakReference<>(seekbar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEEK_TO:
                    weakReference.get().setSeekTo();
                    break;

                case DISMISS_VIEW:
                    weakReference.get().dismiss();
                    break;

                case REFRESH_CURRENTTIME_AND_PROGRESS:
                    weakReference.get().refreshTimeAndProgress();
                    break;

                case REFRESH_LEFTTIME:

                    weakReference.get().refreshLeftTime();
                    break;
                default:
                    break;
            }
        }
    }
}
