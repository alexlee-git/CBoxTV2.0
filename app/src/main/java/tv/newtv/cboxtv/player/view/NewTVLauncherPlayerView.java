package tv.newtv.cboxtv.player.view;

import android.app.Activity;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.newtv.ActivityStacks;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.model.MediaCDNInfo;
import tv.newtv.cboxtv.cms.details.model.ProgramDetailInfo;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.details.model.VideoPlayInfo;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.NetworkManager;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.player.Constants;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.NewTVLauncherPlayer;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.PlayerNetworkRequestUtils;
import tv.newtv.cboxtv.player.iPlayCallBackEvent;
import tv.newtv.cboxtv.player.menu.IMenuGroupPresenter;
import tv.newtv.cboxtv.player.menu.MenuGroupPresenter;
import tv.newtv.cboxtv.player.menu.MenuPopupWindow;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.model.LivePermissionCheckBean;
import tv.newtv.cboxtv.player.model.PlayCheckRequestBean;
import tv.newtv.cboxtv.player.model.VideoDataStruct;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VPlayCenter;
import tv.newtv.cboxtv.uc.bean.HistoryBean;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.utils.CmsLiveUtil;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.utils.Encryptor;
import tv.newtv.cboxtv.utils.KeyEventUtils;
import tv.newtv.cboxtv.utils.LivePermissionCheckUtil;
import tv.newtv.cboxtv.utils.LiveTimingUtil;
import tv.newtv.cboxtv.utils.ScreenUtils;
import tv.newtv.cboxtv.views.VideoFrameLayout;

//import tv.newtv.cboxtv.cms.details.PushManager;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerView extends FrameLayout {

    public static final int SHOWING_NO_VIEW = 0;
    public static final int SHOWING_SEEKBAR_VIEW = 2;
    public static final int SHOWING_NUMBER_PROGRAM_SELECTOR = 3;
    public static final int SHOWING_NAME_PROGRAM_SELECTOR = 4;
    public static final int SHOWING_SETTING_VIEW = 5;
    public static final int SHOWING_EXIT_VIEW = 6;
    public static final int SHOWING_PROGRAM_TREE = 7;
    private static final String TAG = NewTVLauncherPlayerView.class.getName();
    private static final int PROGRAM_SELECTOR_TYPE_NONE = 0; //不显示选集
    private static final int PROGRAM_SELECTOR_TYPE_NUMBER = 1; //显示数字选集
    private static final int PROGRAM_SELECTOR_TYPE_NAME = 2; //显示名称选集
    private static final int PLAY_SINGLE = 0;
    private static final int PLAY_SERIES = 1;
    private static int defaultWidth;
    private static int defaultHeight;
    private static boolean enterFullScreen = false;
    protected PlayerViewConfig defaultConfig;
    protected boolean startIsFullScreen = true;
    protected boolean ProgramIsChange = false;
    private long PLAYER_ID = 0;
    private VideoFrameLayout mPlayerFrameLayout;
    private NewTVLauncherPlayerLoading mLoading;
    private NewTVLauncherPlayerSeekbar mNewTVLauncherPlayerSeekbar;
    private int mShowingChildView = SHOWING_NO_VIEW;
    private boolean mIsLoading; //是否在缓冲
    private boolean mIsPause; //是否在暂停
    private boolean mIsPrepared;
    //    private ImageView mPauseImageView;
    private boolean mIsNeedPause;
    private ProgramSeriesInfo mProgramSeriesInfo; //当前播放的节目集信息
    private ProgramDetailInfo mProgramDetailInfo; //当前播放的节目信息
    private int mPlayingIndex; //当前播放第几集
    private String mContentUUid; //当前播放第几集
    private int mProgramSelectorType; //显示哪种选集
    private int mPlaySeriesOrSingle;
    private int mHistoryPostion;
    private String mPlayUrl; //进入播放指定清晰度的url
    private String mAppKey = BuildConfig.APP_KEY;
    private String mChannelId = BuildConfig.CHANNEL_ID;
    private boolean isLiving = false;
    private LiveInfo mLiveInfo;
    private boolean isReleased = false;
    private MenuPopupWindow menuPopupWindow;
    private IMenuGroupPresenter menuGroupPresenter;
    private NewTVLauncherPlayer mNewTVLauncherPlayer;
    private List<IPlayProgramsCallBackEvent> listener = new ArrayList<>();
    private boolean NeedJumpAd = false;
    private boolean unshowLoadBack = false;
    private iPlayCallBackEvent mLiveCallBackEvent = new iPlayCallBackEvent() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> definitionDatas) {
            LogUtils.i(TAG, "live onPrepared: ");
            mIsPrepared = true;
            stopLoading();

            if (mShowingChildView == SHOWING_PROGRAM_TREE) {
                menuGroupPresenter.gone();
            }

            if (!(isLiving() && mLiveInfo != null && !mLiveInfo.isTimeShift())) {
                mNewTVLauncherPlayerSeekbar.setDuration();
            }

            mHistoryPostion = 0;
        }

        @Override
        public void onCompletion() {
            LogUtils.i(TAG, "live onCompletion: ");
        }

        @Override
        public void onVideoBufferStart(String typeString) {

            LogUtils.i(TAG, "live onVideoBufferStart: typeString=" + typeString);
            startLoading();
        }

        @Override
        public void onVideoBufferEnd(String typeString) {
            Log.i(TAG, "live onVideoBufferEnd: typeString=" + typeString);
            if ("702".equals(typeString)) {
                unshowLoadBack = true;
            }
            if (!TextUtils.isEmpty(typeString) && (typeString.equals("702") ||
                    "ad_onPrepared".equals(typeString))) {
                stopLoading();
                hidePauseImage();
            }
        }

        @Override
        public void onTimeout() {
            LogUtils.i(TAG, "live onTimeout: ");
        }

        @Override
        public void changePlayWithDelay(int delay, String liveUrl) {

            if (mProgramSeriesInfo != null) {
                String playUrl = translateUrl(liveUrl, delay);
                LogUtils.d(TAG, "changePlayWithDelay video delay=" + delay + " url=" + playUrl);
                if (mLiveInfo.getmLiveUrl().equals(playUrl)) {
                    return;
                }
                mNewTVLauncherPlayer.release();
                mLiveInfo.setLiveUrl(playUrl);
                PlayerConfig.getInstance().setScreenChange(true);
                PlayerConfig.getInstance().setJumpAD(true);
                playLive(playUrl, mProgramSeriesInfo, false, 0, 0);
            }
        }

        @Override
        public void onError(int what, int extra, String msg) {
            LogUtils.i(TAG, "live onError: ");
        }
    };
    private Callback<ResponseBody> mPlayPermissionCheckCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                if (response == null) {
                    LogUtils.i(TAG, "onResponse: response==null");
                    if (!NetworkManager.getInstance().isConnected()) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .search_fail_agin), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .check_error), Toast.LENGTH_SHORT).show();
                    }
                    onError("-1", getContext().getResources().getString(R.string.check_error));
                    LogUtils.e("调用鉴权接口后没有返回数据");
                    return;
                }
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    LogUtils.i(TAG, "onResponse: responseBody==null");
                    if (!NetworkManager.getInstance().isConnected()) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .search_fail_agin), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .check_error), Toast.LENGTH_SHORT).show();
                    }
                    onError("-2", getContext().getResources().getString(R.string.check_error));
                    LogUtils.e("调用鉴权接口后没有返回数据");
                    return;
                }
                String responseStr = responseBody.string();
                LogUtils.i(TAG, "onResponse: " + responseStr);
                if (isLiving) {
                    LivePermissionCheckBean livePermissionCheck = GsonUtil.fromjson(responseStr,
                            LivePermissionCheckBean.class);
                    if ("0".equals(livePermissionCheck.getErrorCode())) {
                        if (livePermissionCheck.getData() != null && livePermissionCheck.getData
                                ().isEncryptFlag()) {
                            mLiveInfo.setKey(Encryptor.decrypt(Constant.APPSECRET,
                                    livePermissionCheck.getData().getDecryptKey()));
                        }
                        LogUtils.i(TAG, "getEncryptFlag:" + livePermissionCheck.getData()
                                .isEncryptFlag() + ",key=" + Encryptor.decrypt(Constant
                                .APPSECRET, livePermissionCheck.getData().getDecryptKey()));
                        playAlive(mLiveInfo);
                    } else {
                        onError(livePermissionCheck.getErrorCode(), getContext().getResources()
                                .getString(R.string.check_error));
                        LogUtils.i(TAG, "直播鉴权失败");
                    }
                    return;
                }


                mProgramDetailInfo = PlayerNetworkRequestUtils.getInstance()
                        .parsePlayPermissionCheckResult(responseStr);
                if (mProgramDetailInfo == null) {
                    LogUtils.i(TAG, "onResponse: programDetailInfo==null");
                    String errorCode = PlayerNetworkRequestUtils.getErrorCode(responseStr);
                    if (!NetworkManager.getInstance().isConnected()) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .search_fail_agin), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string
                                .check_error) + errorCode, Toast.LENGTH_SHORT).show();
                    }
                    onError(errorCode, getContext().getResources().getString(R.string
                            .check_error));
                    LogUtils.e("调用鉴权接口后没有返回数据");
                    return;
                }
                if (mProgramDetailInfo.getData().size() < 1) {
                    LogUtils.i(TAG, "onResponse: programDetailInfo.getData().size()<1");
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string
                            .program_info_no_data), Toast.LENGTH_SHORT).show();
                    onError("-3", getContext().getResources().getString(R.string
                            .program_info_no_data));
                    LogUtils.e("暂无节目内容");
                    return;
                }
                if (sendSharpnessesToSetting()) return;

                VideoDataStruct videoDataStruct = new VideoDataStruct();
                if (mProgramDetailInfo.getEncryptFlag()) {
                    videoDataStruct.setKey(Encryptor.decrypt(Constant.APPSECRET,
                            mProgramDetailInfo.getDecryptKey()));
                }
                LogUtils.i(TAG, "playViewgetEncryptFlag:" + mProgramDetailInfo.getEncryptFlag() +
                        ",key=" + Encryptor.decrypt(Constant.APPSECRET, mProgramDetailInfo
                        .getDecryptKey()));
                videoDataStruct.setPlayType(0);

                mContentUUid = mProgramDetailInfo.getContentUUID();
                videoDataStruct.setPlayUrl(mPlayUrl);
                videoDataStruct.setProgramId(mProgramDetailInfo.getContentUUID());

                String duration = mProgramDetailInfo.getDuration();
                if (!TextUtils.isEmpty(duration)) {
                    videoDataStruct.setDuration(Integer.parseInt(mProgramDetailInfo.getDuration()));
                }

                if (mPlaySeriesOrSingle == PLAY_SERIES) {
                    //videoDataStruct.setSeriesId(mProgramSeriesInfo.getContentUUID());
                    videoDataStruct.setSeriesId(mProgramDetailInfo.getProgramSeriesUUIDs());
                } else if (mPlaySeriesOrSingle == PLAY_SINGLE) {
                    videoDataStruct.setSeriesId(mProgramDetailInfo.getProgramSeriesUUIDs());
                }
                videoDataStruct.setDataSource(Constants.DATASOURCE_ICNTV);
                videoDataStruct.setDeviceID(Constant.UUID);
                videoDataStruct.setCategoryIds(mProgramDetailInfo.getCategoryIds());
                ADConfig.getInstance().setCategoryIds(mProgramDetailInfo.getCategoryIds());

                if (mNewTVLauncherPlayer == null) {
                    mNewTVLauncherPlayer = new NewTVLauncherPlayer();
                }

                if (mNewTVLauncherPlayerSeekbar != null) {
                    mNewTVLauncherPlayerSeekbar.setmNewTVLauncherPlayer(mNewTVLauncherPlayer);
                }

                mNewTVLauncherPlayer.play(getContext(), mPlayerFrameLayout, mCallBackEvent,
                        videoDataStruct);

            } catch (Exception e) {
                LogUtils.e(e.toString());
                onError("-3", getContext().getResources().getString(R.string
                        .check_error));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            LogUtils.e(TAG, "onFailure: " + t.toString());
            if (getContext() != null) {
                if (!NetworkManager.getInstance().isConnected()) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string
                            .search_fail_agin), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string
                            .check_error), Toast.LENGTH_SHORT).show();
                }

            }
            onError("-6", getContext().getResources().getString(R.string
                    .search_fail_agin));
        }
    };
    private iPlayCallBackEvent mCallBackEvent = new iPlayCallBackEvent() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> definitionDatas) {
            LogUtils.i(TAG, "onPrepared: ");
            mIsPrepared = true;
            stopLoading();
            mNewTVLauncherPlayerSeekbar.setDuration();
            if (mHistoryPostion > 0 && mHistoryPostion < mNewTVLauncherPlayer.getDuration() - 30
                    * 1000) {
                mNewTVLauncherPlayer.seekTo(mHistoryPostion);
            }
            mHistoryPostion = 0;
        }

        @Override
        public void onCompletion() {
            LogUtils.i(TAG, "onCompletion: ");
            /*
             *  大屏点播完成后，
             *  判断是否符合直播条件，如果符合则直播。 不符合则播放下一级
             */
            // 什么时候会修改Constant.isLiving的值？
            // 3. 大屏加载完一个点播文件，播放下一个之前，需要判断当前时间是否满足直播
            Constant.isLiving = false;

            playVodNext();
        }

        @Override
        public void onVideoBufferStart(String typeString) {
            LogUtils.i(TAG, "onVideoBufferStart: typeString=" + typeString);
            startLoading();
        }

        @Override
        public void onVideoBufferEnd(String typeString) {
            Log.i(TAG, "onVideoBufferEnd: typeString=" + typeString);
            if ("702".equals(typeString)) {
                unshowLoadBack = true;
            }
            if (!TextUtils.isEmpty(typeString) && (typeString.equals("702") || "ad_onPrepared"
                    .equals(typeString))) {
                stopLoading();
                hidePauseImage();
            }
        }

        @Override
        public void onTimeout() {
            LogUtils.i(TAG, "onTimeout: ");
        }

        @Override
        public void changePlayWithDelay(int delay, String url) {

        }

        @Override
        public void onError(int what, int extra, String msg) {
            LogUtils.i(TAG, "onError: ");
        }
    };
    private ViewGroup.LayoutParams defaultLayoutParams;
    private PlayerLocation mPlayerLocation;

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

    public void updateDefaultConfig(PlayerViewConfig config) {
        defaultConfig = config;
        if (config != null) {
            startIsFullScreen = config.startIsFullScreen;
            setLayoutParams(config.layoutParams);
            ((ViewGroup) config.parentViewGroup).addView(this, config.layoutParams);
        }
    }

    public void buildPlayerViewConfig() {
        if (defaultConfig == null) {
            defaultConfig = new PlayerViewConfig();
        }

        defaultConfig.layoutParams = getLayoutParams();
        defaultConfig.prepared = mIsPrepared;
        defaultConfig.playPosition = getCurrentPosition();
        defaultConfig.isFullScreen = isFullScreen();
        defaultConfig.startIsFullScreen = startIsFullScreen;
        defaultConfig.parentViewGroup = getParent();
    }

    public PlayerViewConfig getDefaultConfig() {
        if (defaultConfig == null) {
            buildPlayerViewConfig();
        }
        return defaultConfig;
    }

    protected void onError(String code, String messgae) {
        stopLoading();
    }

    protected boolean NeedRepeat() {
        return false;
    }

    public void ExitFullScreen() {
        if(!enterFullScreen) return;
        enterFullScreen = false;

        if (mPlayerLocation != null) {
            mPlayerLocation.destroy();
            mPlayerLocation = null;
        }

        Activity activity = ActivityStacks.get().getCurrentActivity();
        dismissChildView();

        final int screenWidth = activity.getWindow().getDecorView().getMeasuredWidth();
        final int screenHeight = activity.getWindow().getDecorView().getMeasuredHeight();

        ViewGroup.LayoutParams container = getLayoutParams();
        container.width = defaultWidth;
        container.height = defaultHeight;
        setLayoutParams(container);
        FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android.R.id
                .content);
        View rootView = frameLayout.getChildAt(0);

        setParentWidth(this, rootView, screenWidth - defaultWidth, screenHeight - defaultHeight,
                screenWidth, screenHeight, false, true);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout
                .getLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;
        frameLayout.setLayoutParams(layoutParams);

        updateUIPropertys(false);
        if (menuGroupPresenter != null && menuGroupPresenter instanceof MenuGroupPresenter) {
            ((MenuGroupPresenter) menuGroupPresenter).exitFullScreen();
        }

        NeedJumpAd = ProgramIsChange;

        if (mIsPause && mNewTVLauncherPlayer != null) {
            start();
        }
    }

    public void setFromFullScreen() {
        startIsFullScreen = true;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
        }
    }

    public boolean onBackPressed() {
        if (!startIsFullScreen) {
            if (isFullScreen()) {
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

        if(!enterFullScreen){
            defaultWidth = getLayoutParams().width;
            defaultHeight = getLayoutParams().height;
        }
    }

    private void setParentWidth(View view, View rootView, int changeWidth, int changeHeight, int
            maxWidth, int maxHeight, boolean bringFront, boolean isExit) {
        if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();

            if (bringFront) {
                view.bringToFront();
            }
            ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
            boolean isSame = (!isExit && viewGroup.getWidth() == defaultWidth && viewGroup
                    .getHeight() == defaultHeight) || (isExit && viewGroup.getWidth() ==
                    maxWidth &&
                    viewGroup.getHeight() == maxHeight);
            if (isSame) {
                layoutParams.width = isExit ? defaultWidth : maxWidth;
                layoutParams.height = isExit ? defaultHeight : maxHeight;
            } else {
                layoutParams.width = viewGroup.getWidth() + (isExit ? -changeWidth : changeWidth);
                layoutParams.height = viewGroup.getHeight() + (isExit ? -changeHeight :
                        changeHeight);
            }
            viewGroup.setLayoutParams(layoutParams);
            if (viewGroup == rootView) return;
            setParentWidth(viewGroup, rootView, changeWidth, changeHeight, maxWidth, maxHeight,
                    bringFront, isExit);
        }
    }

    public void EnterFullScreen(Activity activity, final boolean bringFront) {
        if(enterFullScreen) return;
        enterFullScreen = true;

        if (mPlayerLocation != null) {
            mPlayerLocation.destroy();
            mPlayerLocation = null;
        }

        final int screenWidth = activity.getWindow().getDecorView().getMeasuredWidth();
        final int screenHeight = activity.getWindow().getDecorView().getMeasuredHeight();

        final FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android
                .R.id.content);
        View rootView = frameLayout.getChildAt(0);

        setParentWidth(this, rootView, screenWidth - defaultWidth, screenHeight - defaultHeight,
                screenWidth, screenHeight, bringFront, false);

        ViewGroup.LayoutParams container = getLayoutParams();
        container.width = screenWidth;
        container.height = screenHeight;
        setLayoutParams(container);

        mPlayerLocation = PlayerLocation.build(this, bringFront);

        ProgramIsChange = false;

        createMenuGroup();

        if (mNewTVLauncherPlayer != null && !mNewTVLauncherPlayer.isADPlaying()) {
            if (menuGroupPresenter != null && menuGroupPresenter instanceof MenuGroupPresenter) {
                ((MenuGroupPresenter) menuGroupPresenter).showHinter();
            }
            showSeekBar(mIsPause);
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
        if (isFullScreen && menuGroupPresenter != null && menuGroupPresenter instanceof
                MenuGroupPresenter) {
            ((MenuGroupPresenter) menuGroupPresenter).enterFullScreen();
        }
        if (mPlayerFrameLayout != null) {
            mPlayerFrameLayout.updateTimeTextView(getResources().getDimensionPixelSize
                    (isFullScreen ? R.dimen.height_22px : R.dimen.height_12px));
        }
        if (mLoading != null) {
            mLoading.updatePropertys(getResources().getDimensionPixelSize(isFullScreen ? R.dimen
                    .height_22px : R.dimen.height_11px), isFullScreen);
        }
    }

    public boolean isFullScreen() {
        return this.getWidth() == ScreenUtils.getScreenW() && this.getHeight() == ScreenUtils
                .getScreenH();
    }

    public void destroy() {

        if (listener != null) {
            listener.clear();
        }

        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
            menuPopupWindow = null;
        }

        if (menuGroupPresenter != null) {
            menuGroupPresenter.release();
            menuGroupPresenter = null;
        }

        mLiveCallBackEvent = null;
        mCallBackEvent = null;

        mNewTVLauncherPlayer = null;
        mNewTVLauncherPlayerSeekbar = null;

        mProgramSeriesInfo = null;
        mProgramDetailInfo = null;
        onDetachedFromWindow();
    }

    public void release() {
        addHistory();
        Log.i(TAG, "release: ");
        if (listener != null) {
            listener.clear();
        }

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

        isReleased = true;

        destroy();
    }

    public boolean isReleased() {
        return isReleased;
    }

    private String translateUrl(String url, int delay) {
        return mLiveInfo.setTimeDelay(delay);
    }

    protected void initView(Context context) {
        mNewTVLauncherPlayer = new NewTVLauncherPlayer();

        View view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout
                .newtv_launcher_player_view, this);
        mPlayerFrameLayout = (VideoFrameLayout) view.findViewById(R.id.player_view_framelayout);
        mNewTVLauncherPlayerSeekbar = (NewTVLauncherPlayerSeekbar) view.findViewById(R.id
                .player_seekbar_area);

        mLoading = (NewTVLauncherPlayerLoading) view.findViewById(R.id.player_loading);
        mNewTVLauncherPlayerSeekbar.setmNewTVLauncherPlayer(mNewTVLauncherPlayer);

        updateUIPropertys(defaultConfig != null ? defaultConfig.isFullScreen : startIsFullScreen);

        PLAYER_ID = NewTVLauncherPlayerViewManager.getInstance().setPlayerView(this);
    }

    private boolean equalsInfo(ProgramSeriesInfo AInfo, ProgramSeriesInfo BInfo) {
        if (AInfo == null || BInfo == null) return false;
        if (TextUtils.isEmpty(AInfo.getContentUUID()) || TextUtils.isEmpty(BInfo.getContentUUID()
        )) {
            return false;
        }
        Log.e(TAG, "AInfo Id=" + AInfo.getContentUUID() + " BInfo Id=" + BInfo.getContentUUID());
        return AInfo.getContentUUID().equals(BInfo.getContentUUID());
    }

    /*
     * 播放节目集
     * programSeriesInfo 节目集信息
     * isNeedStartActivity 是否需要启动新的activity（播放器内选集，切换清晰度时为false）
     * index 播放第几集
     * position 从什么位置开始播放
     * */
    public void playProgramSeries(ProgramSeriesInfo programSeriesInfo, boolean
            isNeedStartActivity, int index, int position) {
        unshowLoadBack = false;
        LogUtils.i(TAG, "playVideo: index=" + index + " position=" + position);
        updatePlayStatus(2, index, position);
        if (isFullScreen() && !equalsInfo(mProgramSeriesInfo, programSeriesInfo)) {
//            Toast.makeText(getContext().getApplicationContext(), "剧集发生改变了", Toast.LENGTH_SHORT)
//                    .show();
            ProgramIsChange = true;
        }
        mProgramSeriesInfo = programSeriesInfo;

        mProgramSelectorType = getContext().getResources().getString(R.string.tv_series).equals
                (programSeriesInfo.getVideoType()) ? PROGRAM_SELECTOR_TYPE_NUMBER :
                PROGRAM_SELECTOR_TYPE_NAME;

        List<ProgramSeriesInfo.ProgramsInfo> programsInfos = programSeriesInfo.getData();
        if (programsInfos != null && programsInfos.size() > index) {

            if (mNewTVLauncherPlayerSeekbar != null) {
                boolean hasMutipleProgram = programsInfos.size() > 1;
                if (0 < index && index < programsInfos.size()) {
                    mNewTVLauncherPlayerSeekbar.setProgramName(programsInfos.get(index).getTitle(),
                            hasMutipleProgram);
                }
            }

            if (mNewTVLauncherPlayerSeekbar != null) {
                mNewTVLauncherPlayerSeekbar.setProgramName(programsInfos.get(index).getTitle(),
                        false);
            }

            if (mLoading != null) {
                mLoading.setProgramName(programsInfos.get(index).getTitle());
            }

            playIndex(index);

            PlayCheckRequestBean playCheckRequestBean = null;
            if (programsInfos.get(index).isMenuGroupHistory()) {
                playCheckRequestBean = createPlayCheckRequest(programSeriesInfo.getData().get
                                (index).getContentUUID(),
                        programsInfos.get(index).getSeriesSubUUID());
            } else {
                playCheckRequestBean = createPlayCheckRequest(programSeriesInfo.getData().get
                                (index).getContentUUID(),
                        programSeriesInfo.getContentUUID());
            }
            startPlayPermissionsCheck(playCheckRequestBean);

            startLoading();
            isNeedStartActivity(isNeedStartActivity, programSeriesInfo, index);
        } else {
            LogUtils.i(TAG, "playVideo: programsInfos == null || programsInfos.size() <= index");
//            NewTVLauncherPlayerViewManager.getInstance().release();
            onError("-8", "播放信息为空");
        }

    }

    public void playLive(String liveUrl, ProgramSeriesInfo programSeriesInfo, boolean
            isNeedStartActivity, int index, int position) {
        playLive(liveUrl, "", programSeriesInfo, isNeedStartActivity, index, position);
    }

    /*
     * add by lxf
     * 播放直播流
     * programSeriesInfo 节目集信息
     * isNeedStartActivity 是否需要启动新的activity（播放器内选集，切换清晰度时为false）
     * index 播放第几集
     * position 从什么位置开始播放
     * */
    public void playLive(String liveUrl, String contentUUID, ProgramSeriesInfo programSeriesInfo,
                         boolean isNeedStartActivity, int index, int position) {
        unshowLoadBack = false;
        LogUtils.i(TAG, "playlive playVideo: index=" + index + " position=" + position);
        updatePlayStatus(3, index, position);
        mProgramSeriesInfo = programSeriesInfo;

        boolean isNewLive = false;
        if (mLiveInfo == null) {
            mLiveInfo = new LiveInfo();
            isNewLive = true;
        }
        mLiveInfo.setLiveUrl(liveUrl);

        PlayCheckRequestBean playCheckRequest = null;

        if (programSeriesInfo != null) {
            if (isNewLive) {
                if (TextUtils.isEmpty(programSeriesInfo.getPlayStartTime()) || TextUtils.isEmpty
                        (programSeriesInfo.getPlayEndTime())) {
                    if (programSeriesInfo.getData() != null && programSeriesInfo.getData().size()
                            > 0) {

                        ProgramSeriesInfo.ProgramsInfo info = programSeriesInfo.getData().get(0);
                        mLiveInfo.setPlayTimeInfo(CmsLiveUtil.formatToSeconds(info
                                        .getPlayStartTime()),
                                CmsLiveUtil.formatToSeconds(info.getPlayEndTime()));
                    }
                } else {
                    mLiveInfo.setPlayTimeInfo(CmsLiveUtil.formatToSeconds(programSeriesInfo
                                    .getPlayStartTime()),
                            CmsLiveUtil.formatToSeconds(programSeriesInfo.getPlayEndTime()));
                }


                LogUtils.i(TAG, "startTime: " + programSeriesInfo.getPlayStartTime() + "," +
                        "endTime:" +
                        programSeriesInfo.getPlayEndTime());
            }
            mLiveInfo.setIsTimeShift(programSeriesInfo.getIsTimeShift());

            mProgramSelectorType = getContext().getResources().getString(R.string.tv_series).equals
                    (programSeriesInfo.getVideoType()) ? PROGRAM_SELECTOR_TYPE_NUMBER :
                    PROGRAM_SELECTOR_TYPE_NAME;

            if (mNewTVLauncherPlayerSeekbar != null) {
                mNewTVLauncherPlayerSeekbar.setLiveInfo(mLiveInfo);
                mNewTVLauncherPlayerSeekbar.setProgramName(programSeriesInfo.getTitle(),
                        false);
            }

            if (mLoading != null) {
                mLoading.setProgramName(programSeriesInfo.getTitle());
            }
            playCheckRequest = createPlayCheckRequest(programSeriesInfo, index);
        } else {
            playCheckRequest = LivePermissionCheckUtil.createPlayCheckRequest(contentUUID, liveUrl);
        }
        mLiveInfo.setContentUUID(playCheckRequest.getId());

        startPlayPermissionsCheck(playCheckRequest);
//        timer(programSeriesInfo);
        startLoading();
        isNeedStartActivity(isNeedStartActivity, programSeriesInfo, index);
    }

    /**
     * 创建直播鉴权数据
     */
    private PlayCheckRequestBean createPlayCheckRequest(ProgramSeriesInfo programSeriesInfo, int
            index) {
        PlayCheckRequestBean playCheckRequestBean = new PlayCheckRequestBean();
        playCheckRequestBean.setAppKey(mAppKey);
        playCheckRequestBean.setChannelId(mChannelId);
        playCheckRequestBean.setSource("NEWTV");
        if (programSeriesInfo.getData() != null && programSeriesInfo.getData().size() > index) {
            playCheckRequestBean.setId(programSeriesInfo.getData().get(index).getContentUUID());
        } else {
            playCheckRequestBean.setId(programSeriesInfo.getContentUUID());
        }
        playCheckRequestBean.setPid(mLiveInfo.getDefaultLiveUrl());

        return playCheckRequestBean;
    }

    /*
     * 播放节目
     * programDetailInfo 节目信息
     * position 从什么位置开始播放
     * */
    public void playProgramSingle(ProgramSeriesInfo programDetailInfo, int position, boolean
            openActivity) {
        unshowLoadBack = false;
        LogUtils.i(TAG, "playProgram: ");
        if (programDetailInfo == null) {
            return;
        }
        updatePlayStatus(1, 0, position);
        mProgramSeriesInfo = programDetailInfo;
        mProgramSelectorType = PROGRAM_SELECTOR_TYPE_NONE;
        if (mNewTVLauncherPlayerSeekbar != null) {
            mNewTVLauncherPlayerSeekbar.setProgramName(programDetailInfo.getTitle(), false);
        }

        if (mLoading != null) {
            mLoading.setProgramName(programDetailInfo.getTitle());
        }

        PlayCheckRequestBean playCheckRequestBean = createPlayCheckRequest(programDetailInfo
                .getContentUUID(), programDetailInfo.getProgramSeriesUUIDs());
        startPlayPermissionsCheck(playCheckRequestBean);
        startLoading();

        isNeedStartActivity(openActivity, programDetailInfo, 0);
    }

    /**
     * 创建点播鉴权数据
     */
    private PlayCheckRequestBean createPlayCheckRequest(String contentUUID, String
            programSeriesUUID) {
        PlayCheckRequestBean playCheckRequestBean = new PlayCheckRequestBean();

        playCheckRequestBean.setAppKey(mAppKey);
        playCheckRequestBean.setChannelId(mChannelId);
        playCheckRequestBean.setSource("NEWTV");
        playCheckRequestBean.setId(contentUUID);
        if (!TextUtils.isEmpty(programSeriesUUID)) {
            playCheckRequestBean.setAlbumId(programSeriesUUID);
        }

        PlayCheckRequestBean.Product productInfo = new PlayCheckRequestBean.Product();
        productInfo.setId(1);

        List<PlayCheckRequestBean.Product> productList = new ArrayList<>();
        productList.add(productInfo);
        playCheckRequestBean.setProductDTOList(productList);

        return playCheckRequestBean;
    }

    /**
     * 开始播放时进行状态和行为变更
     * type 1为单节目 2为节目集 3为直播
     */
    private void updatePlayStatus(int type, int index, int position) {
        setHintTextVisible(GONE);
        mIsPrepared = false;
        dismissChildView();

        switch (type) {
            case 1:
                mPlaySeriesOrSingle = PLAY_SINGLE;
                isLiving = false;
                break;
            case 2:
                mPlaySeriesOrSingle = PLAY_SERIES;
                isLiving = false;
                break;
            case 3:
                mPlaySeriesOrSingle = PLAY_SERIES;
                isLiving = true;
                break;
        }

        if (!isLiving) {
            addHistory();
            PlayerConfig.getInstance().setJumpAD(NeedJumpAd);
            NeedJumpAd = false;
            if (enterFullScreen) {
                createMenuGroup();
            }
        }

        mPlayingIndex = index;
        mHistoryPostion = position;
    }

    private void isNeedStartActivity(boolean isNeedStartActivity, ProgramSeriesInfo
            programDetailInfo, int index) {
        if (isNeedStartActivity) {
            Intent intent = new Intent(getContext(), NewTVLauncherPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("programSeriesInfo", programDetailInfo);
            bundle.putInt("index", index);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    private boolean sendSharpnessesToSetting() {
        LogUtils.i(TAG, "sendSharpnessesToSetting: ");
        List<MediaCDNInfo> mediaCDNInfos = mProgramDetailInfo.getData();
        List<MediaCDNInfo> specifiedCDNInfos = new ArrayList<MediaCDNInfo>();
        for (int i = 0; i < mediaCDNInfos.size(); i++) {
            MediaCDNInfo mediaCDNInfo = mediaCDNInfos.get(i);
            if (mediaCDNInfo.getCDNId() == mediaCDNInfos.get(0).getCDNId()) {
                specifiedCDNInfos.add(mediaCDNInfo);
                //测试专用
//                MediaCDNInfo test = new MediaCDNInfo(mediaCDNInfo.getCDNId(),"SD",mediaCDNInfo
// .getPlayURL());
//                specifiedCDNInfos.add(test);
                LogUtils.i(TAG, "sendSharpnessesToSetting: " + mediaCDNInfo.toString());
            }
        }

        if (specifiedCDNInfos.size() < 1) {
            LogUtils.i(TAG, "onResponse: specifiedCDNInfos == null");
            Toast.makeText(getContext(), getContext().getResources().getString(R.string
                    .program_info_no_data), Toast.LENGTH_SHORT).show();
            LogUtils.e("鉴权接口后没有返回视频地址");
            onError("-5", "视频地址为空");
            return true;
        }
        for (int j = 0; j < specifiedCDNInfos.size(); j++) {
            MediaCDNInfo mediaCDNInfo = specifiedCDNInfos.get(j);
            if (Constants.SHARPNESS_HD.equals(mediaCDNInfo.getMediaType())) {
                mPlayUrl = mediaCDNInfo.getPlayURL();
                LogUtils.i(TAG, "sendSharpnessesToSetting: mPlayUrl=" + mPlayUrl);
                if (TextUtils.isEmpty(mPlayUrl)) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string
                            .program_info_no_data), Toast.LENGTH_SHORT).show();
                    LogUtils.e("鉴权接口后没有返回视频地址");
                    onError("-5", "视频地址为空");
                    return true;
                }
                break;
            } else if ("ts".equals(mediaCDNInfo.getMediaType()) || "TS".equals(mediaCDNInfo
                    .getMediaType())) {
                mPlayUrl = mediaCDNInfo.getPlayURL();
                break;
            } else if ("M3U8".equals(mediaCDNInfo.getMediaType().toUpperCase())) {
                mPlayUrl = mediaCDNInfo.getPlayURL();
            }
        }
        return false;
    }

    private void startPlayPermissionsCheck(PlayCheckRequestBean playCheckRequestBean) {
        LogUtils.i(TAG, "startPlayPermissionsCheck: ");
        try {
            Gson gson = new Gson();
            String requestJson = gson.toJson(playCheckRequestBean);
            LogUtils.i(TAG, "startPlayPermissionsCheck: requestJson=" + requestJson);
//            String requestJson = "{\"vipFlag\":1,\"whetherTencentProduct\":false,
// \"appKey\":\"newtv\",\"channelId\":\"2\",\"contentUuid\":\"chenfei\",\"user\":{\"userId\":1,
// \"userToken\":\"1111111111\",\"logon\":true},\"tencentOrderDTO\":{\"cid\":1,\"vid\":1,
// \"pid\":1}}";
            PlayerNetworkRequestUtils.getInstance().playPermissionCheck(requestJson,
                    mPlayPermissionCheckCallback);

        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    private void playAlive(LiveInfo liveInfo) {
        VideoDataStruct videoDataStruct = new VideoDataStruct();
        videoDataStruct.setPlayType(Constants.PLAYTYPE_LIVE);
        if (liveInfo.isTimeShift()) {
            videoDataStruct.setPlayUrl(liveInfo.getmLiveUrl());
        } else {
            videoDataStruct.setPlayUrl(liveInfo.getDefaultLiveUrl());
        }

        videoDataStruct.setDataSource(Constants.DATASOURCE_ICNTV);
        videoDataStruct.setDeviceID(Constant.UUID);
        videoDataStruct.setKey(liveInfo.getKey());
        videoDataStruct.setContentUUID(liveInfo.getContentUUID());
        mNewTVLauncherPlayer.playAlive(getContext(), mPlayerFrameLayout, liveInfo,
                mLiveCallBackEvent,
                videoDataStruct);
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
                    showSeekBar(mIsPause);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                LogUtils.i(TAG, "onKeyDown: KEYCODE_DPAD_RIGHT");
                if (!mIsPrepared) {
                    return true;
                }
                if (mShowingChildView == SHOWING_NO_VIEW) {
                    showSeekBar(mIsPause);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!mIsPrepared) {
                    LogUtils.i(TAG, "onKeyDown: mIsPrepared is false");
                    return true;
                }
                if (mPlaySeriesOrSingle == PLAY_SERIES) {
                    if (mProgramSeriesInfo == null || mProgramSeriesInfo.getData() == null ||
                            mProgramSeriesInfo.getData().size() <= 1) {
                        LogUtils.i(TAG, "onKeyDown: mProgramSeriesInfo.getData()==null");
                        return true;
                    }
                } else if (mPlaySeriesOrSingle == PLAY_SINGLE) {
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

    private void showSeekBar(boolean isPause) {
        if (isLiving() && mLiveInfo != null && !mLiveInfo.isTimeShift()) {
            return;
        }

        if (mShowingChildView != SHOWING_NO_VIEW) {
            return;
        }
        if (mNewTVLauncherPlayerSeekbar != null) {
            if (isPause) {
                mNewTVLauncherPlayerSeekbar.showPauseIcon();
            } else {
                mNewTVLauncherPlayerSeekbar.show();
            }
        }
    }

    private void dismissChildView() {
        if (mShowingChildView == SHOWING_NO_VIEW) return;
        LogUtils.i(TAG, "dismissChildView: " + mShowingChildView);
        int current = mShowingChildView;
        mShowingChildView = SHOWING_NO_VIEW;
        switch (current) {
            case SHOWING_SEEKBAR_VIEW:
                if (mNewTVLauncherPlayerSeekbar != null) {
                    mNewTVLauncherPlayerSeekbar.dismiss();
                }
                break;
            case SHOWING_PROGRAM_TREE:
                if (menuGroupPresenter != null && menuGroupPresenter.isShow() &&
                        menuGroupPresenter instanceof MenuGroupPresenter) {
                    menuGroupPresenter.gone();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        /**
         * 适配讯码盒子
         * 正常盒子按返回键返回KeyEvent.KEYCODE_BACK
         * 讯码盒子非长按返回KeyEvent.KEYCODE_ESCAPE  长按返回KeyEvent.KEYCODE_ESCAPE KeyEvent.KEYCODE_BACK
         */
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA)) {
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
                if (mShowingChildView == SHOWING_SEEKBAR_VIEW
                        || mShowingChildView == SHOWING_PROGRAM_TREE) {
                    dismissChildView();
                } else {
                    onBackPressed();
                }
            }
            return true;
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

        if (menuGroupPresenter != null && menuGroupPresenter.dispatchKeyEvent(event)) {
            return true;
        }

        if (mShowingChildView == SHOWING_SEEKBAR_VIEW &&
                (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            mNewTVLauncherPlayerSeekbar.dispatchKeyEvent(event);
            return true;
        }

        if (isFullScreen()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (onBackPressed()) {
                        return true;
                    }
                }
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

                    if (isLiving() && mLiveInfo != null && !mLiveInfo.isTimeShift()) {
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
            reportPlayerHistory();
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
        return false;
    }

    private void reportPlayerHistory() {
        LogUtils.i(TAG, "reportPlayerHistory: ");
        HistoryBean historyBean = new HistoryBean();
        if (mPlaySeriesOrSingle == PLAY_SINGLE) {
            if (mProgramDetailInfo == null) {
                LogUtils.i(TAG, "reportPlayerHistory: mProgramDetailInfo==null");
                return;
            }
            LogUtils.i(TAG, "reportPlayerHistory: PLAY_SINGLE");
            historyBean.setContentUUId(mProgramDetailInfo.getContentUUID());
            historyBean.setContentType(mProgramDetailInfo.getContentType());
            historyBean.setName(mProgramDetailInfo.getTitle());
            historyBean.setPoster(mProgramDetailInfo.getvImage());
            historyBean.setCurrentTime(mNewTVLauncherPlayer.getCurrentPosition() + "");

        } else if (mPlaySeriesOrSingle == PLAY_SERIES) {
            if (mProgramSeriesInfo == null) {
                LogUtils.i(TAG, "reportPlayerHistory: mProgramSeriesInfo==null");
                return;
            }
            if (mProgramSeriesInfo.getData() == null || mProgramSeriesInfo.getData().size() <=
                    mPlayingIndex) {
                LogUtils.i(TAG, "reportPlayerHistory: mProgramSeriesInfo.getData()==null || " +
                        "mProgramSeriesInfo.getData().size()<=mPlayingIndex");
                return;
            }
            ProgramSeriesInfo.ProgramsInfo programsInfo = mProgramSeriesInfo.getData().get
                    (mPlayingIndex);
            if (programsInfo == null) {
                LogUtils.i(TAG, "reportPlayerHistory: programsInfo==null");
                return;
            }
            LogUtils.i(TAG, "reportPlayerHistory: PLAY_SERIES");
            historyBean.setContentUUId(mProgramSeriesInfo.getContentUUID());
            historyBean.setContentType(mProgramSeriesInfo.getContentType());
            historyBean.setName(mProgramSeriesInfo.getTitle());
            historyBean.setPoster(mProgramSeriesInfo.getvImage());
            historyBean.setCurrentTime(mNewTVLauncherPlayer.getCurrentPosition() + "");
            historyBean.setCurrentNum(mPlayingIndex + "");
            historyBean.setTotalNum(mProgramSeriesInfo.getSeriesSum());
        }
        LogUtils.i(TAG, "reportPlayerHistory: historyBean=" + historyBean.toString());
    }

    public boolean isLiving() {
        return isLiving;
    }

    // add by lxf
    private void playVodNext() {
        if (mPlaySeriesOrSingle == PLAY_SINGLE) {
            addHistory();
            Toast.makeText(getContext(), getContext().getResources().getString(R.string
                            .play_complete),
                    Toast.LENGTH_SHORT).show();
            reportPlayerHistory();
            AllComplete(false, "播放结束");

            if (startIsFullScreen) {
                NewTVLauncherPlayerViewManager.getInstance().release();
            }
        } else if (mPlaySeriesOrSingle == PLAY_SERIES) {
            int next = mPlayingIndex + 1;
            if (mProgramSeriesInfo.getData().size() > next) {
                playProgramSeries(mProgramSeriesInfo, false, next, 0);
                if (listener != null && listener.size() > 0) {
                    for (IPlayProgramsCallBackEvent l : listener) {
                        l.onNext(mProgramSeriesInfo.getData().get(next), next, true);
                    }
                }

            } else {
                addHistory();
                if (listener != null && listener.size() > 0) {
                    for (IPlayProgramsCallBackEvent l : listener) {
                        l.onNext(null, next, false);
                    }
                }
                Toast.makeText(getContext(), getContext().getResources().getString(R.string
                        .play_complete), Toast.LENGTH_SHORT).show();
                reportPlayerHistory();
                if (startIsFullScreen) {
                    NewTVLauncherPlayerViewManager.getInstance().release();
                }
                AllComplete(false, "播放结束");
            }
        }
    }

    protected void playIndex(int index) {
    }

    protected void AllComplete(boolean isError, String info) {

        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.release();
            mNewTVLauncherPlayer = null;
        }

    }

    public int getShowingView() {
        return mShowingChildView;
    }

    public void setShowingView(int showingView) {
        if (mShowingChildView == showingView) return;
        LogUtils.i(TAG, "setShowingView: showingView=" + showingView);
        if (mShowingChildView != SHOWING_NO_VIEW) {
            dismissChildView();
        }
        mShowingChildView = showingView;
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
        if (mNewTVLauncherPlayer != null)
            mNewTVLauncherPlayer.start();

        mIsPause = false;
        hidePauseImage();
        if (mIsPause && mNewTVLauncherPlayerSeekbar != null &&
                mNewTVLauncherPlayerSeekbar.getVisibility() == VISIBLE) {
            mNewTVLauncherPlayerSeekbar.hidePauseIcon();
        }
    }

    public void pause() {
        if (mNewTVLauncherPlayer != null)
            mNewTVLauncherPlayer.pause();

        mIsPause = true;
        showPauseImage();
        showSeekBar(true);
    }

    public void stop() {
        if (mNewTVLauncherPlayer != null) {
            mNewTVLauncherPlayer.stop();
        }
    }

    public int getIndex() {
        return mPlayingIndex;
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
     * 直播有结束时间，并且没有时移功能，就启动计时在结束时关闭直播
     */
    private void timer(ProgramSeriesInfo mProgramSeriesInfo) {
        if (mProgramSeriesInfo != null && !TextUtils.isEmpty(mProgramSeriesInfo.getPlayEndTime())
                && !"1".equals(mProgramSeriesInfo.getIsTimeShift()))
            LiveTimingUtil.endTime(mProgramSeriesInfo.getPlayEndTime(), new LiveTimingUtil
                    .LiveEndListener() {
                @Override
                public void end() {
                    if (isLiving) {
                        mLiveCallBackEvent.onCompletion();
                    }
                }
            });
    }

    /**
     * 保存播放记录  在播放单节目和节目集的时候调用
     */
    private void addHistory() {
        if (mProgramSeriesInfo == null) {
            return;
        }
        RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(getIndex(),
                getCurrentPosition(), mProgramSeriesInfo.getContentUUID()));

        Log.i(TAG, "addHistory: " + getIndex() + ",position:" + getCurrentPosition());
        DBUtil.addHistory(mProgramSeriesInfo, getIndex(), getCurrentPosition(), new
                DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            if (mProgramSeriesInfo != null) {
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," +
                                        mProgramSeriesInfo.getContentUUID());//添加历史记录
                            }
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    }
                });
    }

    public boolean isADPlaying() {
        return mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isADPlaying();
    }

    public boolean isPlaying() {
        return mNewTVLauncherPlayer != null && mNewTVLauncherPlayer.isPlaying();
    }

    public void setHintTextVisible(int visible) {
    }

    public static class PlayerViewConfig {
        public boolean prepared = false;
        public ViewGroup.LayoutParams layoutParams;     //布局属性
        public boolean isFullScreen;            //当前是否为全屏状态
        public ViewParent parentViewGroup;      //父级容器
        public boolean startIsFullScreen;       //开始时候是不是全屏状态
        public View defaultFocusView;           //进入全屏时候的默认焦点位置
        public PlayerCallback playerCallback;
        public int playPosition;
        public VPlayCenter playCenter;
    }
}
