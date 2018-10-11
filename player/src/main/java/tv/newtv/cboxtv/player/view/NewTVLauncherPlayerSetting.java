package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.player.NewTVLauncherPlayer;
import tv.newtv.cboxtv.player.model.CdnInfo;
import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/2/26.
 */

public class NewTVLauncherPlayerSetting extends FrameLayout implements View.OnKeyListener, View.OnFocusChangeListener {

    private static final String TAG = NewTVLauncherPlayerSetting.class.getName();
    private static final int DISMISS_VIEW = 5001;
    private static final long DISMISS_VIEW_DELAY_TIME = 5000;

    private FrameLayout mSizeFrameLayout;
    private FrameLayout mSharpnessFrameLayout;

    private TextView mSizeTextView;
    private TextView mSharpnessTextView;

    private String[] mSizeStrings = {"全屏", "原始比例", "4:3"};
    private int mSizeIndex;

    private int mSharpnessIndex;
    private List<CdnInfo> mMediaCDNInfos;
    private PlayerSettingHandler mHandler;
    private AnimationSet mAnimationIn, mAnimationOut;
    private NewTVLauncherPlayer mNewTVLauncherPlayer;

    public NewTVLauncherPlayerSetting(@NonNull Context context) {
        this(context, null);
    }

    public NewTVLauncherPlayerSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTVLauncherPlayerSetting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData(context);
    }

    public void setmNewTVLauncherPlayer(NewTVLauncherPlayer mNewTVLauncherPlayer) {
        this.mNewTVLauncherPlayer = mNewTVLauncherPlayer;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.newtv_launcher_player_setting, this);
        mSizeFrameLayout = (FrameLayout) view.findViewById(R.id.player_setting_size);
        mSharpnessFrameLayout = (FrameLayout) view.findViewById(R.id.player_setting_sharpness);
        mSizeTextView = (TextView) view.findViewById(R.id.player_setting_size_textview);
        mSharpnessTextView = (TextView) view.findViewById(R.id.player_setting_sharpness_textview);

        mSizeFrameLayout.setOnKeyListener(this);
        mSharpnessFrameLayout.setOnKeyListener(this);

        mSizeFrameLayout.setOnFocusChangeListener(this);
        mSharpnessFrameLayout.setOnFocusChangeListener(this);

    }

    private void initData(Context context) {

        mHandler = new PlayerSettingHandler();

        mAnimationIn = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.player_setting_in);
        mAnimationOut = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.player_setting_out);
    }

    private class PlayerSettingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_VIEW:
                    dismiss();
                    break;
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.removeMessages(DISMISS_VIEW);
            mHandler.sendEmptyMessageDelayed(DISMISS_VIEW, DISMISS_VIEW_DELAY_TIME);
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                return true;
            }
            int i = v.getId();
            if (i == R.id.player_setting_size) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    Log.i(TAG, "onKey: ACTION_UP player_setting_size KEYCODE_DPAD_LEFT");

                    if (mSizeIndex > 0) {

                        mSizeIndex--;
                    } else {
                        mSizeIndex = mSizeStrings.length - 1;
                    }
                    mSizeTextView.setText(mSizeStrings[mSizeIndex]);
                    mNewTVLauncherPlayer.setVideoSize(mSizeIndex + 1);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    Log.i(TAG, "onKey: ACTION_UP KEYCODE_DPAD_RIGHT KEYCODE_DPAD_LEFT");

                    if (mSizeIndex < mSizeStrings.length - 1) {

                        mSizeIndex++;
                    } else {
                        mSizeIndex = 0;
                    }
                    mSizeTextView.setText(mSizeStrings[mSizeIndex]);
                    mNewTVLauncherPlayer.setVideoSize(mSizeIndex + 1);

                }

            } else if (i == R.id.player_setting_sharpness) {
                if (mMediaCDNInfos == null || mMediaCDNInfos.size() < 1) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    Log.i(TAG, "onKey: ACTION_UP player_setting_sharpness KEYCODE_DPAD_LEFT");

                    if (mSharpnessIndex > 0) {

                        mSharpnessIndex--;
                    } else {
                        mSharpnessIndex = mMediaCDNInfos.size() - 1;
                    }
                    mSharpnessTextView.setText(mMediaCDNInfos.get(mSharpnessIndex).getMediaType());
                    mNewTVLauncherPlayer.setDataSource(mMediaCDNInfos.get(mSharpnessIndex)
                            .getPlayURL());
//                        NewTVLauncherPlayer.getInstance().setDataSource("http://manmanyuntest
// .vod06.icntvcdn.com/newtv2/2016/11/01/qhsl/qhsl.m3u8");
//                        dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    Log.i(TAG, "onKey: ACTION_UP player_setting_sharpness KEYCODE_DPAD_RIGHT");

                    if (mSharpnessIndex < mMediaCDNInfos.size() - 1) {

                        mSharpnessIndex++;
                    } else {
                        mSharpnessIndex = 0;
                    }
                    mSharpnessTextView.setText(mMediaCDNInfos.get(mSharpnessIndex).getMediaType());
                    mNewTVLauncherPlayer.setDataSource(mMediaCDNInfos.get(mSharpnessIndex)
                            .getPlayURL());
//                        NewTVLauncherPlayer.getInstance().setDataSource("http://manmanyuntest
// .vod06.icntvcdn.com/newtv2/2016/11/01/qhsl/qhsl.m3u8");
//                        dismiss();
                }

            }
        }
        return false;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            int i = v.getId();
            if (i == R.id.player_setting_size) {
                mSizeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
                mSizeTextView.setBackgroundResource(R.drawable.player_setting_text_bg);

            } else if (i == R.id.player_setting_sharpness) {
                mSharpnessTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
                mSharpnessTextView.setBackgroundResource(R.drawable.player_setting_text_bg);


            }
        } else {
            int i = v.getId();
            if (i == R.id.player_setting_size) {
                mSizeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
                mSizeTextView.setBackgroundColor(Color.TRANSPARENT);


            } else if (i == R.id.player_setting_sharpness) {
                mSharpnessTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
                mSharpnessTextView.setBackgroundColor(Color.TRANSPARENT);


            }
        }
    }

    public void setData(List<CdnInfo> mediaCDNInfos, int index) {
        Log.i(TAG, "setData: ");
        mMediaCDNInfos = mediaCDNInfos;
        mSharpnessIndex = index;
        if (mediaCDNInfos.size() <= 1) {
            mSharpnessTextView.setTextColor(Color.parseColor("#33dcdcdc"));
        } else {
            mSharpnessTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        String sharpness = mediaCDNInfos.get(index).getMediaType();
        mSharpnessTextView.setText(sharpness);

    }

    public void show() {
        Log.i(TAG, "show: ");
        setVisibility(View.VISIBLE);
        bringToFront();
        mSizeFrameLayout.requestFocus();
        if (mMediaCDNInfos.size() <= 1) {
            mSharpnessFrameLayout.setFocusable(false);
        } else {
            mSharpnessFrameLayout.setFocusable(true);
        }
        startAnimation(mAnimationIn);
        mHandler.sendEmptyMessageDelayed(DISMISS_VIEW, DISMISS_VIEW_DELAY_TIME);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_SETTING_VIEW);
    }

    public void dismiss() {
        Log.i(TAG, "dismiss: ");
        mHandler.removeMessages(DISMISS_VIEW);
        setVisibility(View.INVISIBLE);
        startAnimation(mAnimationOut);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NO_VIEW);
    }

    public void release() {
        Log.i(TAG, "release: ");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mMediaCDNInfos = null;

    }
}
