package tv.newtv.cboxtv.views.detail;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.LiveParam;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LiveTimingUtil;
import com.newtv.libs.util.LogUploadUtils;

import tv.newtv.cboxtv.player.util.PlayInfoUtil;
import com.newtv.libs.util.RxBus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reactivex.disposables.Disposable;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.DescriptionActivity;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.views.custom.FocusToggleSelect;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         11:23
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class HeadPlayerView extends RelativeLayout implements IEpisode, View.OnClickListener,
        ContentContract.View {

    private static final String TAG = "HeadPlayerView";

    private ContentContract.Presenter mPresenter;

    Content mInfo;
    private VideoPlayerView playerView;
    private int currentPlayIndex = 0;
    private int currentPosition = 0;
    private NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;
//    private boolean isFullScreen = false;

    private Content currentProgramSeriesInfo;
    private Builder mBuilder;
    private View contentView;

    private boolean isBuildComplete = false;

    private Boolean isPlayLive = false;
    private PlayInfo mPlayInfo;

    private Disposable mDisposable;

    private long lastClickTime = 0;
    private PlayerCallback mPlayerCallback = new PlayerCallback() {
        @Override
        public void onEpisodeChange(int index, int position) {
            setCurrentPlayIndex("onEpisodeChange", index);
            currentPosition = position;

            if (mBuilder != null && mBuilder.playerCallback != null) {
                mBuilder.playerCallback.onEpisodeChange(index, position);
            }
        }

        @Override
        public void onPlayerClick(VideoPlayerView videoPlayerView) {
            if (mBuilder != null && mBuilder.playerCallback != null) {
                mBuilder.playerCallback.onPlayerClick(videoPlayerView);
            }
        }

        @Override
        public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
            if (mBuilder != null && mBuilder.playerCallback != null) {
                mBuilder.playerCallback.AllPlayComplete(isError, info, videoPlayerView);
            }
        }

        @Override
        public void ProgramChange() {
            if (mBuilder != null && mBuilder.playerCallback != null) {
                mBuilder.playerCallback.ProgramChange();
            }
        }
    };


    public HeadPlayerView(Context context) {
        this(context, null);
    }

    public HeadPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public HeadPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void setCurrentPlayIndex(String tag, int index) {
        Log.e(TAG, "setCurrentPlayIndex tag=" + tag + " index = " + index);
        currentPlayIndex = index;
    }

    /**
     * 准备播放器
     */
    public void prepareMediaPlayer() {

        if (playerView != null && playerView.isReleased()) {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            playerView = null;
        }

        if (playerView == null && mBuilder != null && contentView != null) {

            View video = contentView.findViewById(mBuilder.mPlayerId);
            if (video == null) return;
            if (video instanceof VideoPlayerView) {
                playerView = (VideoPlayerView) video;
            } else if (video instanceof FrameLayout) {
                if (defaultConfig == null) {
                    playerView = new VideoPlayerView(getContext());
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                            .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    playerView.setLayoutParams(layoutParams);
                    ((ViewGroup) video).addView(playerView, layoutParams);
                } else {
                    playerView = new VideoPlayerView(defaultConfig, getContext());
                    if (defaultConfig.defaultFocusView instanceof VideoPlayerView) {
                        playerView.requestFocus();
                    }
                }
            }

            if (playerView != null) {
                playerView.setPlayerCallback(mPlayerCallback);
                isBuildComplete = true;
            }
        }
    }

    public Content getInfo() {
        return mInfo;
    }

    private void initData() {
        DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, mBuilder.contentUUid)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Gson mGson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                            }.getType();
                            List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                            if (data.size() > 0) {
                                UserCenterPageBean.Bean value = data.get(0);
                                if (value != null) {
                                    if (!TextUtils.isEmpty(value.playPosition)) {
                                        currentPosition = Integer.valueOf(value.playPosition);
                                    } else {
                                        currentPosition = 0;
                                    }
                                    if (data.get(0).playIndex != null) {
                                        setCurrentPlayIndex("DataSupport", Integer.valueOf(data
                                                .get(0).playIndex));
                                    } else {
                                        setCurrentPlayIndex("DataSupport", 0);
                                    }
                                }


                            }

                        }

                    }
                }).excute();


    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
    }

    public <T extends View> T findViewUseId(int id) {
        if (contentView != null) {
            return contentView.findViewById(id);
        }
        return super.findViewById(id);
    }

    public <T extends View> T findViewUseTag(Object tag) {
        if (contentView != null) {
            return contentView.findViewWithTag(tag);
        }
        return super.findViewWithTag(tag);
    }

    public void onActivityPause() {
        if (playerView != null) {
            currentPosition = playerView.getCurrentPosition();
            defaultConfig = playerView.getDefaultConfig();

            addHistory();
        }
    }

    public void onActivityStop() {
        if (playerView != null) {
            currentPosition = playerView.getCurrentPosition();
            defaultConfig = playerView.getDefaultConfig();

            addHistory();
            playerView.stopPlay();
            playerView.release();
            playerView.destory();
            playerView = null;
        }
        LiveTimingUtil.clearListener();
    }

    private void addHistory() {
//        int position = playerView.getCurrentPosition();
//        if (position > 0 && !isPlayLive) {
//            if (currentProgramSeriesInfo != null) {
//                DBUtil.addHistory(currentProgramSeriesInfo
//                        , currentPlayIndex, position, new DBCallback<String>() {
//                            @Override
//                            public void onResult(int code, String result) {
//                                if (code == 0) {
//                                    LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," +
//                                            currentProgramSeriesInfo.getContentUUID());//添加历史记录
//                                    RxBus.get().post(Constant.UPDATE_UC_DATA, true);
//                                }
//                            }
//                        });
//            }
//        }
    }

    public void onActivityResume() {
        if (playerView != null && !playerView.isReleased() && playerView.isReady() && (playerView
                .isADPlaying() || playerView.isPlaying())) {
            Log.e(TAG, "player view is working....");
            return;
        }
        if (playerView != null && playerView.isReleased()) {
            Log.e(TAG, "player view is released, rebuild it....");
            playerView.destory();
            playerView = null;
            prepareMediaPlayer();
        }

        if (isPlayLive && mInfo != null) {
//            Log.e(TAG, "player view is builded, play live video....");
//            playerView.playLiveVideo(mInfo.getContentUUID(), mInfo.getPlayUrl(), mInfo
//                    .getTitle(), 0, 0);
//            timer();
            return;
        }
        if (playerView != null && currentProgramSeriesInfo != null) {
            currentPosition = (defaultConfig != null ? defaultConfig.playPosition :
                    currentPosition);
            Log.e(TAG, "player view is builded, play vod video....index=" + currentPlayIndex + " " +
                    "pos=" + currentPosition);
            playerView.setSeriesInfo(currentProgramSeriesInfo);
            if (currentPlayIndex >= 0 && currentPosition >= 0) {
                playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
            } else {
                playerView.playSingleOrSeries(0, 0);
            }
        }
    }

    public void EnterFullScreen(Activity activity) {
        if (isPlayLive && playerView != null) {
            playerView.enterFullScreen(activity, isPlayLive);
        } else if (playerView != null) {
            playerView.EnterFullScreen(activity, false);
        }
    }

    public void Build(Builder builder) {
        mBuilder = builder;
        initData();
        if (mBuilder.playerCallback == null) return;
        if (mBuilder.contentUUid == null) return;
        if (mBuilder.mPlayerId == -1) return;
        if (mBuilder.mLayout == -1) return;
        if (mBuilder.clickables != null && mBuilder.clickListener == null) return;
        if (mBuilder.focusables != null && mBuilder.focusChangeListener == null) return;

        contentView = LayoutInflater.from(getContext()).inflate(mBuilder.mLayout, this, false);
        addView(contentView);
        checkDataFromDB();

        prepareMediaPlayer();

        if (mBuilder.focusables != null && mBuilder.focusChangeListener != null) {
            for (int id : mBuilder.focusables) {
                View target = contentView.findViewById(id);
                if (target != null) {
                    target.setOnFocusChangeListener(mBuilder.focusChangeListener);
                }
            }
        }

        if (mBuilder.clickables != null && mBuilder.clickListener != null) {
            for (int id : mBuilder.clickables) {
                View target = contentView.findViewById(id);
                if (target != null) {
                    target.setOnClickListener(this);
                }
            }
        }

        if (mBuilder.defaultFocusID != 0) {
            View focus = contentView.findViewById(mBuilder.defaultFocusID);
            if (focus != null) {
                focus.requestFocus();
            }
        }

        mPresenter = new ContentContract.ContentPresenter(getContext(), this);

        mPresenter.getContent(mBuilder.contentUUid);
    }

    private void checkDataFromDB() {
        if (mBuilder != null && mBuilder.dbTypes != null && mBuilder.dbTypes.size() > 0) {
            for (CustomFrame value : mBuilder.dbTypes) {
                switch (value.dbType) {
                    case Builder.DB_TYPE_COLLECT:
                        //TODO
                        final View collect = contentView.findViewById(value.viewId);
                        if (collect != null) {
                            DBUtil.CheckCollect(mBuilder.contentUUid, new DBCallback<String>() {
                                @Override
                                public void onResult(int code, String result) {
                                    if (collect instanceof FocusToggleSelect) {
                                        ((FocusToggleSelect) collect).setSelect(code == 0 &&
                                                !TextUtils.isEmpty(result));
                                    }
                                }
                            });
                            collect.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (collect instanceof FocusToggleSelect) {
                                        if (System.currentTimeMillis() - lastClickTime >= 2000)
                                        {//判断距离上次点击小于2秒
                                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                            if (((FocusToggleSelect) collect).isSelect()) {
                                                DBUtil.UnCollect(mBuilder.contentUUid, new
                                                        DBCallback<String>() {
                                                            @Override
                                                            public void onResult(int code, String
                                                                    result) {
                                                                ((FocusToggleSelect) collect)
                                                                        .setSelect
                                                                                (code == 0 &&
                                                                                        !TextUtils
                                                                                                .isEmpty(result));
                                                                RxBus.get().post(Constant
                                                                        .UPDATE_UC_DATA, true);
                                                                if (code == 0) {
                                                                    LogUploadUtils.uploadLog
                                                                            (Constant
                                                                                    .LOG_NODE_COLLECT, "1," + mBuilder.contentUUid);//取消收藏
                                                                    Toast.makeText(getContext()
                                                                                    .getApplicationContext(),
                                                                            "取消收藏成功",
                                                                            Toast
                                                                                    .LENGTH_SHORT)
                                                                            .show();
                                                                }

                                                            }
                                                        });
                                            } else {
                                                DBUtil.PutCollect(mInfo, new DBCallback<String>() {
                                                    @Override
                                                    public void onResult(int code, String result) {
                                                        ((FocusToggleSelect) collect).setSelect
                                                                (code ==
                                                                        0 && !TextUtils
                                                                        .isEmpty
                                                                                (result));
                                                        RxBus.get().post(Constant
                                                                        .UPDATE_UC_DATA,

                                                                true);
                                                        if (code == 0) {
                                                            LogUploadUtils.uploadLog(Constant
                                                                    .LOG_NODE_COLLECT, "0," +
                                                                    mInfo.getContentUUID());
                                                            Toast.makeText(getContext()
                                                                            .getApplicationContext(),
                                                                    R.string.collect_success,
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }

                                                    }
                                                });
                                            }
                                        }

                                    }
                                }
                            });
                        }
                        break;
                    case Builder.DB_TYPE_SUBSCRIP:
                        View view = contentView.findViewById
                                (value.viewId);
                        final View Subscrip = contentView.findViewById
                                (value.viewId);
                        if (Subscrip != null) {
                            DBUtil.CheckSubscrip(mBuilder.contentUUid, new DBCallback<String>() {
                                @Override
                                public void onResult(int code, String result) {
                                    if (Subscrip instanceof FocusToggleSelect) {
                                        ((FocusToggleSelect) Subscrip).setSelect(code == 0 &&
                                                !TextUtils.isEmpty
                                                        (result));
                                    }
                                }
                            });
                            Subscrip.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Subscrip instanceof FocusToggleSelect) {
                                        if (System.currentTimeMillis() - lastClickTime >= 2000)
                                        {//判断距离上次点击小于2秒
                                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                            if (((FocusToggleSelect) Subscrip).isSelect()) {
                                                DBUtil.UnSubcribe(mBuilder.contentUUid, new
                                                        DBCallback<String>() {
                                                            @Override
                                                            public void onResult(int code, String
                                                                    result) {

                                                                if (code == 0) {
                                                                    ((FocusToggleSelect) Subscrip)
                                                                            .setSelect(false);
                                                                    Toast.makeText(getContext()
                                                                                    .getApplicationContext(), "取消订阅成功",
                                                                            Toast
                                                                                    .LENGTH_SHORT)
                                                                            .show();
                                                                    RxBus.get().post(Constant
                                                                            .UPDATE_UC_DATA, true);
                                                                }
                                                            }
                                                        });
                                            } else {
                                                if (mInfo != null) {
                                                    DBUtil.AddSubcribe(mInfo, new
                                                            DBCallback<String>() {
                                                                @Override
                                                                public void onResult(int code,
                                                                                     String
                                                                                             result) {
                                                                    if (code == 0) {
                                                                        ((FocusToggleSelect)
                                                                                Subscrip)
                                                                                .setSelect(true);
                                                                        Toast.makeText(getContext()
                                                                                        .getApplicationContext(),
                                                                                R.string.subscribe_success,
                                                                                Toast
                                                                                        .LENGTH_SHORT).show();
                                                                        RxBus.get().post(Constant
                                                                                        .UPDATE_UC_DATA,

                                                                                true);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        break;
                }
            }
        }
    }

    private void onLoadError(String message) {
        Toast.makeText(LauncherApplication.AppContext, "HeadPlayerView:" + message, Toast
                .LENGTH_SHORT).show();
    }

    public void setProgramSeriesInfo(Content programSeriesInfo) {
        if (TextUtils.isEmpty(programSeriesInfo.getTitle())) {
            if (mInfo != null) {
                programSeriesInfo.setTitle(mInfo.getTitle());
            }

        }
        if (TextUtils.isEmpty(programSeriesInfo.getVImage())) {
            if (mInfo != null) {
                programSeriesInfo.setVImage(mInfo.getVImage());
            }
        }

        if (TextUtils.isEmpty(programSeriesInfo.getContentType())) {
            if (mInfo != null) {
                programSeriesInfo.setContentType(mInfo.getContentType());
            }
        }
        currentProgramSeriesInfo = programSeriesInfo;
        if (playerView != null && !isPlayLive && mInfo != null) {
            playerView.setSeriesInfo(programSeriesInfo);
            playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
        } else {
            mPlayInfo = new PlayInfo(currentPlayIndex, currentPosition);
        }
    }

    public void Play(int index, int postion, boolean requestFocus) {

        if (isPlayLive) {
            isPlayLive = false;
            if (playerView != null) {
                playerView.setHintTextVisible(View.GONE);
            }
        } else if (currentPlayIndex == index) {
            if (requestFocus) {
                requestPlayerFocus();
            }
            return;
        }
        setCurrentPlayIndex("Play", index);

        prepareMediaPlayer();

        if (playerView != null) {
            playerView.beginChange();

            playerView.setSeriesInfo(currentProgramSeriesInfo);
            playerView.playSingleOrSeries(index, postion);

            if (requestFocus) {
                requestPlayerFocus();
            }
        }
    }

    private void requestPlayerFocus() {
        if (playerView == null) {
            return;
        }
        playerView.requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayerCallback.onPlayerClick(playerView);
            }
        }, 500);
    }

    private void parseResult() {
        if (mInfo == null) return;

        TextView title = contentView.findViewById(R.id.id_detail_title);
        TextView type = contentView.findViewById(R.id.id_detail_type);
        TextView typeTWO = contentView.findViewById(R.id.id_detail_type2);
        TextView star = contentView.findViewById(R.id.id_detail_star);
        TextView content = contentView.findViewById(R.id.id_detail_content);
        ViewStub moreStub = contentView.findViewById(R.id.more_view_stub);
        if (title != null) {
            title.setText(mInfo.getTitle());
        }

        Log.e("HeadPlayerparseResult: ", mInfo.toString());
//              第一行是 地区、年代、一级分级  第二行是 主持人、导演、主演，所有人员名称就是 连续一行显示，用竖线前后空格区分。
        // mInfo.getDistrict() 地区  mInfo.getArea()//国家地区 mInfo.getPresenter

        if (type != null) {
            type.setText(PlayInfoUtil.formatSplitInfo(mInfo.getArea()
                    , mInfo.getAirtime()
                    , mInfo.getVideoType()));
        }

        if (star != null) {
            if (!TextUtils.isEmpty(mInfo.getDirector()) && !TextUtils.isEmpty(mInfo
                    .getActors()) && !mInfo.getDirector().equals("无") && !mInfo.getActors
                    ().equals("无")) {
                star.setVisibility(VISIBLE);
                star.setText(PlayInfoUtil.formatSplitInfo("导演:" + mInfo.getDirector(),
                        "主演:" + mInfo
                                .getActors()));
            } else if (!TextUtils.isEmpty(mInfo.getPresenter())) {
                star.setText(PlayInfoUtil.formatSplitInfo("主持人:" + mInfo.getPresenter()));
            } else {
                star.setVisibility(GONE);
            }

        }

        if (content != null) {
            content.setText(mInfo.getDescription().replace("\r\n", ""));
            int ellipsisCount = content.getLayout().getEllipsisCount(content.getLineCount
                    () - 1);
            if (ellipsisCount > 0 && moreStub != null) {
                final View view = moreStub.inflate();
                view.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            view.setBackgroundResource(R.drawable.more_hasfocus);
                        } else {
                            view.setBackgroundResource(R.drawable.more_nofocus);
                        }
                    }
                });

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DescriptionActivity.runAction(getContext(), mInfo.getTitle(),
                                mInfo.getDescription());
                    }
                });
            }
        }

        if (mInfo.getLiveLoopParam() != null) {
            LiveParam param = CmsUtil.isLiveTime(mInfo.getLiveLoopParam());
            if (param != null) {
                //需要直播
                playerView.playLiveVideo(mInfo.getContentUUID(), param.getLiveParam(), mInfo
                        .getTitle(), 0, 0);
                timer();
                isPlayLive = true;
            }
        } else {
            if (currentProgramSeriesInfo != null && mPlayInfo != null) {
                playerView.setSeriesInfo(currentProgramSeriesInfo);
                playerView.playSingleOrSeries(mPlayInfo.index, mPlayInfo.position);
            }
        }
    }

    @Override
    public String getContentUUID() {
        return mBuilder.contentUUid;
    }

    @Override
    public boolean interuptKeyEvent(KeyEvent event) {
        View focusView = findFocus();

        if (focusView != null && focusView instanceof VideoPlayerView) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_LEFT) {
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                playerView.dispatchKeyEvent(event);
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (!hasFocus()) {
                    playerView.requestFocus();
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_RIGHT);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_LEFT);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_UP);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }

        if (NewTVLauncherPlayerViewManager.getInstance().equalsPlayer(playerView)) {
            NewTVLauncherPlayerViewManager.getInstance().release();
        }
        defaultConfig = null;
        if (playerView != null) {
            playerView.release();
            playerView.destory();
            playerView = null;
        }
        contentView = null;
        if (mBuilder != null) {
            mBuilder.release();
            mBuilder = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (mBuilder.clickListener != null) {
            mBuilder.clickListener.onClick(view);
        }
    }

    private void timer() {
//        if (mInfo == null || TextUtils.isEmpty(mInfo.getPlayEndTime())) {
//            return;
//        }
//        LiveTimingUtil.endTime(mInfo.getPlayEndTime(), new LiveTimingUtil.LiveEndListener() {
//            @Override
//            public void end() {
//                if (playerView != null) {
//                    playerView.setHintText("播放已结束");
//                    playerView.setHintTextVisible(View.VISIBLE);
//                    playerView.release();
//                }
//            }
//        });
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

    }

    @Override
    public void onContentResult(@org.jetbrains.annotations.Nullable Content content) {
        mInfo = content;
        mBuilder.infoResult.onResult(content);
        parseResult();
    }

    @Override
    public void onSubContentResult(@Nullable ArrayList<SubContent> result) {

    }


    public interface InfoResult {
        void onResult(Content info);
    }

    private static class PlayInfo {

        int index;
        int position;

        private PlayInfo(int ind, int pos) {
            index = ind;
            position = pos;
        }
    }

    public static class CustomFrame {
        int viewId;
        int dbType;

        public CustomFrame(int view, int type) {
            viewId = view;
            dbType = type;
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {
        public static final int DB_TYPE_SUBSCRIP = 1;
        public static final int DB_TYPE_COLLECT = 2;

        @SuppressWarnings("UnusedAssignment")
        private int mLayout = -1;
        private int mPlayerId = -1;
        private int mPlayerFocusId = -1;
        private List<Integer> clickables;
        private List<Integer> focusables;
        private OnClickListener clickListener;
        private PlayerCallback playerCallback;
        private OnFocusChangeListener focusChangeListener;
        private InfoResult infoResult;
        private String contentUUid;
        private int ProgramType;
        private List<CustomFrame> dbTypes;
        private int defaultFocusID = 0;

        private Builder(int layout) {
            mLayout = layout;
        }

        public static Builder build(int id) {
            return new Builder(id);
        }

        public void release() {
            dbTypes = null;
            focusChangeListener = null;
            playerCallback = null;
            clickListener = null;
            focusables = null;
            clickables = null;
            infoResult = null;
        }

        public Builder CheckFromDB(CustomFrame... types) {
            dbTypes = Arrays.asList(types);
            return this;
        }

        public Builder SetDefaultFocusID(int id) {
            defaultFocusID = id;
            return this;
        }

        public Builder SetPlayerId(int id) {
            mPlayerId = id;
            return this;
        }

        public Builder SetClickListener(OnClickListener listener) {
            clickListener = listener;
            return this;
        }

        public Builder SetFocusListener(OnFocusChangeListener listener) {
            focusChangeListener = listener;
            return this;
        }

        public Builder SetPlayerFocusId(int id) {
            mPlayerFocusId = id;
            return this;
        }

        public Builder SetPlayerCallback(PlayerCallback callback) {
            playerCallback = callback;
            return this;
        }

        public Builder SetOnInfoResult(InfoResult result) {
            infoResult = result;
            return this;
        }

        public Builder SetContentUUID(String uuid) {
            contentUUid = uuid;
            return this;
        }

        public Builder setIntType(int type) {
            ProgramType = type;
            return this;
        }

        public Builder SetClickableIds(Integer... ids) {
            clickables = Arrays.asList(ids);
            return this;
        }

        public Builder SetFocusableIds(Integer... ids) {
            focusables = Arrays.asList(ids);
            return this;
        }

    }
}
