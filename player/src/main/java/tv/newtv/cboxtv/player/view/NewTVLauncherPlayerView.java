package tv.newtv.cboxtv.player.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.LetvDeviceUtil;
import com.newtv.cms.CmsErrorCode;
import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.uc.pay.ExterPayBean;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.KeyEventUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScreenUtils;
import com.newtv.libs.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tv.icntv.icntvplayersdk.NewTVPlayerInterface;
import tv.newtv.cboxtv.menu.IMenuGroupPresenter;
import tv.newtv.cboxtv.menu.MenuPopupWindow;
import tv.newtv.cboxtv.player.AlternateCallback;
import tv.newtv.cboxtv.player.FocusWidget;
import tv.newtv.cboxtv.player.IFocusWidget;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.NewTVLauncherPlayer;
import tv.newtv.cboxtv.player.Player;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.PlayerConstants;
import tv.newtv.cboxtv.player.PlayerErrorCode;
import tv.newtv.cboxtv.player.VPlayCenter;
import tv.newtv.cboxtv.player.ad.BuyGoodsBusiness;
import tv.newtv.cboxtv.player.contract.LiveContract;
import tv.newtv.cboxtv.player.contract.PlayerAlternateContract;
import tv.newtv.cboxtv.player.contract.PlayerContract;
import tv.newtv.cboxtv.player.contract.VodContract;
import tv.newtv.cboxtv.player.iPlayCallBackEvent;
import tv.newtv.cboxtv.player.listener.ScreenListener;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.model.VideoDataStruct;
import tv.newtv.cboxtv.player.model.VideoPlayInfo;
import tv.newtv.cboxtv.player.videoview.ExitVideoFullCallBack;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.vip.VipCheck;
import tv.newtv.player.R;

//import tv.newtv.cboxtv.cms.details.PushManager;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerView extends FrameLayout implements LiveContract.View, VodContract
        .View, LiveTimer.LiveTimerCallback, PlayerAlternateContract.View, PlayerTimer
        .PlayerTimerCallback {

    public static final int SHOWING_NO_VIEW = 0;
    public static final int SHOWING_SEEKBAR_VIEW = 2;
    public static final int SHOWING_NUMBER_PROGRAM_SELECTOR = 3;
    public static final int SHOWING_NAME_PROGRAM_SELECTOR = 4;
    public static final int SHOWING_SETTING_VIEW = 5;
    public static final int SHOWING_EXIT_VIEW = 6;
    public static final int SHOWING_PROGRAM_TREE = 7;
    public static final int SHOWING_TIP_VIEW = 8;
    public static final int SHOWING_ALTER_CHANGE_VIEW = 9;


    public static final int PLAY_TYPE_SINGLE = 0;
    public static final int PLAY_TYPE_SERIES = 1;
    public static final int PLAY_TYPE_LIVE = 2;
    public static final int PLAY_TYPE_ALTERNATE = 3;

    private static final String TAG = NewTVLauncherPlayerView.class.getName();
    private static final int PROGRAM_SELECTOR_TYPE_NONE = 0; //不显示选集
    private static final int PROGRAM_SELECTOR_TYPE_NUMBER = 1; //显示数字选集
    private static final int PROGRAM_SELECTOR_TYPE_NAME = 2; //显示名称选集
    private static final String SP_ALTERNATE_FIRST = "Alternate_first";


    private static final String AD_START_BUFFER = "ADStartBuffer";          //广告开始缓冲
    private static final String AD_END_BUFFER = "ad_onPrepared";            //广告缓冲完成，开始播放
    private static final String VIDEO_START_BUFFER = "VideoStartBuffer";    //正片开始缓冲
    private static final String PIC_AD_END_BUFFER = "picAD";    //正片开始缓冲
    private static final String VIDEO_SEEK_END = "onSeekComplete";    //跳播结束
    private static final String START_BUFFER = "701";    //开始缓冲
    private static final String END_BUFFER = "702";    //缓冲结束

    protected PlayerViewConfig defaultConfig;
    private NewTVLauncherPlayerLoading mLoading;
    private NewTVLauncherPlayerSeekbar mNewTVLauncherPlayerSeekbar;
    private NewTvLauncherPlayerTip mNewTvTipView;
    private int mShowingChildView = SHOWING_NO_VIEW;
    private boolean mIsLoading; //是否在缓冲
    private boolean mIsPause; //是否在暂停
    private boolean mIsPrepared;
    private int mHistoryPostion;
    private boolean isReleased = false;
    private MenuPopupWindow menuPopupWindow;
    private IMenuGroupPresenter menuGroupPresenter;
    private NewTVLauncherPlayer mNewTVLauncherPlayer;
    private List<IPlayProgramsCallBackEvent> listener = new ArrayList<>();
    private boolean NeedJumpAd = false;
    private boolean unshowLoadBack = false;

    private LiveContract.Presenter mLivePresenter;
    private VodContract.Presenter mVodPresenter;
    private PlayerAlternateContract.Presenter mAlternatePresenter;

    private PlayerTimer mPlayerTimer;

    private LiveTimer mLiveTimer;
    private boolean isNextPlay;
    private List<ScreenListener> screenListeners;
    private boolean isTrySee;
    private TextView hintVip, alterTitle, alterChannel;
    private View bigScreen;
    private NewTVLauncherPlayerSeekbar.FreeDurationListener freeDurationListener = new
            FreeDuration();

    private BuyGoodsBusiness buyGoodsBusiness = null;
    private PlayerLocation mPlayerLocation;
    private LiveListener mLiveListener;
    private PlayerContract mPlayerContract;
    private iPlayCallBackEvent mLiveCallBackEvent = new iPlayCallBackEvent() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> definitionDatas) {
            LogUtils.i(TAG, "live onPrepared: ");
            mIsPrepared = true;
            stopLoading();

            if (mShowingChildView == SHOWING_PROGRAM_TREE) {
                menuGroupPresenter.gone();
            }

            if (!(isLiving() && defaultConfig.liveInfo != null && !defaultConfig.liveInfo
                    .isTimeShift())) {
                mNewTVLauncherPlayerSeekbar.setDuration();
                showSeekBar(mIsPause, true);
            }

            mHistoryPostion = 0;
        }

        @Override
        public void onCompletion(int type) {
            LogUtils.i(TAG, "live onCompletion: ");
        }

        @Override
        public void onVideoBufferStart(String typeString) {
            LogUtils.i(TAG, "live onVideoBufferStart: typeString=" + typeString);
            startLoading();

            if (TextUtils.equals(AD_START_BUFFER, typeString)) {
                setCurrentVideoState(PlayerContract.STATE_AD_BUFFERING);
            } else if (TextUtils.equals(VIDEO_START_BUFFER, typeString)) {
                setCurrentVideoState(PlayerContract.STATE_VIDEO_BUFFERING);
            }
        }

        @Override
        public void onVideoBufferEnd(String typeString) {
            Log.i(TAG, "live onVideoBufferEnd: typeString=" + typeString);
            if ("702".equals(typeString)) {
                unshowLoadBack = true;
            }

            if (TextUtils.equals(AD_END_BUFFER, typeString)) {
                setCurrentVideoState(PlayerContract.STATE_AD_PLAYING);
            } else if (TextUtils.equals(END_BUFFER, typeString) && mPlayerContract
                    .equalsPlayerState(PlayerContract.STATE_AD_PLAYING)) {
                setCurrentVideoState(PlayerContract.STATE_VIDEO_PLAYING);
            }

            if (!TextUtils.isEmpty(typeString) && (typeString.equals(END_BUFFER) ||
                    AD_END_BUFFER.equals(typeString))) {
                stopLoading();
                showSeekBar(mIsPause, true);
            }
        }

        @Override
        public void onTimeout(int i) {
            LogUtils.i(TAG, "live onTimeout: " + i);
        }

        @Override
        public void changePlayWithDelay(int delay, String liveUrl) {

//            if (mProgramSeriesInfo != null) {
//                String playUrl = translateUrl(liveUrl, delay);
//                LogUtils.d(TAG, "changePlayWithDelay video delay=" + delay + " url=" + playUrl);
//                if (mLiveInfo.getLiveUrl().equals(playUrl)) {
//                    return;
//                }
//                mNewTVLauncherPlayer.addHistory();
//                mLiveInfo.setLiveUrl(playUrl);
//                PlayerConfig.getInstance().setScreenChange(true);
//                PlayerConfig.getInstance().setJumpAD(true);
//                playLive(playUrl, mProgramSeriesInfo, false, 0, 0);
//            }
        }

        @Override
        public void onError(int what, int extra, String msg) {
            LogUtils.i(TAG, "live onError: ");
        }

        @Override
        public void onAdStartPlaying() {

        }
    };
    private iPlayCallBackEvent mCallBackEvent = new iPlayCallBackEvent() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> definitionDatas) {
            LogUtils.i(TAG, "onPrepared: p=" + mHistoryPostion);
            mIsPrepared = true;
//            stopLoading();//注释掉该行代码会在乐视上导致在播放某些视频时一直显示加载  但是视频已经播放的问题
            if (LetvDeviceUtil.isLetvDevice()) {
                stopLoading();
            }
            mNewTVLauncherPlayerSeekbar.setDuration();
//            if (mHistoryPostion > 0 && mHistoryPostion < mNewTVLauncherPlayer.getDuration() - 30
////                    * 1000) {
////                mNewTVLauncherPlayer.seekTo(mHistoryPostion);
////                mPlayerContract.setPlayerState(PlayerContract.STATE_VIDEO_SEEK_START);
////            }
////            mHistoryPostion = 0;
            showSeekBar(mIsPause, true);
        }

        @Override
        public void onCompletion(int type) {
            LogUtils.i(TAG, "onCompletion: ");
            /*
             *  大屏点播完成后，
             *  判断是否符合直播条件，如果符合则直播。 不符合则播放下一级
             */
            // 什么时候会修改Constant.isLiving的值？
            // 3. 大屏加载完一个点播文件，播放下一个之前，需要判断当前时间是否满足直播
            if (type == NewTVPlayerInterface.CONTENT_TYPE ||
                    type == NewTVPlayerInterface.POST_AD_TYPE) {
                Constant.isLiving = false;
                playVodNext();
            }

            if (type == NewTVPlayerInterface.PRE_AD_TYPE || type == NewTVPlayerInterface.MID_AD_TYPE
                    || type == NewTVPlayerInterface.POST_AD_TYPE) {
                if (isTrySee) {
                    hintVip.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onVideoBufferStart(String typeString) {
            LogUtils.i(TAG, "onVideoBufferStart: typeString=" + typeString);
            if (!mIsLoading) {
                startLoading();
            }
            if (TextUtils.equals(AD_START_BUFFER, typeString)) {
                //如果状态等于ADStartBuffer的时候，设置状态为广告缓冲中
                setCurrentVideoState(PlayerContract.STATE_AD_BUFFERING);
            } else if (TextUtils.equals(VIDEO_START_BUFFER, typeString)) {
                //如果状态等于VideoStartBuffer的时候，设置状态为正片缓冲中
                setCurrentVideoState(PlayerContract.STATE_VIDEO_BUFFERING);
            }
        }

        @Override
        public void onVideoBufferEnd(String typeString) {
            Log.i(TAG, "onVideoBufferEnd: typeString=" + typeString);
            if ("702".equals(typeString)) {
                unshowLoadBack = true;
            }
            if (LetvDeviceUtil.isLetvDevice() && mNewTVLauncherPlayer.isADPlaying()) {
                unshowLoadBack = false;
            }
            boolean isHaveAD;
            if (!TextUtils.isEmpty(typeString)) {
                if (typeString.equals(AD_END_BUFFER)) {
                    isHaveAD = true;
                    RxBus.get().post(Constant.IS_HAVE_AD, isHaveAD);
                }
            }

            if (TextUtils.equals(PIC_AD_END_BUFFER, typeString)) {
                setCurrentVideoState(PlayerContract.STATE_AD_PLAYING);
            } else if (TextUtils.equals(AD_END_BUFFER, typeString)) {
                //如果状态等于ad_onPrepared的时候，设置状态为广告播放中
                setCurrentVideoState(PlayerContract.STATE_AD_PLAYING);
            } else if (TextUtils.equals(VIDEO_SEEK_END, typeString)) {
                //如果状态等于onSeekComplete的时候，设置状态为正片播放中
                setCurrentVideoState(PlayerContract.STATE_VIDEO_SEEK_END);
            } else if (TextUtils.equals(END_BUFFER, typeString) &&
                    !mPlayerContract.equalsPlayerState(PlayerContract.STATE_VIDEO_SEEK_START) &&
                    (!mPlayerContract.equalsPlayerState(PlayerContract.STATE_AD_PLAYING) ||
                            mPlayerContract.equalsPlayerState(PlayerContract
                                    .STATE_VIDEO_SEEK_END))) {
                //如果当前typeString=702 并且之前状态不等于广告播放状态 并且不等于跳转状态
                // 或者 当前状态等于 跳转结束 状态的时候，设置当前状态为正片播放状态
                setCurrentVideoState(PlayerContract.STATE_VIDEO_PLAYING);
            } else {

            }
            if (!TextUtils.isEmpty(typeString) && (typeString.equals("702") || "ad_onPrepared"
                    .equals(typeString))) {
                stopLoading();
                showSeekBar(mIsPause, true);
            }
        }

        @Override
        public void onTimeout(int i) {
            LogUtils.i(TAG, "onTimeout: " + i);
        }

        @Override
        public void changePlayWithDelay(int delay, String url) {

        }

        @Override
        public void onError(int what, int extra, String msg) {
            LogUtils.i(TAG, "onError: ");
        }

        @Override
        public void onAdStartPlaying() {
            Log.i(TAG, "onAdStartPlaying  dismiss SeekBar");
            if (mNewTVLauncherPlayerSeekbar != null
                    && mNewTVLauncherPlayerSeekbar.getVisibility() == VISIBLE) {
                mNewTVLauncherPlayerSeekbar.dismiss();
            }
            if (isTrySee) {
                hintVip.setVisibility(View.GONE);
            }
        }
    };
    private ChangeAlternateListener mChangeAlternateListener;
    private NewTvAlterChangeView mNewTvAlterChange;
    private OnPlayerStateChange mOnPlayerStateChange = new OnPlayerStateChange() {
        @Override
        public boolean onStateChange(boolean fullScreen, int visible, boolean videoPlaying) {

            if (bigScreen != null) {
                bigScreen.setVisibility(fullScreen ? GONE : VISIBLE);
            }
            if (alterTitle != null) {
                alterTitle.setVisibility(defaultConfig.isAlternate && !fullScreen ? VISIBLE :
                        GONE);
            }
            if (alterChannel != null) {
                alterChannel.setVisibility(defaultConfig.isAlternate && !fullScreen ? VISIBLE :
                        GONE);
            }

            if (defaultConfig.isAlternate) {
                if (!fullScreen) {
                    View alternate = findViewWithTag("ALTERNATE_MESSAGE");
                    if (alternate != null) {
                        bringChildToFront(alternate);
                    }
                }

                if (!fullScreen || !videoPlaying) {
                    if (mNewTvAlterChange != null) {
                        mNewTvAlterChange.dismiss();
                    }
                }

                if (fullScreen && visible == 0 && videoPlaying) {
                    if (mNewTvAlterChange != null && mNewTvAlterChange.isNeedTip()) {
                        Alternate current = mAlternatePresenter.getCurrentAlternate();
                        if (current != null) {
                            mNewTvAlterChange.setChannelText(String.format(Locale.getDefault(),
                                    "%s %s",
                                    mAlternatePresenter.getCurrrentChannel(), mAlternatePresenter
                                            .getCurrrentTitle()));
                            mNewTvAlterChange.setTitleText(current.getTitle());
                            mNewTvAlterChange.show();
                            return true;
                        }
                    }
                }

                if (fullScreen && visible == 0 && videoPlaying && !defaultConfig.hasTipAlternate) {
                    //TODO 是否显示轮播帮助提示
                    AlternateTipView tipView = new AlternateTipView(getContext());
                    if (tipView.isNeedTip()) {
                        tip(tipView, 1);
                        defaultConfig.hasTipAlternate = true;
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean processKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP && defaultConfig.isAlternate) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyEvent.getKeyCode() ==
                        KeyEvent.KEYCODE_DPAD_CENTER) {
                    dismissTipView();
                    return true;
                }
            }
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_MENU:
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    public NewTVLauncherPlayerView(PlayerViewConfig config, @NonNull Context context) {
        this(context, null, 0, config);
    }

    public NewTVLauncherPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public NewTVLauncherPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTVLauncherPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public NewTVLauncherPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr, PlayerViewConfig config) {
        super(context, attrs, defStyleAttr);
        updateDefaultConfig(config);
        initView(context);
    }

    public void destroy() {

        if (listener != null) {
            listener.clear();
        }

        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
            menuPopupWindow = null;
        }

        if (mPlayerLocation != null) {
            mPlayerLocation.destroy();
            mPlayerLocation = null;
        }

        if (mVodPresenter != null) {
            mVodPresenter.destroy();
            mVodPresenter = null;
        }

        if (mLivePresenter != null) {
            mLivePresenter.destroy();
            mLivePresenter = null;
        }

        if (mAlternatePresenter != null) {
            mAlternatePresenter.destroy();
            mAlternatePresenter = null;
        }

        if (menuGroupPresenter != null) {
            menuGroupPresenter.release();
            menuGroupPresenter = null;
        }

        mLiveCallBackEvent = null;
        mCallBackEvent = null;
        freeDurationListener = null;

        if (mPlayerContract != null) {
            mPlayerContract.destroy();
            mPlayerContract = null;
        }

        mLiveListener = null;

        mNewTVLauncherPlayer = null;
        mNewTVLauncherPlayerSeekbar = null;

        onDetachedFromWindow();
    }

    public void release() {
        if (isReleased) return;
        isReleased = true;
        addHistory();
        uploadExitLbLog();
        Log.i(TAG, "release: ");
        if (listener != null) {
            listener.clear();
            listener = null;
        }

        if (screenListeners != null) {
            screenListeners.clear();
            screenListeners = null;
        }
        defaultConfig = null;

        if (mLiveTimer != null && mLiveTimer.isRunning()) {
            mLiveTimer.cancel();
        }
        mLiveTimer = null;

        if (mPlayerTimer != null && mPlayerTimer.isRunning()) {
            mPlayerTimer.cancel();
        }
        mPlayerTimer = null;

        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.release();
        }
        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.release();
        }

        stopLoading();
        if (mLoading != null) {
            mLoading.release();
            mLoading = null;
        }

        hidePauseImage();


        if (buyGoodsBusiness != null) {
            buyGoodsBusiness.onDestroy();
            buyGoodsBusiness = null;
        }

        destroy();
    }

    private void setCurrentVideoState(int state) {
        mPlayerContract.setPlayerState(state);
    }

    public void setAlternatePlay() {
        defaultConfig.isAlternate = true;
    }

    @SuppressLint("UseSparseArrays")
    public int registerWidget(int id, IFocusWidget widget) {
        if (id != 0) {
            unregisterWidget(id);
        }
        FocusWidget focusWidget = new FocusWidget(widget);
        if (defaultConfig.widgetMap == null) {
            defaultConfig.widgetMap = new HashMap<>();
        }
        defaultConfig.widgetMap.put(focusWidget.getId(), focusWidget);
        return focusWidget.getId();
    }

    /**
     * 接触外部控件注册
     *
     * @param id
     */
    public void unregisterWidget(int id) {
        if (defaultConfig.widgetMap != null && defaultConfig.widgetMap.containsKey(id)) {
            FocusWidget focusWidget = defaultConfig.widgetMap.get(id);
            if (focusWidget != null && focusWidget.isShowing()) {
                focusWidget.onBackPressed();
            }
            defaultConfig.widgetMap.remove(id);
        }
    }

    public void updateDefaultConfig(PlayerViewConfig config) {
        if (config != null) {
            LogUtils.d(TAG, config.toString());
            defaultConfig = config;
            if (config.layoutParams != null && config.parentViewGroup != null) {
                setLayoutParams(config.layoutParams);
                ((ViewGroup) config.parentViewGroup).addView(this, config.layoutParams);
                return;
            }
        } else {
            defaultConfig = new PlayerViewConfig();
        }
        if (defaultConfig.playCenter == null) {
            defaultConfig.playCenter = new VPlayCenter();
        }

    }

    public PlayerViewConfig getDefaultConfig() {
        if (isReleased) return null;
        if (defaultConfig != null) {

            defaultConfig.playPosition = getCurrentPosition();
            defaultConfig.layoutParams = getLayoutParams();
            defaultConfig.parentViewGroup = getParent();
        }
        return defaultConfig;
    }

    protected void onError(String code, String messgae) {
        if (alterTitle != null) {
            alterTitle.setText("");
        }
        stopLoading();
    }

    protected boolean NeedRepeat() {
        return false;
    }

    public void ExitFullScreen() {
        if (!defaultConfig.isFullScreen) return;
        defaultConfig.isFullScreen = false;
        callBackScreenListener(false);

        if (mPlayerLocation != null) {
            mPlayerLocation.destroy();
            mPlayerLocation = null;
        }

        Activity activity = Player.get().getCurrentActivity();
        dismissChildView();

        final int screenWidth = activity.getWindow().getDecorView().getMeasuredWidth();
        final int screenHeight = activity.getWindow().getDecorView().getMeasuredHeight();

        ViewGroup.LayoutParams container = getLayoutParams();
        container.width = defaultConfig.defaultWidth;
        container.height = defaultConfig.defaultHeight;
        setLayoutParams(container);
        final FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android
                .R.id.content);
        View rootView = frameLayout.getChildAt(0);

        setParentWidth(this, rootView, screenWidth - defaultConfig.defaultWidth, screenHeight -
                        defaultConfig.defaultHeight,
                screenWidth, screenHeight, false, true);

        updateUIPropertys(false);
        if (menuGroupPresenter != null) {
            menuGroupPresenter.exitFullScreen();
        }

        NeedJumpAd = defaultConfig.ProgramIsChange;

        if (mIsPause && mNewTVLauncherPlayer != null) {
            start();
        }

    }

    public void setFromFullScreen() {
        defaultConfig.startIsFullScreen = true;
        defaultConfig.isFullScreen = true;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
        }
    }

    public boolean onBackPressed() {
        if (defaultConfig!=null && !defaultConfig.startIsFullScreen) {
            if (defaultConfig.isFullScreen) {
                ExitFullScreen();
            }
        } else {
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mPlayerContract != null) {
            mPlayerContract.screenSizeChange(right - left, bottom - top);
        }

        if (defaultConfig!=null && !defaultConfig.isFullScreen) {
            defaultConfig.defaultWidth = getLayoutParams().width;
            defaultConfig.defaultHeight = getLayoutParams().height;
        }
    }

    private void setParentWidth(View view, View rootView, int changeWidth, int changeHeight, int
            maxWidth, int maxHeight, boolean bringFront, boolean isExit) {
        if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup.getId() == android.R.id.content){
                if(isExit){
                    //退出全屏，将根布局margin值恢复正常
                    MarginLayoutParams layoutParams = (MarginLayoutParams) viewGroup.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    layoutParams.topMargin = 0;
                    viewGroup.setLayoutParams(layoutParams);
                }else{
                    if (mPlayerLocation != null) {
                        mPlayerLocation.destroy();
                        mPlayerLocation = null;
                    }
                    mPlayerLocation = PlayerLocation.build(this, bringFront);
                }
                return;
            }
            if (bringFront) {
                view.bringToFront();
            }
            ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
            boolean isSame = (!isExit && viewGroup.getWidth() == defaultConfig.defaultWidth &&
                    viewGroup
                            .getHeight() == defaultConfig.defaultHeight) || (isExit && viewGroup
                    .getWidth() ==
                    maxWidth &&
                    viewGroup.getHeight() == maxHeight);
            if (isSame) {
                layoutParams.width = isExit ? defaultConfig.defaultWidth : maxWidth;
                layoutParams.height = isExit ? defaultConfig.defaultHeight : maxHeight;
            } else {
                layoutParams.width = viewGroup.getWidth() + (isExit ? -changeWidth : changeWidth);
                layoutParams.height = viewGroup.getHeight() + (isExit ? -changeHeight :
                        changeHeight);
            }
            viewGroup.setLayoutParams(layoutParams);

            setParentWidth(viewGroup, rootView, changeWidth, changeHeight, maxWidth, maxHeight,
                    bringFront, isExit);
        }
    }

    public void delayEnterFullScreen(final Activity activity, final boolean bringFront, int delay) {
        if (defaultConfig.isFullScreen) return;
        defaultConfig.isFullScreen = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                defaultConfig.isFullScreen = false;
                EnterFullScreen(activity, bringFront);
            }
        }, delay);
    }

    public void EnterFullScreen(Activity activity, final boolean bringFront) {
        if (defaultConfig.isFullScreen) return;
        defaultConfig.isFullScreen = true;
        callBackScreenListener(true);

        defaultConfig.defaultWidth = getMeasuredWidth();
        defaultConfig.defaultHeight = getMeasuredHeight();

        final int screenWidth = activity.getWindow().getDecorView().getMeasuredWidth();
        final int screenHeight = activity.getWindow().getDecorView().getMeasuredHeight();

        final FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android
                .R.id.content);
        View rootView = frameLayout.getChildAt(0);

        setParentWidth(this, rootView, screenWidth - defaultConfig.defaultWidth, screenHeight -
                        defaultConfig.defaultHeight,
                screenWidth, screenHeight, bringFront, false);

        ViewGroup.LayoutParams container = getLayoutParams();
        container.width = screenWidth;
        container.height = screenHeight;
        setLayoutParams(container);

        defaultConfig.ProgramIsChange = false;

        createMenuGroup();

        if (mNewTVLauncherPlayer != null && !mNewTVLauncherPlayer.isADPlaying()) {
            if (menuGroupPresenter != null && !isLiving()) {
                menuGroupPresenter.showHinter();
            }
            showSeekBar(mIsPause, true);
        }

        updateUIPropertys(true);
    }

    private void createMenuGroup() {
        if (menuGroupPresenter == null) {
            menuPopupWindow = new MenuPopupWindow();
            menuGroupPresenter = menuPopupWindow.show(getContext(), this);
        }
    }

    public void updateUIPropertys(boolean isFullScreen) {
        if (isFullScreen && menuGroupPresenter != null) {
            menuGroupPresenter.enterFullScreen();
        }
        if (defaultConfig.videoFrameLayout != null) {
            defaultConfig.videoFrameLayout.updateTimeTextView(getResources().getDimensionPixelSize
                    (isFullScreen ? R.dimen.height_20px : R.dimen.height_10px));
        }
        if (mLoading != null) {
            mLoading.updatePropertys(getResources().getDimensionPixelSize(isFullScreen ? R.dimen
                    .height_22sp : R.dimen.height_11sp), isFullScreen);
        }
    }

    public boolean isFullScreen() {
        return this.getWidth() == ScreenUtils.getScreenW() && this.getHeight() == ScreenUtils
                .getScreenH();
    }


    public boolean isReleased() {
        return isReleased;
    }

    private String translateUrl(String url, int delay) {
        return defaultConfig.liveInfo.setTimeDelay(delay);
    }

    protected void initView(Context context) {
        mNewTVLauncherPlayer = new NewTVLauncherPlayer();
        mPlayerContract = new PlayerContract();
        mPlayerContract.setOnPlayerStateChange(mOnPlayerStateChange);

        View view = LayoutInflater.from(getContext()).inflate(R.layout
                .newtv_launcher_player_view, this);

        defaultConfig.videoFrameLayout = (VideoFrameLayout) view.findViewById(R.id
                .player_view_framelayout);
        mNewTVLauncherPlayerSeekbar = (NewTVLauncherPlayerSeekbar) view.findViewById(R.id
                .player_seekbar_area);

        mLoading = (NewTVLauncherPlayerLoading) view.findViewById(R.id.player_loading);

        mNewTvAlterChange = new NewTvAlterChangeView(context);
        LayoutParams alterLayoutParams = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mNewTvAlterChange.setLayoutParams(alterLayoutParams);
        mNewTvAlterChange.setVisibility(INVISIBLE);
        addView(mNewTvAlterChange, alterLayoutParams);

        mNewTVLauncherPlayerSeekbar.setmNewTVLauncherPlayer(mNewTVLauncherPlayer);

        updateUIPropertys(defaultConfig != null ? defaultConfig.isFullScreen : defaultConfig
                .startIsFullScreen);
        hintVip = findViewById(R.id.hint_vip);
        NewTVLauncherPlayerViewManager.getInstance().setPlayerView(this);

        View alternate = LayoutInflater.from(getContext()).inflate(R.layout
                .player_alternate_layout, this, false);
        alternate.setTag("ALTERNATE_MESSAGE");
        addView(alternate);

        alterTitle = alternate.findViewById(R.id.alter_title);
        alterChannel = alternate.findViewById(R.id.alter_channel);
        bigScreen = alternate.findViewById(R.id.alter_big_screen);

        mNewTvTipView = new NewTvLauncherPlayerTip(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        mNewTvTipView.setLayoutParams(layoutParams);
        ((ViewGroup) view).addView(mNewTvTipView, layoutParams);

        mLivePresenter = new LiveContract.LivePresenter(getContext(), this);
        mVodPresenter = new VodContract.VodPresenter(getContext(), this);

        mPlayerTimer = new PlayerTimer();
        mPlayerTimer.setCallback(this);
    }

    public void setSeriesInfo(Content seriesInfo) {
        if (defaultConfig.playCenter == null) {
            defaultConfig.playCenter = new VPlayCenter();
        }

        if (defaultConfig.playCenter != null && seriesInfo != null) {
            defaultConfig.playCenter.setSeriesInfo(seriesInfo);
        }
    }

    public void playAlternate(String alternateId, String title, String channelId) {

        //设置播放的位置
        stop();

        if (!defaultConfig.isFullScreen) {
            if (alterTitle != null) alterTitle.setVisibility(VISIBLE);
            if (alterChannel != null) alterChannel.setVisibility(VISIBLE);
            if (bigScreen != null) bigScreen.setVisibility(VISIBLE);
        } else {
            if (alterTitle != null) alterTitle.setVisibility(GONE);
            if (alterChannel != null) alterChannel.setVisibility(GONE);
            if (bigScreen != null) bigScreen.setVisibility(GONE);
        }

        if (isFullScreen() && !equalsInfo(defaultConfig.programSeriesInfo, alternateId)) {
            defaultConfig.ProgramIsChange = true;
        }

        if (mAlternatePresenter != null && mAlternatePresenter.equalsAlternate(alternateId)) {
            return;
        }
        if (mChangeAlternateListener != null) {
            mChangeAlternateListener.changeAlternate(alternateId, title, channelId);
        }

        if (alterChannel != null) {
            alterChannel.setText(defaultConfig.useAlternateUI ? String.format("%s %s", channelId,
                    title) : "");
        }

        if (alterTitle != null) {
            alterTitle.setText(defaultConfig.useAlternateUI ? "正在准备轮播数据..." : "");
        }
        setCurrentVideoState(PlayerContract.STATE_NORMAL);
        updatePlayStatus(PLAY_TYPE_ALTERNATE, 0, 0);
        stop();
        defaultConfig.alternateID = alternateId;
        if (mNewTvAlterChange != null) {
            mNewTvAlterChange.setCurrentId(alternateId);
        }
        if (mAlternatePresenter == null) {
            mAlternatePresenter = new PlayerAlternateContract.AlternatePresenter(getContext(),
                    this);
        }
        mAlternatePresenter.requestAlternate(alternateId, title, channelId);

        mPlayerTimer.start();
        LogUploadUtils.uploadLog(Constant.LOG_LB,"0,"+channelId);
    }

    public void playSingleOrSeries(int mIndex, int position) {
        playSingleOrSeries(mIndex, position, true);

        mPlayerTimer.start();
    }

    private void playSingleOrSeries(int mIndex, int position, boolean updateState) {
        //设置播放的位置
        stop();

        int index = CmsUtil.translateIndex(defaultConfig.playCenter.getCurrentSeriesInfo(), mIndex);
        defaultConfig.playCenter.setCurrentIndex(index);
        setCurrentVideoState(PlayerContract.STATE_NORMAL);
        setHintTextVisible(GONE);
        VPlayCenter.DataStruct dataStruct = defaultConfig.playCenter.getDataStruct();
        if (dataStruct != null) {
            if (dataStruct.playType == VPlayCenter.PLAY_SERIES) {
                playProgramSeries(defaultConfig.playCenter.getCurrentSeriesInfo(), false, index,
                        position, updateState);
            } else {
                playProgramSingle(defaultConfig.playCenter.getCurrentSeriesInfo(), position,
                        false, updateState);
            }
        } else {
            onError(PlayerErrorCode.PROGRAM_SERIES_EMPTY, PlayerErrorCode.getErrorDesc(getContext
                    (), PlayerErrorCode.PROGRAM_SERIES_EMPTY));
        }
    }

    public boolean isReady() {
        return defaultConfig.playCenter != null && defaultConfig.playCenter.isReady();
    }

    private boolean equalsInfo(Content AInfo, Content BInfo) {
        if (AInfo == null || BInfo == null) return false;
        if (TextUtils.isEmpty(AInfo.getContentUUID()) || TextUtils.isEmpty(BInfo.getContentUUID()
        )) {
            return false;
        }
        Log.e(TAG, "AInfo Id=" + AInfo.getContentUUID() + " BInfo Id=" + BInfo.getContentUUID());
        return AInfo.getContentUUID().equals(BInfo.getContentUUID());
    }

    private boolean equalsInfo(Content AInfo, String contentId) {
        if (AInfo == null) return false;
        if (TextUtils.isEmpty(AInfo.getContentUUID()) || TextUtils.isEmpty(contentId
        )) {
            return false;
        }
        Log.e(TAG, "AInfo Id=" + AInfo.getContentUUID() + " BInfo Id=" + contentId);
        return AInfo.getContentUUID().equals(contentId);
    }

    /*
     * 播放节目集
     * programSeriesInfo 节目集信息
     * isNeedStartActivity 是否需要启动新的activity（播放器内选集，切换清晰度时为false）
     * index 播放第几集
     * position 从什么位置开始播放
     * */
    private void playProgramSeries(Content programSeriesInfo, boolean
            isNeedStartActivity, int index, int position, boolean updateState) {
        unshowLoadBack = false;

        if (isFullScreen() && !equalsInfo(defaultConfig.programSeriesInfo, programSeriesInfo)) {
            defaultConfig.ProgramIsChange = true;
        }

        LogUtils.i(TAG, "playVideo: index=" + index + " position=" + position);
        if (updateState) {
            updatePlayStatus(PLAY_TYPE_SERIES, index, position);
        } else {
            updatePlayStatus(PLAY_TYPE_ALTERNATE, index, position);
        }

        defaultConfig.programSeriesInfo = programSeriesInfo;

        if (programSeriesInfo != null) {
            List<SubContent> programsInfos = programSeriesInfo.getData();
            if (programsInfos != null && programsInfos.size() > index) {
                SubContent program = programsInfos.get(index);
                if (mNewTVLauncherPlayerSeekbar != null) {
                    boolean hasMutipleProgram = programsInfos.size() > 1;
                    mNewTVLauncherPlayerSeekbar.setProgramName(program.getTitle(),
                            hasMutipleProgram);
                }

                if (mNewTVLauncherPlayerSeekbar != null) {
                    mNewTVLauncherPlayerSeekbar.setProgramName(program.getTitle(),
                            false);
                }

                if (mLoading != null) {
                    mLoading.setProgramName(program.getTitle());
                }

                playIndex(index);
                String seriesUUID = "";
                if (program.getUseSeriesSubUUID()) {
                    seriesUUID = program.getSeriesSubUUID();
                } else {
                    seriesUUID = programSeriesInfo.getContentUUID();
                }
                mVodPresenter.checkVod(program.getContentUUID(), seriesUUID, defaultConfig
                        .isAlternate);

                startLoading();
                isNeedStartActivity(isNeedStartActivity, programSeriesInfo, index);
            } else {
                LogUtils.i(TAG, "playVideo: programsInfos == null || programsInfos.size() <= " +
                        "index");
                onError(PlayerErrorCode.PROGRAM_SERIES_EMPTY, PlayerErrorCode
                        .getErrorDesc
                                (getContext(), PlayerErrorCode.PROGRAM_SERIES_EMPTY));
            }
        }
    }

    public void playLive(LiveInfo liveInfo, boolean isNeedStartActivity, LiveListener listener) {
        stop();

        unshowLoadBack = false;
        mLiveListener = listener;
        defaultConfig.liveInfo = liveInfo;
        LogUtils.i(TAG, "playlive playVideo");
        setCurrentVideoState(PlayerContract.STATE_NORMAL);
        updatePlayStatus(PLAY_TYPE_LIVE, 0, 0);
        mLivePresenter.checkLive(liveInfo);
        if (mLoading != null) {
            mLoading.setProgramName(liveInfo.getTitle());
        }
        startLoading();
        isNeedStartActivity(isNeedStartActivity, null, 0);

        mPlayerTimer.start();
    }

    private void stopAllRequest() {
        if (mVodPresenter != null) {
            mVodPresenter.stop();
        }
        if (mLivePresenter != null) {
            mLivePresenter.stop();
        }
        if (mAlternatePresenter != null) {
            mAlternatePresenter.stop();
        }
    }

    private void playAlive(LiveInfo liveInfo) {
        stop();

        if (liveInfo == null) {
            onError(PlayerErrorCode.LIVE_INFO_EMPTY, PlayerErrorCode.getErrorDesc(getContext(),
                    PlayerErrorCode
                            .LIVE_INFO_EMPTY));
            return;
        }


        VideoDataStruct videoDataStruct = new VideoDataStruct();
        videoDataStruct.setPlayType(PlayerConstants.PLAYTYPE_LIVE);
        if (liveInfo.isTimeShift()) {
            videoDataStruct.setPlayUrl(liveInfo.getLiveUrl());
        } else {
            videoDataStruct.setPlayUrl(liveInfo.getDefaultLiveUrl());
        }

        videoDataStruct.setDataSource(PlayerConstants.DATASOURCE_ICNTV);
        videoDataStruct.setDeviceID(Constant.UUID);
        videoDataStruct.setKey(liveInfo.getKey());
        videoDataStruct.setContentUUID(liveInfo.getContentUUID());
        mNewTVLauncherPlayer.playAlive(getContext(), defaultConfig.videoFrameLayout, liveInfo,
                mLiveCallBackEvent,
                videoDataStruct);
        if (!liveInfo.isAlwaysPlay()) {
            if (mLiveTimer == null) {
                mLiveTimer = new LiveTimer();
            }
            mLiveTimer.setCallback(this);
            mLiveTimer.setLiveInfo(defaultConfig.liveInfo);
        }
    }

    /*
     * 播放节目
     * programDetailInfo 节目信息
     * position 从什么位置开始播放
     * */
    private void playProgramSingle(Content programDetailInfo, int position, boolean
            openActivity, boolean updateState) {
        unshowLoadBack = false;
        LogUtils.i(TAG, "playProgram: ");
        if (programDetailInfo == null) {
            return;
        }

        if (isFullScreen() && !equalsInfo(defaultConfig.programSeriesInfo, programDetailInfo)) {
            defaultConfig.ProgramIsChange = true;
        }

        if (updateState) {
            updatePlayStatus(PLAY_TYPE_SINGLE, 0, position);
        } else {
            updatePlayStatus(PLAY_TYPE_ALTERNATE, 0, position);
        }
        defaultConfig.programSeriesInfo = programDetailInfo;

        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.setProgramName(programDetailInfo.getTitle(), false);
        }

        if (mLoading != null) {
            mLoading.setProgramName(programDetailInfo.getTitle());
        }

        mVodPresenter.checkVod(programDetailInfo.getContentUUID(),
                programDetailInfo.getCsContentIDs().split("\\|")[0], defaultConfig.isAlternate);

        startLoading();

        isNeedStartActivity(openActivity, programDetailInfo, 0);
    }

    protected void PlayTypeChange(int type) {
        switch (type) {
            case PLAY_TYPE_ALTERNATE:
                if (alterTitle != null) {
                    alterTitle.setVisibility(VISIBLE);
                }
                if (alterChannel != null) {
                    alterChannel.setVisibility(VISIBLE);
                }
                break;
            default:
                if (alterTitle != null) {
                    alterTitle.setVisibility(GONE);
                }
                if (alterChannel != null) {
                    alterChannel.setVisibility(GONE);
                }
                break;
        }
    }

    /**
     * 开始播放时进行状态和行为变更
     * type 1为单节目 2为节目集 3为直播
     */
    private void updatePlayStatus(int type, int index, int position) {
        Log.i(TAG, "updatePlay position=" + position + "   ====" + type + ":" + index);
        setHintTextVisible(GONE);
        mIsPrepared = false;
        dismissChildView();

        if (defaultConfig.playType != type) {
            if(defaultConfig.isAlternate){
                uploadExitLbLog();
            }
            PlayTypeChange(type);
        }

        switch (type) {
            case PLAY_TYPE_SINGLE:
                defaultConfig.playType = PLAY_TYPE_SINGLE;
                defaultConfig.isAlternate = false;
                defaultConfig.isLiving = false;
                break;
            case PLAY_TYPE_SERIES:
                defaultConfig.playType = PLAY_TYPE_SERIES;
                defaultConfig.isAlternate = false;
                defaultConfig.isLiving = false;
                break;
            case PLAY_TYPE_LIVE:
                defaultConfig.playType = PLAY_TYPE_LIVE;
                defaultConfig.isAlternate = false;
                defaultConfig.isLiving = true;
                break;
            case PLAY_TYPE_ALTERNATE:
                defaultConfig.playType = PLAY_TYPE_ALTERNATE;
                defaultConfig.isAlternate = true;
                defaultConfig.isLiving = false;
                break;
        }

        if (!defaultConfig.isLiving) {
            addHistory();
            PlayerConfig.getInstance().setJumpAD(NeedJumpAd);
            NeedJumpAd = false;
            if (isFullScreen()) {
                createMenuGroup();
            } else if (getWidth() == 0) {
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                        .OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.i(TAG, "onGlobalLayout: ");
                        if (isFullScreen()) {
                            createMenuGroup();
                        }
                        NewTVLauncherPlayerView.this.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

        defaultConfig.playIndex = index;
        mHistoryPostion = position;
    }

    private void isNeedStartActivity(boolean isNeedStartActivity, Content
            programDetailInfo, int index) {
        if (isNeedStartActivity) {
            Intent intent = Player.get().getPlayerActivityIntent();
            Bundle bundle = new Bundle();
            if (programDetailInfo != null) {
                bundle.putSerializable("programSeriesInfo", programDetailInfo);
            }
            bundle.putInt("index", index);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i(TAG, "onKeyDown: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mIsLoading) {
                    return true;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                LogUtils.i(TAG, "onKeyDown: KEYCODE_DPAD_LEFT");
                if (!mIsPrepared) {
                    return true;
                }
                if (mShowingChildView == SHOWING_NO_VIEW) {
                    showSeekBar(mIsPause, true);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                LogUtils.i(TAG, "onKeyDown: KEYCODE_DPAD_RIGHT");
                if (!mIsPrepared) {
                    return true;
                }
                if (mShowingChildView == SHOWING_NO_VIEW) {
                    showSeekBar(mIsPause, true);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!mIsPrepared || defaultConfig.playType == PLAY_TYPE_LIVE || defaultConfig
                        .isAlternate) {
                    LogUtils.i(TAG, "onKeyDown: mIsPrepared is false");
                    return true;
                }
                if (defaultConfig.playType == PLAY_TYPE_SERIES) {
                    if (defaultConfig.programSeriesInfo == null || defaultConfig
                            .programSeriesInfo.getData() == null ||
                            defaultConfig.programSeriesInfo.getData().size() <= 1) {
                        LogUtils.i(TAG, "onKeyDown: mProgramSeriesInfo.getData()==null");
                        return true;
                    }
                } else if (defaultConfig.playType == PLAY_TYPE_SINGLE) {
                    return true;
                }

                if (mShowingChildView == SHOWING_SEEKBAR_VIEW) {
                    dismissChildView();
                    return true;
                } else if (mShowingChildView == SHOWING_EXIT_VIEW) {

                } else if (mShowingChildView == SHOWING_NUMBER_PROGRAM_SELECTOR) {

                } else if (mShowingChildView == SHOWING_NO_VIEW) {
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSeekBar(boolean isPause, boolean isShow) {
        if (isLiving() && defaultConfig.liveInfo != null && !defaultConfig.liveInfo.isTimeShift()) {
            return;
        }

        if (defaultConfig.isAlternate) {
            return;
        }

        if (mShowingChildView != SHOWING_NO_VIEW) {
            return;
        }
        if (mNewTVLauncherPlayerSeekbar != null) {
            if (isPause) {
                showPauseImage();
            } else {
                hidePauseImage();
            }

            if (isShow) {
                if (mNewTVLauncherPlayerSeekbar.getVisibility() == GONE ||
                        mNewTVLauncherPlayerSeekbar.getVisibility() == INVISIBLE) {
                    mNewTVLauncherPlayerSeekbar.show();
                }
            } else {
                if (mNewTVLauncherPlayerSeekbar.getVisibility() == VISIBLE) {
                    mNewTVLauncherPlayerSeekbar.dismiss();
                }
            }
        }
    }

    private void dismissChildView() {
        if (mShowingChildView == SHOWING_NO_VIEW) return;
        LogUtils.i(TAG, "dismissChildView: " + mShowingChildView);
        int current = mShowingChildView;
        mShowingChildView = SHOWING_NO_VIEW;
        boolean interrupt = false;
        if (defaultConfig.widgetMap != null) {
            for (FocusWidget focusWidget : defaultConfig.widgetMap.values()) {
                if (current == focusWidget.getId()) {
                    focusWidget.onBackPressed();
                    interrupt = true;
                    break;
                }
            }
        }

        if (!interrupt) {
            switch (current) {
                case SHOWING_SEEKBAR_VIEW:
                    showSeekBar(mIsPause, false);
                    break;
                case SHOWING_PROGRAM_TREE:
                    if (menuGroupPresenter != null && menuGroupPresenter.isShow()) {
                        menuGroupPresenter.gone();
                    }
                    break;
                case SHOWING_TIP_VIEW:
                    if (mNewTvTipView != null && mNewTvTipView.getVisibility() == VISIBLE) {
                        mNewTvTipView.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (defaultConfig!=null && !defaultConfig.isLiving) {
            mPlayerTimer.reset();
        }

        if (isFullScreen() && buyGoodsBusiness != null && buyGoodsBusiness.isShow()
                && buyGoodsBusiness.dispatchKeyEvent(event)) {
            return true;
        }


        if (mPlayerContract != null && mPlayerContract.processKeyEvent(event) &&
                mShowingChildView == SHOWING_TIP_VIEW) {
            return true;
        }

        /**
         * 适配讯码盒子
         * 正常盒子按返回键返回KeyEvent.KEYCODE_BACK
         * 讯码盒子非长按返回KeyEvent.KEYCODE_ESCAPE  长按返回KeyEvent.KEYCODE_ESCAPE KeyEvent.KEYCODE_BACK
         */
        if (Libs.get().getFlavor().equals(DeviceUtil.XUN_MA)) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (mShowingChildView == SHOWING_SEEKBAR_VIEW
                                || mShowingChildView == SHOWING_PROGRAM_TREE) {
                            dismissChildView();
                        } else {
                            onBackPressed();
                        }
                    }
                    return true;
            }
        }

        //有限处理BACK按键事件，关闭进度条显示
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (mShowingChildView != SHOWING_NO_VIEW) {
                    dismissChildView();
                } else {
                    onBackPressed();
                }
            }
            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (defaultConfig.playType == PLAY_TYPE_LIVE) {
                return true;
            }
        }

        if (defaultConfig.widgetMap != null) {
            Collection<FocusWidget> widgets = defaultConfig.widgetMap.values();
            for (FocusWidget widget : widgets) {
                if (widget.isOverride(event.getKeyCode())) {
                    if (widget.isRegisterKey(event)) {
                        if (!widget.isShowing()) {
                            widget.show(this);
                            widget.requestDefaultFocus();
                            NewTVLauncherPlayerViewManager.getInstance().setShowingView(widget
                                    .getId());
                        } else {
                            if (widget.isToggleKey(event.getKeyCode())) {
                                dismissChildView();
                            } else {
                                widget.dispatchKeyEvent(event);
                            }
                        }
                    }
                    return true;
                }
            }
        }

        if (mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isADPlaying()
                && KeyEventUtils.FullScreenAllowKey(event)) {
            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent
                .KEYCODE_DPAD_DOWN) {
            if (mShowingChildView == SHOWING_SEEKBAR_VIEW) {
                dismissChildView();
            }
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent
                .KEYCODE_ENTER) {
            if (mShowingChildView == SHOWING_NO_VIEW || mShowingChildView == SHOWING_SEEKBAR_VIEW) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    onKeyUp(event.getKeyCode(), event);
                }
                return true;
            }
        }

        if (mShowingChildView != SHOWING_NO_VIEW) {
            if (defaultConfig.widgetMap != null && defaultConfig.widgetMap.containsKey
                    (mShowingChildView)) {
                FocusWidget focusWidget = defaultConfig.widgetMap.get(mShowingChildView);
                if (focusWidget != null) {
                    focusWidget.dispatchKeyEvent(event);
                    return true;
                }
            }
        }

        if (mShowingChildView == SHOWING_SEEKBAR_VIEW &&
                (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            mNewTVLauncherPlayerSeekbar.dispatchKeyEvent(event);
            return true;
        }

        if (menuGroupPresenter != null && menuGroupPresenter.dispatchKeyEvent(event)) {
            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (defaultConfig.playType == PLAY_TYPE_ALTERNATE) {
                return true;
            }
        }

        if (isFullScreen()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                    if (onBackPressed()) {
//                        return true;
//                    }
//                }
                if (NewTVLauncherPlayerViewManager.getInstance().onKeyUp(event.getKeyCode(),
                        event)) {
                    return true;
                }
            } else {
                if (NewTVLauncherPlayerViewManager.getInstance().onKeyDown(event.getKeyCode(),
                        event)) {
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtils.i(TAG, "onKeyUp: " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (isFullScreen() && isTrySee) {
                goToBuy();
                return true;
            }
        }

        if (!mIsPrepared) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                NewTVLauncherPlayerViewManager.getInstance().release();
            }
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                LogUtils.i(TAG, "onKeyUp: KEYCODE_DPAD_CENTER");
                if (mIsLoading) {
                    return true;
                }
                if (mShowingChildView == SHOWING_SEEKBAR_VIEW || mShowingChildView ==
                        SHOWING_NO_VIEW) {
                    if (mNewTVLauncherPlayer == null) {
                        return true;
                    }

                    if (isLiving() && defaultConfig.liveInfo != null && !defaultConfig.liveInfo
                            .isTimeShift()) {
                        return true;
                    }

                    if (defaultConfig.isAlternate) {
                        return true;
                    }

                    if (mNewTVLauncherPlayer.isPlaying()) {
                        pause();
                    } else {
                        start();
                    }
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_BACK:
                LogUtils.i(TAG, "onKeyDown: KEYCODE_BACK");
                if (seekVisibleExit()) return true;
                break;
            case KeyEvent.KEYCODE_MENU:
                dismissChildView();
                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * 进度条显示时返回键触发
     *
     * @return
     */
    private boolean seekVisibleExit() {
        if (mNewTVLauncherPlayerSeekbar.getVisibility() == VISIBLE) {
            mNewTVLauncherPlayerSeekbar.dismiss();
            return true;
        }
        if (mShowingChildView != SHOWING_NO_VIEW) {
            dismissChildView();
            return true;
        } else {
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
        return false;
    }

    public boolean isLiving() {
        return defaultConfig.isLiving;
    }

    // add by lxf
    private void playVodNext() {
        if (isTrySee) {
            goToBuy();
            return;
        }
        if (defaultConfig.playType == PLAY_TYPE_SINGLE || defaultConfig.playType ==
                PLAY_TYPE_LIVE) {
            if (defaultConfig.playType != PLAY_TYPE_LIVE) {
                addHistory();
            }
            AllComplete(false, "播放结束");

            if (defaultConfig.startIsFullScreen) {
                NewTVLauncherPlayerViewManager.getInstance().release();
            }
        } else if (defaultConfig.playType == PLAY_TYPE_SERIES) {
            int next = defaultConfig.playIndex + 1;
            if (next <= defaultConfig.programSeriesInfo.getData().size() - 1) {
                playProgramSeries(defaultConfig.programSeriesInfo, false, next, 0, true);
                if (listener != null && listener.size() > 0) {
                    for (IPlayProgramsCallBackEvent l : listener) {
                        l.onNext(defaultConfig.programSeriesInfo.getData().get(next), next,
                                true);
                    }
                }
            } else {
                addHistory();
                if (listener != null && listener.size() > 0) {
                    for (IPlayProgramsCallBackEvent l : listener) {
                        l.onNext(null, next, false);
                    }
                }
                if (isNextPlay) {
                    RxBus.get().post(Constant.IS_VIDEO_END, true);
                } else {
                    if (defaultConfig.startIsFullScreen) {
                        NewTVLauncherPlayerViewManager.getInstance().release();
                    }
                    AllComplete(false, "播放结束");
                }
            }
        } else if (defaultConfig.playType == PLAY_TYPE_ALTERNATE) {
            if (!mAlternatePresenter.playNext()) {
                AllComplete(false, "播放结束");
            }
        }
    }

    protected void playIndex(int index) {

    }

    protected void AllComplete(boolean isError, String info) {
        if (defaultConfig.isAlternate) {

        } else {
            if (mNewTVLauncherPlayer != null) {
                mNewTVLauncherPlayer.release();
                mNewTVLauncherPlayer = null;
            }
        }

    }

    public int getShowingView() {
        return mShowingChildView;
    }

    public void setShowingView(int showingView) {
        mPlayerContract.setCurrentShow(showingView);
        if (mShowingChildView == showingView) return;
        LogUtils.i(TAG, "setShowingView: showingView=" + showingView);
        if (mShowingChildView != SHOWING_NO_VIEW) {
            dismissChildView();
        }

        mShowingChildView = showingView;
    }

    public void tip(View view, int align) {
        if (mNewTvTipView != null) {
            setShowingView(SHOWING_TIP_VIEW);
            mNewTvTipView.show(align, view);
        }
    }

    public void dismissTipView() {
        if (mNewTvTipView != null && mNewTvTipView.getVisibility() == VISIBLE) {
            mNewTvTipView.dismiss();
            if (mAlternatePresenter == null) {
                LogUtils.e(TAG, "mAlternatePresenter is null");
                return;
            }
            mAlternatePresenter.alternateTipComplete();
        }
    }

    private void startLoading() {
        if (mLoading != null) {
            mLoading.show(unshowLoadBack, NewTVLauncherPlayerLoading.allScrFlag);
            bringChildToFront(mLoading);
            mIsLoading = true;
        }
    }

    private void stopLoading() {
        if (mLoading != null) {
            mLoading.dismiss();
            mIsLoading = false;
        }
    }

    private void showPauseImage() {
        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.setSeekPlayIcon();
        }
    }

    private void hidePauseImage() {
        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.setSeekPauseIcon();
        }
    }

    public void start() {
        hidePauseImage();
        if (mNewTVLauncherPlayer != null)
            mNewTVLauncherPlayer.start();

        mIsPause = false;
        showSeekBar(false, false);
    }

    public void pause() {
        showPauseImage();
        if (mNewTVLauncherPlayer != null)
            mNewTVLauncherPlayer.pause();

        mIsPause = true;
        showSeekBar(true, true);
    }

    public void stop() {
        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.stop();
        }
        stopAllRequest();
    }

    public int getIndex() {
        return defaultConfig.playIndex;
    }

    public int getDuration() {
        if (mNewTVLauncherPlayer != null)
            return mNewTVLauncherPlayer.getDuration();

        return 0;
    }

    public int getCurrentPosition() {
        if (mNewTVLauncherPlayer != null)
            return mNewTVLauncherPlayer.getCurrentPosition();

        return 0;
    }

    public void addListener(IPlayProgramsCallBackEvent l) {
        if (listener == null) {
            listener = new ArrayList<>();
        }
        listener.add(l);
    }

    /**
     * 保存播放记录  在播放单节目和节目集的时候调用
     */
    private void addHistory() {
        if (isLiving()) return;

        if (defaultConfig.isAlternate) {
            if (mAlternatePresenter != null) {
                mAlternatePresenter.addHistory();
            }
//            return;
        }

        if (defaultConfig.programSeriesInfo == null) {
            return;
        }

        int index = CmsUtil.translateIndex(defaultConfig.programSeriesInfo, getIndex());

        RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(index,
                getCurrentPosition(), defaultConfig.programSeriesInfo.getContentUUID()));

        if (defaultConfig.programSeriesInfo == null
                || getDuration() <= 0 || getCurrentPosition() <= 0) {
            return;
        }
        int historyPosition = 0;
        if (getCurrentPosition() < getDuration() - 30 * 1000) {
            historyPosition = getCurrentPosition();
        } else {
            Log.i(TAG, "History postion reach the end, ignore");
        }

        if (getDuration() != 0) {
            Player.get().onFinish(defaultConfig.programSeriesInfo, index, historyPosition,
                    getDuration());
        }
    }

    public boolean isADPlaying() {
        return mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isADPlaying();
    }

    public boolean isPlaying() {
        return mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isPlaying();
    }

    public void setHintTextVisible(int visible) {
    }

    public void setVideoSilent(boolean isSilent) {
        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.setVideoSilent(isSilent);
        }
    }


    @Override
    public void onVodchkResult(VideoDataStruct videoDataStruct, String contentUUID) {
        if (mNewTVLauncherPlayer == null) {
            mNewTVLauncherPlayer = new NewTVLauncherPlayer();
        }
        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.setmNewTVLauncherPlayer(mNewTVLauncherPlayer);
        }

        if (videoDataStruct.isTrySee() && !defaultConfig.isAlternate) {
            isTrySee = true;
            hintVip.setVisibility(View.VISIBLE);
            String freeDuration = videoDataStruct.getFreeDuration();
            if (!TextUtils.isEmpty(freeDuration) && Integer.parseInt(freeDuration) > 0) {
                if (mNewTVLauncherPlayerSeekbar != null) {
                    int duration = Integer.parseInt(videoDataStruct.getFreeDuration());
                    mNewTVLauncherPlayerSeekbar.setFreeDuration(duration, freeDurationListener);
                }
            } else {
//                goToBuy();
                onChkError(PlayerErrorCode.USER_NOT_BUY,PlayerErrorCode.getErrorDesc
                        (getContext(),PlayerErrorCode.USER_NOT_BUY));
                return;
            }
        } else {
            isTrySee = false;
            hintVip.setVisibility(View.GONE);
            mNewTVLauncherPlayerSeekbar.setFreeDuration(0, null);
        }


        if (defaultConfig.playType != PLAY_TYPE_ALTERNATE) {
            if (buyGoodsBusiness == null) {
                buyGoodsBusiness = new BuyGoodsBusiness(getContext().getApplicationContext(), this);
            }
            buyGoodsBusiness.getAd();
        }

        if (defaultConfig.programSeriesInfo != null && (Constant.CONTENTTYPE_CG.equals
                (defaultConfig.programSeriesInfo.getContentType())
                || Constant.CONTENTTYPE_TV.equals(defaultConfig.programSeriesInfo.getContentType
                ()))) {
            videoDataStruct.setSeriesId(defaultConfig.programSeriesInfo.getContentID());
            ADConfig.getInstance().setSeriesID(defaultConfig.programSeriesInfo.getContentID(),
                    false);
        }
        if (defaultConfig.programSeriesInfo != null) {
            ADConfig.getInstance().setVideoType(defaultConfig.programSeriesInfo.getVideoType());
            ADConfig.getInstance().setVideoClass(defaultConfig.programSeriesInfo.getVideoClass());
        }

        videoDataStruct.setAlternate(defaultConfig.isAlternate, defaultConfig.isFirstAlternate);
        videoDataStruct.setAlternateId(defaultConfig.alternateID);
        videoDataStruct.setHistoryPosition(mHistoryPostion);
        mNewTVLauncherPlayer.play(getContext(), defaultConfig.videoFrameLayout, mCallBackEvent,
                videoDataStruct);
    }

    @Override
    public void liveChkResult(LiveInfo liveInfo) {
        defaultConfig.liveInfo = liveInfo;
        playAlive(liveInfo);
    }

    @Override
    public void onChkError(String code, String desc) {
        if (!TextUtils.isEmpty(code)) {
            switch (code) {
                case PlayerErrorCode.USER_NOT_BUY:
                case PlayerErrorCode.USER_NOT_LOGIN:
                case PlayerErrorCode.USER_TOKEN_IS_EXPIRED:
                    isTrySee = true;
                    break;
                default:
                    isTrySee = false;
            }
            onError(code, desc);
        } else {
            onError("000", "UNKONWN ERROR");
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {
        onError(code, desc);
    }

    @Override
    public void onChange(String current, String start, String end, boolean isComplete) {
        if (isComplete) {
            if (mLiveListener != null) {
                mLiveListener.onComplete();
            }
            release();
        }
        if (mLiveListener != null) {
            mLiveListener.onTimeChange(current, end);
        }

    }

    private void callBackScreenListener(boolean enterFullScreen) {
        if (screenListeners != null) {
            for (ScreenListener screenListener : screenListeners) {
                if (screenListener != null) {
                    if (enterFullScreen) {
                        screenListener.enterFullScreen();
                    } else {
                        screenListener.exitFullScreen();
                    }
                }

            }
        }
    }

    public void registerScreenListener(ScreenListener listener) {
        if (screenListeners == null) {
            screenListeners = new ArrayList<>();
        }
        screenListeners.add(listener);
    }

    public void unregisterScreenListener(ScreenListener listener) {
        if (screenListeners != null) {
            screenListeners.remove(listener);
        }
    }

    private void goToBuy() {
        ExterPayBean exterPayBean = new ExterPayBean();
        exterPayBean.setContentUUID(defaultConfig.programSeriesInfo.getContentUUID());
        exterPayBean.setContentid(defaultConfig.programSeriesInfo.getContentID());
        exterPayBean.setContentType(defaultConfig.programSeriesInfo.getContentType());
        exterPayBean.setVipProductId(defaultConfig.programSeriesInfo.getVipProductId());
        exterPayBean.setMAMID(defaultConfig.programSeriesInfo.getMAMID());
        exterPayBean.setVipFlag(defaultConfig.programSeriesInfo.getVipFlag());
        exterPayBean.setAction(getContext().getClass().getName());
        exterPayBean.setTitle(defaultConfig.programSeriesInfo.getTitle());

        String vipFlag = defaultConfig.programSeriesInfo.getVipFlag();
        if (UserStatus.isLogin() && (VipCheck.VIP_FLAG_VIP.equals(vipFlag) ||
                VipCheck.VIP_FLAG_VIP_BUY.equals(vipFlag))) {
            Intent intent = new Intent();
            intent.setClassName(getContext(), "tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity");
            intent.putExtra("ispay", true);
            intent.putExtra("payBean", exterPayBean);
            getContext().startActivity(intent);
        } else if (UserStatus.isLogin() && VipCheck.VIP_FLAG_BUY.equals(vipFlag)) {
            Intent intent = new Intent();
            intent.setClassName(getContext(), "tv.newtv.cboxtv.uc.v2.Pay.PayOrderActivity");
            intent.putExtra("ispay", true);
            intent.putExtra("payBean", exterPayBean);
            getContext().startActivity(intent);
        } else if (!UserStatus.isLogin() && (VipCheck.VIP_FLAG_VIP.equals(vipFlag)
                || VipCheck.VIP_FLAG_BUY.equals(vipFlag) || VipCheck.VIP_FLAG_VIP_BUY.equals
                (vipFlag))) {
            Intent intent = new Intent();
            intent.setClassName(getContext(), "tv.newtv.cboxtv.uc.v2.LoginActivity");
            intent.putExtra("ispay", true);
            intent.putExtra("payBean", exterPayBean);
            intent.putExtra("isAuth", true);
            getContext().startActivity(intent);
        }
    }

    public void setVideoPlayNext(boolean isNextPlay) {
        this.isNextPlay = isNextPlay;
    }

    public void setOnPlayerStateChange(OnPlayerStateChange onViewVisibleChange) {
        mPlayerContract.setOnPlayerStateChange(onViewVisibleChange);
    }


    protected void changeAlternate(String contentId, String title, String channelCode) {
        playAlternate(contentId, title, channelCode);
    }

    public void setChangeAlternateListen(ChangeAlternateListener listen) {
        mChangeAlternateListener = listen;
    }

    @Override
    public void onAlternateResult(List<Alternate> alternateList, int currentPlayIndex, String
            title, String channelId) {
        if (isReleased) return;
        updatePlayStatus(PLAY_TYPE_ALTERNATE, currentPlayIndex, 0);

        if (defaultConfig.alternateCallback != null) {
            defaultConfig.alternateCallback.onAlternateResult(alternateList);
        }

        if (mAlternatePresenter != null && mAlternatePresenter
                .getCurrentAlternate() != null) {
            if (alterTitle != null) {
                alterTitle.setText(defaultConfig.useAlternateUI ? String.format(Locale.getDefault(),
                        "即将播放 %s",
                        mAlternatePresenter.getCurrentAlternate().getTitle()) : "");
            }
            if (defaultConfig.isFullScreen) {
                if (mNewTvAlterChange != null) {
                    mNewTvAlterChange.setTitleText(String.format(Locale.getDefault(),
                            "%s %s",
                            mAlternatePresenter.getCurrentAlternate().getStartTime(),
                            mAlternatePresenter.getCurrentAlternate().getTitle()));
                }
            }
        }
    }

    @Override
    public void onAlternateTimeChange(String current, String end) {

    }

    @Override
    public void onAlterItemResult(String contentId, Content content, boolean isLive, boolean
            isFirst) {
        if (isReleased) return;
        setSeriesInfo(content);

        if (alterTitle != null) {
            if (!defaultConfig.isFullScreen) {
                alterTitle.setVisibility(VISIBLE);
            }
            alterTitle.setText(defaultConfig.useAlternateUI ? mAlternatePresenter
                    .getCurrentAlternate().getTitle() : "");
        }

        defaultConfig.isFirstAlternate = isFirst;

        if (!isLive) {
            if (defaultConfig.alternateCallback != null) {
                defaultConfig.alternateCallback.onPlayIndexChange(mAlternatePresenter
                        .getCurrentPlayIndex());
            }
            Long currentStartTime = CmsUtil.parse(mAlternatePresenter.getCurrentAlternate()
                    .getStartTime());
            playSingleOrSeries(0, (int) (System.currentTimeMillis() - currentStartTime), false);
        } else {
            LiveInfo liveInfo = new LiveInfo(content);
            playLive(liveInfo, false, null);
        }
    }

    @Override
    public void onAlternateError(String code, String desc) {
        stop();
        if (CmsErrorCode.CMS_NO_ONLINE_CONTENT.equals(code)) {
            ToastUtil.showToast(getContext(), "节目走丢了 请继续观看");
        } else {
            ToastUtil.showToast(getContext(), desc);
        }

        onError(code, desc);
    }

    public void setAlternateCallback(AlternateCallback callback) {
        defaultConfig.alternateCallback = callback;
    }


    /**
     * 提示用户是否休息一下
     */
    private void tipUserToRest() {
        AlertDialog alertDialog =
                TipDialog.showBuilder(Player.get().getCurrentActivity(),
                        5,
                        "您已观看很久了，请问是否休息片刻呢？",
                        new TipDialog.TipListener() {
                            @Override
                            public void onClick(boolean timeOver, boolean isOK) {
                                if (isOK) {
                                    if (mNewTVLauncherPlayer != null) {
                                        mPlayerTimer.cancel();
                                        mNewTVLauncherPlayer.release();
                                        mNewTVLauncherPlayer = null;
                                    }
                                    if (mAlternatePresenter != null) {
                                        mAlternatePresenter.destroy();
                                        mAlternatePresenter = null;
                                    }
                                    if (defaultConfig.startIsFullScreen) {
                                        NewTVLauncherPlayerViewManager.getInstance().release();
                                    } else {
                                        if (defaultConfig.isFullScreen) {
                                            ExitFullScreen();
                                        }
                                    }

                                    onTipFinishPlay(timeOver);

                                } else {
                                    mPlayerTimer.reset();
                                }
                            }

                            @Override
                            public void onDismiss() {

                            }
                        });
    }

    private void uploadExitLbLog(){
        try {
            if(defaultConfig.isAlternate){
                LogUploadUtils.uploadLog(Constant.LOG_LB,"1,"+mAlternatePresenter.getCurrrentChannel());
            }
        }catch (Exception e){}
    }

    protected void onTipFinishPlay(boolean timeOver) {

    }

    @Override
    public void onKeepLookTimeChange(int currentSecond) {
        if (defaultConfig.isLiving) {
            if (currentSecond % Constant.TIP_LIVE_DURATION == 0) {
                //直播两小时以上
                tipUserToRest();
            }
        } else {
            if (currentSecond % Constant.TIP_VOD_DURATION == 0) {
                //普通点播轮播四小时以上
                tipUserToRest();
            }
        }
    }

    public interface ChangeAlternateListener {
        void changeAlternate(String contentId, String title, String channel);
    }

    public interface GetHaveADListener {
        // isHavaAD 专题通过判断是否有广告，决定是否显示播放器上的Title和Enter全屏提示
        void OnGetHaveADListener(boolean isHavaAD);
    }

    public interface OnPlayerStateChange {
        /**
         * @param fullScreen   是否为全屏状态
         * @param visible      当前播放器View显示状态
         * @param videoPlaying 是否为正片播放状态
         * @return true 消化掉当前事件，不再向下传递
         * false  不消化该事件，继续向下传递
         */
        boolean onStateChange(boolean fullScreen, int visible, boolean videoPlaying);

        /**
         * @param keyEvent 按键事件
         * @return true 消化掉该按键事件   false 不消化该按键事件
         */
        boolean processKeyEvent(KeyEvent keyEvent);
    }

    public static class PlayerViewConfig {
        public ViewGroup.LayoutParams layoutParams;     //布局属性
        public boolean isFullScreen;            //当前是否为全屏状态
        public boolean startIsFullScreen;       //开始时候是不是全屏状态
        public View defaultFocusView;           //进入全屏时候的默认焦点位置
        public PlayerCallback playerCallback;
        public boolean ProgramIsChange = false;          //是否在栏目树切换节目集
        public int playPosition;
        public String alternateID;
        public VPlayCenter playCenter;
        public Map<Integer, FocusWidget> widgetMap;
        public ExitVideoFullCallBack videoFullCallBack;
        public VideoExitFullScreenCallBack videoExitFullScreenCallBack;
        public boolean isAlternate;
        public boolean isFirstAlternate = false;
        public boolean hasTipAlternate = false;
        public boolean useAlternateUI = false;
        int defaultWidth;
        int defaultHeight;
        int playIndex;
        int playType;
        boolean isLiving;
        LiveInfo liveInfo;
        Content programSeriesInfo;
        VideoFrameLayout videoFrameLayout;
        boolean prepared = false;
        ViewParent parentViewGroup;      //父级容器
        private AlternateCallback alternateCallback;

        public boolean canUse() {
            return parentViewGroup != null && layoutParams != null;
        }

        @Override
        public String toString() {
            return "PlayerViewConfig{" +
                    "defaultWidth=" + defaultWidth +
                    ", defaultHeight=" + defaultHeight +
                    ", playIndex=" + playIndex +
                    ", playType=" + playType +
                    ", isLiving=" + isLiving +
                    ", liveInfo=" + liveInfo +
                    ", prepared=" + prepared +
                    ", isFullScreen=" + isFullScreen +
                    ", startIsFullScreen=" + startIsFullScreen +
                    ", playerCallback=" + playerCallback +
                    ", playPosition=" + playPosition +
                    ", isAlternate=" + isAlternate +
                    ", playCenter=" + playCenter +
                    ", videoFullCallBack=" + videoFullCallBack +
                    ", videoExitFullScreenCallBack=" + videoExitFullScreenCallBack +
                    '}';
        }
    }

    public class FreeDuration implements NewTVLauncherPlayerSeekbar.FreeDurationListener {
        @Override
        public void end() {
            if (isFullScreen()) {
                goToBuy();
                ExitFullScreen();
            } else {
                stop();
                onError(PlayerErrorCode.USER_NOT_BUY,
                        PlayerErrorCode.getErrorDesc(getContext(), PlayerErrorCode.USER_NOT_BUY));
            }
//            goToBuy();
        }
    }

}
