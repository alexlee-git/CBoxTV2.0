package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/2/26.
 */

public class NewTVLauncherPlayerLoading extends FrameLayout {
    public static final int windowFlag = 0x00f1;
    public static final int allScrFlag = 0x00f2;
    private static final String TAG = NewTVLauncherPlayerLoading.class.getName();
    private static final int REFRESH_NET_SPEED = 6001;

    private TextView mProgramNameTextView;
    private TextView mNetSpeedTextView;
    private View playerLoadingDrawable;
    private LinearLayout playerLoadingInfoContainer;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_NET_SPEED:
                    refreshNetSpeed();
                    mHandler.sendEmptyMessageDelayed(REFRESH_NET_SPEED, 1000);
                    break;
            }
            return false;
        }
    });

    public NewTVLauncherPlayerLoading(@NonNull Context context) {
        this(context, null);
    }

    public NewTVLauncherPlayerLoading(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public NewTVLauncherPlayerLoading(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData();
    }

    public void release() {
        playerLoadingDrawable = null;
        mProgramNameTextView = null;
        mNetSpeedTextView = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /**
     * 更新显示属性
     *
     * @param size         文本大小
     * @param isFullScreen 是否为全屏状态
     */
    private int mIsFullScreen = -1;

    public void updatePropertys(int size, boolean isFullScreen) {
        int screen_size = isFullScreen ? 1 : 0;
        if(mIsFullScreen != -1 && mIsFullScreen == screen_size) return;
        mIsFullScreen = screen_size;

        mProgramNameTextView.setTextSize(size);
        mProgramNameTextView.postInvalidate();

        mNetSpeedTextView.setTextSize(size - 3);
        mNetSpeedTextView.postInvalidate();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                playerLoadingDrawable.getLayoutParams();
        if (isFullScreen) {
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.width_532px);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.height_175px);
        } else {
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.width_334px);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.height_111px);

        }
        playerLoadingDrawable.setLayoutParams(layoutParams);
        playerLoadingDrawable.postInvalidate();

        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)
                playerLoadingInfoContainer.getLayoutParams();
        if (isFullScreen) {
            containerParams.topMargin = -getResources().getDimensionPixelSize(R.dimen.height_40px);
        } else {
            containerParams.topMargin = -getResources().getDimensionPixelSize(R.dimen.height_25px);
        }
        playerLoadingInfoContainer.setLayoutParams(containerParams);
        playerLoadingInfoContainer.requestLayout();

        requestLayout();
    }

    public void setIsPrepared(boolean isPrepared) {
//        findViewById(R.id.view_root).setVisibility(isPrepared ? View.GONE : View.VISIBLE);
        findViewById(R.id.view_root).setBackground(isPrepared ? null : getResources().getDrawable
                (R.drawable.normalplayer_bg));
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.newtv_launcher_player_loading,
                this);
        mProgramNameTextView = (TextView) view.findViewById(R.id.loading_program_name);
        mNetSpeedTextView = (TextView) view.findViewById(R.id.loading_net_speed);
        playerLoadingDrawable = view.findViewById(R.id.player_loading_drawable);
        playerLoadingInfoContainer = view.findViewById(R.id.player_loading_info_container);
    }

    public void setShowErrorMessage(String message) {
        if (mProgramNameTextView != null) {
            mProgramNameTextView.setVisibility(View.VISIBLE);
            mProgramNameTextView.setText(message);
            loadingAnimation(false);
        }
    }

    public void setShowMessage(String message) {
        if (mProgramNameTextView != null) {
            mProgramNameTextView.setVisibility(View.VISIBLE);
            mProgramNameTextView.setText(message);
        }
    }

    public void setShowMessage(int message) {
        if (mProgramNameTextView != null) {
            mProgramNameTextView.setVisibility(View.VISIBLE);
            mProgramNameTextView.setText(message);
        }
    }

    private void initData() {
        Log.i(TAG, "initData: ");
        mHandler.sendEmptyMessage(REFRESH_NET_SPEED);
    }

    public void setProgramName(String programName) {
        Log.i(TAG, "setProgramName: ");
        if (mProgramNameTextView == null) return;
        if (TextUtils.isEmpty(programName)) {
            mProgramNameTextView.setVisibility(View.GONE);
        } else {
            mProgramNameTextView.setVisibility(View.VISIBLE);
            mProgramNameTextView.setText(String.format(Locale.getDefault(), "即将播放:%s",
                    programName));
        }
    }

    public void show(boolean isShowName, int loadFlag) {
        Log.i(TAG, "show: ");
        if (mProgramNameTextView != null) {
            if (isShowName) {
                mProgramNameTextView.setVisibility(View.VISIBLE);
            } else {
                mProgramNameTextView.setVisibility(View.VISIBLE);
            }
        }
//        setLoadDrawable(loadFlag);
        setIsPrepared(isShowName);
        loadingAnimation(true);
        setVisibility(View.VISIBLE);
        bringToFront();
    }

    private void setLoadDrawable(int loadFlag) {
        ViewGroup.LayoutParams params = playerLoadingDrawable.getLayoutParams();
        if (loadFlag == windowFlag) {
            params.width = (int) getResources().getDimension(R.dimen.height_334px);
            params.height = (int) getResources().getDimension(R.dimen.height_111px);
            playerLoadingDrawable.setLayoutParams(params);
        } else if (loadFlag == allScrFlag) {
            params.width = (int) getResources().getDimension(R.dimen.height_532px);
            params.height = (int) getResources().getDimension(R.dimen.height_175px);
            playerLoadingDrawable.setLayoutParams(params);
        }
    }

    public void dismiss() {
        Log.i(TAG, "dismiss: ");
        loadingAnimation(false);
        setVisibility(View.INVISIBLE);
    }

    private boolean refreshNetSpeed() {
        if (mNetSpeedTextView == null) {
            return false;
        }

        long nowTotalRxBytes = getTotalRxBytes(getContext().getApplicationInfo().uid);
        long nowTimeStamp = System.currentTimeMillis();
        int speed = (int) ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp -
                lastTimeStamp));//毫秒转换
        if (speed >= 5000) {
            speed = (int) (Math.random() * 5000);
        }
//        Log.i(TAG, "refreshNetSpeed: speed=" + speed);
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        mNetSpeedTextView.setText(String.format(Locale.getDefault(), "%d kb/s", speed));
        return true;
    }

    private long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats
                .getTotalRxBytes() / 1024);//转为KB
    }

    private void loadingAnimation(boolean play) {
        View view = playerLoadingDrawable;
        if (view != null) {
            AnimationDrawable animation = (AnimationDrawable) view.getBackground();
            if (animation != null) {
                if (play) {
                    view.setVisibility(View.VISIBLE);
                    animation.start();
                } else {
                    animation.stop();
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
}
