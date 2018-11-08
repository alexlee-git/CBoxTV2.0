package tv.newtv.cboxtv.views.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.DescriptionActivity;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.util.PlayInfoUtil;
import tv.newtv.cboxtv.player.videoview.ExitVideoFullCallBack;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.views.custom.FocusToggleSelect;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IHisoryStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyMemberStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.ISubscribeStatusCallback;
import tv.newtv.cboxtv.uc.v2.sub.QueryUserStatusUtil;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.custom.FocusToggleView2;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         11:23
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class HeadPlayerView extends RelativeLayout implements IEpisode, View.OnClickListener,
        ContentContract.View,LiveListener {

    private static final String TAG = "HeadPlayerView";
    Content mInfo;
    private ContentContract.Presenter mPresenter;
    private VideoPlayerView playerView;
    private int currentPlayIndex = 0;
    private int currentPosition = 0;
    private NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;

    private UserCenterPageBean.Bean historyBean;

    private Builder mBuilder;
    private View contentView;

    private boolean isBuildComplete = false;
    private Boolean isPlayLive = false;
    private PlayInfo mPlayInfo;
    private Disposable mDisposable;
    private String memberStatus;
    private String expireTime;
    private long lastClickTime = 0;
    private View vipPay;
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
                Log.d("ywy_log", "HeadPlayerView  exitFull listener contentUUID : " + mBuilder.contentUUid);
                DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.CONTENTUUID, mBuilder.contentUUid)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                Log.d("ywy_log", "code : " + code + "  result : " + result);
                                if (!TextUtils.isEmpty(result)) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                                    if (data.size() > 0) {
                                        Log.d("ywy_log", "HeadPlayerView iniData title: " + data.get(0)._title_name + " contentUUID : " + data.get(0)._contentuuid);
                                        UserCenterPageBean.Bean value = data.get(0);
                                        if (value != null) {
                                            Log.d("ywy_log", "playPosition : " + value.playPosition);
                                            if (!TextUtils.isEmpty(value.playPosition)) {
                                                currentPosition = Integer.valueOf(value.playPosition);
                                            } else {
                                                currentPosition = 0;
                                            }
                                            if (data.get(0).playIndex != null) {
                                                setCurrentPlayIndex("DataSupport", Integer.valueOf(data
                                                        .get(0)
                                                        .playIndex));
                                            } else {
                                                setCurrentPlayIndex("DataSupport", 0);
                                            }
                                            playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
                                        }
                                    }
                                }
                            }
                        }).excute();

            }
        }


    };


    //检测全屏退出回调
    private VideoExitFullScreenCallBack videoExitFullScreenCallBack = new VideoExitFullScreenCallBack() {
        @Override
        public void videoEitFullScreen() {
            if (mBuilder != null && mBuilder.videoExitFullScreenCallBack != null){
                mBuilder.videoExitFullScreenCallBack.videoEitFullScreen();
            }
        }
    };

    public HeadPlayerView(Context context) {
        this(context, null);
        getMemberStatus();
    }

    public HeadPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        getMemberStatus();
    }


    public HeadPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getMemberStatus();
    }

    private void setCurrentPlayIndex(String tag, int index) {
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
                //全屏显示
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
                playerView.setVideoExitCallback(videoExitFullScreenCallBack);
                isBuildComplete = true;
            }
        }
    }

    public Content getInfo() {
        return mInfo;
    }

    private void initData() {
        UserCenterUtils.getHistoryState(DBConfig.CONTENTUUID, mBuilder.contentUUid, "", new IHisoryStatusCallback() {
            @Override
            public void getHistoryStatus(UserCenterPageBean.Bean bean) {
                if (null != bean) {
                    if (!TextUtils.isEmpty(bean.playPosition)) {
                        currentPosition = Integer.valueOf(bean.playPosition);
                    } else {
                        currentPosition = 0;
                    }
                    if (bean.playIndex != null) {
                        setCurrentPlayIndex("DataSupport", Integer.valueOf(bean.playIndex));
                    } else {
                        setCurrentPlayIndex("DataSupport", 0);
                    }
                }
            }
        });
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
    }

    private void getMemberStatus() {
        UserCenterUtils.getMemberStatus(new INotifyMemberStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(String status, Bundle memberBundle) {
                memberStatus = status;
                if (memberBundle != null) {
                    expireTime = (String) memberBundle.get(QueryUserStatusUtil.expireTime);
                }
            }
        });
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


            playerView.release();
            playerView.destory();
            playerView = null;
        }
    }

    public void onActivityStop() {
        if (playerView != null) {
            currentPosition = playerView.getCurrentPosition();
            defaultConfig = playerView.getDefaultConfig();

            playerView.stopPlay();
            playerView.release();
            playerView.destory();
            playerView = null;
        }
    }
    private void addHistory() {
        int position = playerView.getCurrentPosition();
        if (!isPlayLive) {
            if (mInfo != null) {
                UserCenterUtils.addHistory(mInfo
                        , currentPlayIndex, position, playerView.getDuration(), new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0 && mInfo != null) {
                                    LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," +
                                            mInfo.getContentUUID());//添加历史记录
                                    RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                }
                            }
                        });
            }
        }
    }

    public void onActivityResume() {
        getMemberStatus();
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
            return;
        }
        if (playerView != null && mInfo != null) {
            currentPosition = (defaultConfig != null ? defaultConfig.playPosition :
                    currentPosition);
            Log.e(TAG, "player view is builded, play vod video....index=" + currentPlayIndex + " " +
                    "pos=" + currentPosition);
            playerView.setSeriesInfo(mInfo);
            playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
        }
    }

    //显示全屏
    public void EnterFullScreen(Activity activity) {
        playerView.enterFullScreen(activity);
    }

    public void Build(Builder builder) {
        mBuilder = builder;
        initData();
        if (mBuilder.playerCallback == null) return;
        //if (mBuilder.videoExitFullScreenCallBack == null) return;
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
        mPresenter.getContent(mBuilder.contentUUid, mBuilder.autoGetSub);
    }

    private void checkDataFromDB() {
        if (mBuilder != null && mBuilder.dbTypes != null && mBuilder.dbTypes.size() > 0) {
            for (CustomFrame value : mBuilder.dbTypes) {
                switch (value.dbType) {
                    case Builder.DB_TYPE_COLLECT:
                        //TODO
                        final View collect = contentView.findViewById(value.viewId);
                        if (collect != null) {
                            UserCenterUtils.getCollectState(mBuilder.contentUUid, new ICollectionStatusCallback() {
                                public void notifyCollectionStatus(boolean status) {
                                    if (collect instanceof FocusToggleSelect) {
                                        ((FocusToggleSelect) collect).setSelect(status);
                                    }
                                }
                            });
                            collect.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (collect instanceof FocusToggleSelect) {
                                        if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                            if (((FocusToggleSelect) collect).isSelect()) {
                                                UserCenterUtils.deleteSomeCollect(mInfo,
                                                        mBuilder.contentUUid, new DBCallback<String>() {
                                                            @Override
                                                            public void onResult(int code, String result) {
                                                                ((FocusToggleSelect) collect).setSelect
                                                                        (code == 0 && !TextUtils.isEmpty(result));
                                                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                                                if (code == 0) {
                                                                    LogUploadUtils.uploadLog
                                                                            (Constant.LOG_NODE_COLLECT, "1," + mBuilder.contentUUid);//取消收藏
                                                                    Toast.makeText(getContext().getApplicationContext(),
                                                                            "取消收藏成功", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                UserCenterUtils.addCollect(mInfo, new DBCallback<String>() {
                                                    @Override
                                                    public void onResult(int code, String result) {
                                                        ((FocusToggleSelect) collect).setSelect(code ==0 && !TextUtils
                                                                        .isEmpty(result));
                                                        RxBus.get().post(Constant.UPDATE_UC_DATA,true);
                                                        if (code == 0) {
                                                            LogUploadUtils.uploadLog(Constant
                                                                    .LOG_NODE_COLLECT, "0," +
                                                                    mInfo.getContentID());
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
                        final View Subscrip = contentView.findViewById
                                (value.viewId);
                        if (Subscrip != null) {
                            UserCenterUtils.getSuncribeState(mBuilder.contentUUid, new ISubscribeStatusCallback() {
                                @Override
                                public void notifySubScribeStatus(boolean status) {
                                    if (Subscrip instanceof FocusToggleSelect) {
                                        ((FocusToggleSelect) Subscrip).setSelect(status);
                                    }
                                }
                            });

                            Subscrip.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Subscrip instanceof FocusToggleSelect) {
                                        if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                            if (((FocusToggleSelect) Subscrip).isSelect()) {
                                                UserCenterUtils.deleteSomeSubcribet(mInfo,
                                                        mBuilder.contentUUid, new DBCallback<String>() {
                                                            @Override
                                                            public void onResult(int code, String result) {
                                                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                                                if (code == 0) {
                                                                    ((FocusToggleSelect) Subscrip)
                                                                            .setSelect(false);
                                                                    ((FocusToggleSelect) Subscrip).setSelect(false);
                                                                    LogUploadUtils.uploadLog(Constant.LOG_NODE_SUBSCRIP, "1," +
                                                                            mInfo.getContentUUID());
                                                                    Toast.makeText(getContext()
                                                                                    .getApplicationContext(), "取消订阅成功",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                if (mInfo != null) {
                                                    UserCenterUtils.addSubcribe(mInfo, currentPlayIndex, currentPosition, new DBCallback<String>() {
                                                        @Override
                                                        public void onResult(int code, String result) {
                                                            if (code == 0) {
                                                                ((FocusToggleSelect) Subscrip).setSelect(true);
                                                                LogUploadUtils.uploadLog(Constant.LOG_NODE_SUBSCRIP, "0," +
                                                                        mInfo.getContentUUID());
                                                                Toast.makeText(getContext().getApplicationContext(),
                                                                        "添加订阅成功", Toast.LENGTH_SHORT).show();
                                                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
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
                    case Builder.DB_TYPE_VIPPAY:
                        vipPay = contentView.findViewById(value.viewId);
                        break;
                    case Builder.DB_TYPE_VIPTIP:
                        TextView vipTip = contentView.findViewById
                                (value.viewId);
                        if (!TextUtils.isEmpty(memberStatus) && (memberStatus == QueryUserStatusUtil.SIGN_MEMBER_OPEN_GOOD)) {
                            vipTip.setVisibility(View.VISIBLE);
                            vipTip.setText(String.format(vipTip.getText().toString(), expireTime));
                        }
                        break;
                }
            }
        }
    }

    private void setVipPayStatus(Content info) {
        Log.d("ywy ", "ywy info : " + info.toString());
        if (null != vipPay && info != null && info.getVipFlag() != null) {
            final int vipState = Integer.parseInt(info.getVipFlag());
            if (vipState > 0) {
                vipPay.setVisibility(View.VISIBLE);
                ((FocusToggleView2) vipPay).setSelect(true);
            }
        }
    }

    private void onLoadError(String message) {
        Toast.makeText(LauncherApplication.AppContext, "HeadPlayerView:" + message, Toast
                .LENGTH_SHORT).show();
    }

    public void setProgramSeriesInfo(Content programSeriesInfo) {
        if (programSeriesInfo == null) return;
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

        if(historyBean != null && programSeriesInfo.getData() != null){
            for(SubContent content : programSeriesInfo.getData()){
                if(TextUtils.equals(content.getContentUUID(),historyBean.playId)){
                    int index = programSeriesInfo.getData().indexOf(content);
                    setCurrentPlayIndex("history",index);
                    break;
                }
            }
        }

        mInfo = programSeriesInfo;
        if (playerView != null && !isPlayLive) {
            playerView.setSeriesInfo(programSeriesInfo);
            playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
        } else {
            mPlayInfo = new PlayInfo(currentPlayIndex, currentPosition);
        }
        setVipPayStatus(mInfo);
    }

    //播放
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

            playerView.setSeriesInfo(mInfo);
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
//=======
//    private void parseResult(String result) {
//        if (TextUtils.isEmpty(result)) {
//            onLoadError("获取结果为空");
//            return;
//        }
//        try {
//            JSONObject object = new JSONObject(result);
//            if (object.getInt("errorCode") == 0) {
//                JSONObject obj = object.getJSONObject("data");
//                Gson gson = new Gson();
//                mInfo = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
//                mInfo.resolveVip();
//
//                if (mBuilder.infoResult != null) {
//                    mBuilder.infoResult.onResult(mInfo);
//                }
//>>>>>>> 1.4:app/src/main/java/tv/newtv/cboxtv/views/detailpage/HeadPlayerView.java

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

        //TODO 栏目化直播 直播判断
        if (mInfo.getLiveLoopParam() != null) {
            LiveInfo liveInfo = new LiveInfo(mInfo);
            if (liveInfo.isLiveTime()) {
                //需要直播
                playerView.playLive(liveInfo, false,this);
                isPlayLive = true;
            }
        } else {
            if (mInfo != null && mPlayInfo != null) {
                playerView.setSeriesInfo(mInfo);
                playerView.playSingleOrSeries(mPlayInfo.index, mPlayInfo.position);
            }
        }
    }

    @Override
    public String getContentUUID() {
        return mBuilder.contentUUid;
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
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

        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
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

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {
        mBuilder.infoResult.onResult(null);
//=======
//        if (mInfo == null || TextUtils.isEmpty(mInfo.getPlayEndTime())) {
//            return;
//        }
//        LiveTimingUtil.endTime(mInfo.getPlayEndTime(), new LiveTimingUtil.LiveEndListener() {
//            @Override
//            public void end() {
//                if(!NewTVLauncherPlayerViewManager.getInstance().isLiving()){
//                    Log.i(TAG, "非直播时间，不结束播放");
//                    return;
//                }
//                if (playerView != null) {
//                    playerView.ExitFullScreen();
//                    playerView.setHintText("播放已结束");
//                    playerView.setHintTextVisible(View.VISIBLE);
//                    playerView.release();
//                }
//                TimeDialog.showBuilder(getContext(), "播放时间已结束，您可以观看其它视频.",
//                        new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (playerView != null && playerView.isReleased()) {
//                            Log.e(TAG, "player view is released, rebuild it....");
//                            playerView.destory();
//                            playerView = null;
//                            prepareMediaPlayer();
//                        }
//                        if (playerView != null && currentProgramSeriesInfo != null) {
//                            playerView.setSeriesInfo(currentProgramSeriesInfo);
//                            playerView.playSingleOrSeries(0, 0);
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onContentResult(@Nullable Content content) {
        mBuilder.infoResult.onResult(content);
        setProgramSeriesInfo(content);
        parseResult();
    }

    @Override
    public void onSubContentResult(@Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void onTimeChange(String current, String end) {

    }

    @Override
    public void onComplete() {

    }


    public interface InfoResult {
        void onResult(@Nullable Content info);
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
        public static final int DB_TYPE_VIPPAY = 3;
        public static final int DB_TYPE_VIPTIP = 4;

        @SuppressWarnings("UnusedAssignment")
        private int mLayout = -1;
        private int mPlayerId = -1;
        private int mPlayerFocusId = -1;
        private List<Integer> clickables;
        private List<Integer> focusables;
        private OnClickListener clickListener;
        private PlayerCallback playerCallback;
        private VideoExitFullScreenCallBack videoExitFullScreenCallBack;
        private OnFocusChangeListener focusChangeListener;
        private InfoResult infoResult;
        private String contentUUid;
        private int ProgramType;
        private boolean autoGetSub = false;
        private List<CustomFrame> dbTypes;
        private int defaultFocusID = 0;

        private ExitVideoFullCallBack videoFullCallBack;

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
            videoExitFullScreenCallBack = null;
        }

        public Builder CheckFromDB(CustomFrame... types) {
            dbTypes = Arrays.asList(types);
            return this;
        }

        public Builder autoGetSubContents() {
            autoGetSub = true;
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


        public Builder SetVideoExitFullScreenCallBack(VideoExitFullScreenCallBack callBack) {
            this.videoExitFullScreenCallBack = callBack;
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
