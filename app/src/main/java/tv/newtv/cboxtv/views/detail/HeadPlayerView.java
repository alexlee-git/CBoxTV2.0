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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.DescriptionActivity;
import tv.newtv.cboxtv.player.LiveListener;
import tv.newtv.cboxtv.player.model.LiveInfo;
import tv.newtv.cboxtv.player.util.PlayInfoUtil;
import tv.newtv.cboxtv.player.videoview.ExitVideoFullCallBack;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IHisoryStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyMemberStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.ISubscribeStatusCallback;
import tv.newtv.cboxtv.uc.v2.sub.QueryUserStatusUtil;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.TimeDialog;
import tv.newtv.cboxtv.views.custom.FocusToggleSelect;
import tv.newtv.cboxtv.views.custom.FocusToggleView2;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         11:23
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class HeadPlayerView extends RelativeLayout implements IEpisode, View.OnClickListener,
        ContentContract.View, LiveListener {

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

    private Boolean isPlayLive = false;
    private String memberStatus;
    private String expireTime;

    private long lastClickTime = 0;
    private View vipPay;
    private TextView vipTip;

    //检测全屏退出回调
    private VideoExitFullScreenCallBack videoExitFullScreenCallBack = new
            VideoExitFullScreenCallBack() {
                @Override
                public void videoEitFullScreen() {
                    if (mBuilder != null && mBuilder.videoExitFullScreenCallBack != null) {
                        mBuilder.videoExitFullScreenCallBack.videoEitFullScreen();
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
                    currentPosition = defaultConfig.playPosition;
                    playerView = new VideoPlayerView(defaultConfig, getContext());
                    if (defaultConfig.defaultFocusView instanceof VideoPlayerView) {
                        playerView.requestFocus();
                    }
                }
            }
            if (playerView != null) {
                playerView.setPlayerCallback(mBuilder.playerCallback);
                playerView.setVideoExitCallback(videoExitFullScreenCallBack);
            }
        }
    }

    public Content getInfo() {
        return mInfo;
    }

    //数据库是异步查询，网络快于数据库查询的时候导致历史进度还没渠道就开始播放视频
    private void initData(final boolean isRequest) {
        UserCenterUtils.getHistoryState(DBConfig.CONTENTUUID, mBuilder.contentUUid, "", new
                IHisoryStatusCallback() {
                    @Override
                    public void getHistoryStatus(UserCenterPageBean.Bean bean) {
                        historyBean = bean;
                        if (historyBean != null) {
                            if (!TextUtils.isEmpty(historyBean.playPosition)) {
                                currentPosition = Integer.valueOf(historyBean.playPosition);
                            } else {
                                currentPosition = 0;
                            }
                        }

                        if (isRequest) {
                            mPresenter.getContent(mBuilder.contentUUid, mBuilder.autoGetSub);
                        }
                    }

                    @Override
                    public void onError() {
                        if (isRequest) {
                            mPresenter.getContent(mBuilder.contentUUid, mBuilder.autoGetSub);
                        }
                    }
                });
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);
    }

    private void getMemberStatus(String UUid) {
        UserCenterUtils.getMemberStatus(UUid, new INotifyMemberStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(String status, Bundle memberBundle) {
                memberStatus = status;
                if (memberBundle != null) {
                    Log.d(TAG, "bundle : " + memberBundle.get(QueryUserStatusUtil.expireTime));
                    expireTime = (String) memberBundle.get(QueryUserStatusUtil.expireTime);
                }

                Log.d(TAG, "memberStatus : " + memberStatus + "time : " + expireTime);
                if (!TextUtils.isEmpty(memberStatus) &&
                        (memberStatus.equals(QueryUserStatusUtil.SIGN_MEMBER_OPEN_GOOD)) && vipTip != null) {
                    vipTip.setVisibility(View.VISIBLE);
                    String time = TimeUtil.getInstance().getDateFromSeconds(
                            String.valueOf(TimeUtil.getInstance().getSecondsFromDate(expireTime)));

                    StringBuffer str = new StringBuffer();
                    str.append(getResources().getString(R.string.vip_tip1));
                    str.append(time);
                    str.append(getResources().getString(R.string.vip_tip2));
                    Log.d(TAG, "str : " + str.toString());
                    vipTip.setText(str.toString());

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

            releasePlayer();
        }
    }

    public void releasePlayer() {
        if (playerView != null) {
            playerView.release();
            playerView.destory();
            playerView = null;
        }
    }

    public void onActivityStop() {
        if (playerView != null) {
            currentPosition = playerView.getCurrentPosition();
            defaultConfig = playerView.getDefaultConfig();

            releasePlayer();
        }
    }

    public void onActivityResume() {
        if (mInfo != null) {
            getMemberStatus(mInfo.getContentUUID());
        }
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

        if (playerView != null && mInfo != null) {
            Log.e(TAG, "player view is builded, playVod vod video....index=" + currentPlayIndex + " " +
                    "pos=" + currentPosition);
            startPlayerView(isPlayLive);
        }
    }

    public void resetSeriesInfo(final Content content) {
        initData(false);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setProgramSeriesInfo(content);
            }
        }, 300);
    }

    //显示全屏
    public void EnterFullScreen(Activity activity) {
        playerView.enterFullScreen(activity);
    }

    public void Build(Builder builder) {
        mBuilder = builder;
        initData(true);
        if (mBuilder.playerCallback == null) return;
        if (mBuilder.contentUUid == null) return;
        if (mBuilder.mPlayerId == -1) return;
        if (mBuilder.mLayout == -1) return;
        if (mBuilder.clickables != null && mBuilder.clickListener == null) return;
        if (mBuilder.focusables != null && mBuilder.focusChangeListener == null) return;

        contentView = LayoutInflater.from(getContext()).inflate(mBuilder.mLayout, this, false);
        final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.up_top_, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        inflate.layout(0, (int) getResources().getDimension(R.dimen.height_27px),0,0);
        inflate.setLayoutParams(lp);
//        if (mBuilder.fromOuter) {
            Log.e("yml", "Build: ..1" );
            addView(inflate);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeView(inflate);
                }
            }, 5000);
//        }
        addView(contentView);
        checkDataFromDB();

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
        //mPresenter.getContent(mBuilder.contentUUid, mBuilder.autoGetSub);
    }

    private void checkDataFromDB() {
        if (mBuilder != null && mBuilder.dbTypes != null && mBuilder.dbTypes.size() > 0) {
            for (CustomFrame value : mBuilder.dbTypes) {
                switch (value.dbType) {
                    case Builder.DB_TYPE_COLLECT:
                        //TODO
                        final View collect = contentView.findViewById(value.viewId);
                        if (collect != null) {
                            UserCenterUtils.getCollectState(mBuilder.contentUUid, new
                                    ICollectionStatusCallback() {
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
                                                        mBuilder.contentUUid, new
                                                                DBCallback<String>() {
                                                                    @Override
                                                                    public void onResult(int code, String
                                                                            result) {
                                                                        ((FocusToggleSelect)
                                                                                collect)
                                                                                .setSelect
                                                                                        (code ==
                                                                                                0
                                                                                                && !TextUtils
                                                                                                .isEmpty(result));
                                                                        RxBus.get().post(Constant
                                                                                        .UPDATE_UC_DATA,
                                                                                true);
                                                                        Map<String, String> map = new HashMap<>();
                                                                        map.put("col_operation_type", "delete");
                                                                        map.put("col_operation_id", mInfo.getContentID());
                                                                        RxBus.get().post("col_operation_map", map);

                                                                        if (code == 0) {
                                                                            LogUploadUtils.uploadLog
                                                                                    (Constant
                                                                                            .LOG_NODE_COLLECT, "1," + mBuilder.contentUUid);//取消收藏
                                                                            Toast.makeText
                                                                                    (getContext()
                                                                                                    .getApplicationContext(),
                                                                                            "取消收藏成功", Toast
                                                                                                    .LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                            } else {
                                                UserCenterUtils.addCollect(mInfo, currentPlayIndex, new
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
                                                                                .UPDATE_UC_DATA,
                                                                        true);

                                                                Map<String, String> map = new HashMap<>();
                                                                map.put("col_operation_type", "add");
                                                                map.put("col_operation_id", mInfo.getContentID());
                                                                RxBus.get().post("col_operation_map", map);
                                                                if (code == 0) {
                                                                    LogUploadUtils.uploadLog
                                                                            (Constant
                                                                                            .LOG_NODE_COLLECT,
                                                                                    "0," +
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
                            UserCenterUtils.getSuncribeState(mBuilder.contentUUid, new
                                    ISubscribeStatusCallback() {
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
                                                        mBuilder.contentUUid, new
                                                                DBCallback<String>() {
                                                                    @Override
                                                                    public void onResult(int code, String
                                                                            result) {
                                                                        RxBus.get().post(Constant
                                                                                        .UPDATE_UC_DATA,
                                                                                true);
                                                                        if (code == 0) {
                                                                            ((FocusToggleSelect)
                                                                                    Subscrip)
                                                                                    .setSelect
                                                                                            (false);
                                                                            ((FocusToggleSelect)
                                                                                    Subscrip)
                                                                                    .setSelect
                                                                                            (false);
                                                                            LogUploadUtils.uploadLog
                                                                                    (Constant
                                                                                            .LOG_NODE_SUBSCRIP, "1," +
                                                                                            mInfo.getContentUUID());
                                                                            Toast.makeText
                                                                                    (getContext()
                                                                                                    .getApplicationContext(), "取消订阅成功",
                                                                                            Toast.LENGTH_SHORT)
                                                                                    .show();

                                                                            Map<String, String> param = new HashMap<>();
                                                                            param.put("operation_type", "delete");
                                                                            param.put("operation_id", mInfo.getContentID());
                                                                            RxBus.get().post("operation_param", param);
                                                                        }
                                                                    }
                                                                });
                                            } else {
                                                if (mInfo != null) {
                                                    UserCenterUtils.addSubcribe(mInfo,
                                                            currentPlayIndex, currentPosition,
                                                            new DBCallback<String>() {
                                                                @Override
                                                                public void onResult(int code,
                                                                                     String
                                                                                             result) {
                                                                    if (code == 0) {
                                                                        ((FocusToggleSelect)
                                                                                Subscrip)
                                                                                .setSelect(true);
                                                                        LogUploadUtils.uploadLog
                                                                                (Constant
                                                                                        .LOG_NODE_SUBSCRIP, "0," +
                                                                                        mInfo.getContentUUID());
                                                                        Toast.makeText(getContext()
                                                                                        .getApplicationContext(),
                                                                                "添加订阅成功", Toast
                                                                                        .LENGTH_SHORT)
                                                                                .show();
                                                                        RxBus.get().post(Constant
                                                                                        .UPDATE_UC_DATA,
                                                                                true);

                                                                        Map<String, String> param = new HashMap<>();
                                                                        param.put("operation_type", "add");
                                                                        param.put("operation_id", mInfo.getContentID());
                                                                        RxBus.get().post("operation_param", param);
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
                        vipTip = contentView.findViewById
                                (value.viewId);
                        break;
                }
            }
        }
    }

    private void setVipPayStatus(Content info) {
        Log.d("ywy ", "ywy info : " + info.toString());
        if (null != vipPay && info != null && !TextUtils.isEmpty(info.getVipFlag())) {
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

        if(CmsUtil.isVideoTv(programSeriesInfo)) {
            final boolean isDesc = "1".equals(programSeriesInfo.getPlayOrder());
            if (programSeriesInfo.getData() != null && programSeriesInfo.getData().size() > 0) {
                Collections.sort(programSeriesInfo.getData(), new Comparator<SubContent>() {
                    @Override
                    public int compare(SubContent t1, SubContent t2) {
                        try {
                            String valueA = TextUtils.isEmpty(t1.getPeriods()) ? "0" : t1.getPeriods();
                            String valueB = TextUtils.isEmpty(t2.getPeriods()) ? "0" :
                                    t2.getPeriods();
                            if (isDesc) {
                                return Integer.parseInt(valueB) - Integer.parseInt
                                        (valueA);
                            }
                            return Integer.parseInt(valueA) - Integer.parseInt(valueB);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
            }
        }

        if (programSeriesInfo.getData() != null) {
            String playId = mBuilder.childContentUUID;
            if (TextUtils.isEmpty(playId) && historyBean != null) {
                playId = historyBean.playId;
            }
            if (!TextUtils.isEmpty(playId)) {
                for (SubContent content : programSeriesInfo.getData()) {
                    if (TextUtils.equals(content.getContentUUID(), playId)) {
                        int index = programSeriesInfo.getData().indexOf(content);
                        setCurrentPlayIndex("history", index);
                        break;
                    }
                }
            }
        }

        mInfo = programSeriesInfo;
        setVipPayStatus(mInfo);
        getMemberStatus(mInfo.getContentUUID());
        parseResult();
    }

    public int translateIndex(int index, Content content) {
        return CmsUtil.translateIndex(content, index);
    }

    //播放
    public void Play(int index, int postion, boolean requestFocus) {
        if (isPlayLive) {
            isPlayLive = false;
            if (playerView != null) {
                playerView.setHintTextVisible(View.GONE);
            }
        } else if (currentPlayIndex == index && !isPlayLive) {
            if (requestFocus) {
                requestPlayerFocus();
            }
            return;
        }

        releasePlayer();
        prepareMediaPlayer();

        setCurrentPlayIndex("Play", index);
        currentPosition = postion;

        startPlayerView(!requestFocus);

        if (requestFocus) {
            requestPlayerFocus();
        }
    }

    public boolean isFullScreen() {
        if (playerView != null) {
            return playerView.getDefaultConfig().isFullScreen;
        }
        return false;
    }

    private void requestPlayerFocus() {
        if (playerView == null) {
            return;
        }
        playerView.requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBuilder.playerCallback != null) {
                    mBuilder.playerCallback.onPlayerClick(playerView);
                }
            }
        }, 500);
    }

    private void parseResult() {
        if (mInfo == null && contentView == null) return;

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

        if (content != null && !TextUtils.isEmpty(mInfo.getDescription())) {
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

        startPlayerView(true);
    }

    private void startPlayerView(boolean checkLive) {
        //TODO 栏目化直播 直播判断
        if (mInfo != null) {
            prepareMediaPlayer();
            if (mInfo.getLiveParam() != null) {
                final LiveInfo liveInfo = new LiveInfo(mInfo);
                if (liveInfo.isLiveTime() && checkLive) {
                    //需要直播
                    if (!TextUtils.isEmpty(mInfo.getLvID())) {
                        mPresenter.getContent(mInfo.getLvID(), false, new ContentContract.View() {
                            @Override
                            public void onContentResult(@NotNull String uuid, @org.jetbrains
                                    .annotations.Nullable Content content) {
                                if (content != null) {
                                    String playUrl = content.getPlayUrl();
                                    liveInfo.setLiveUrl(playUrl);
                                    isPlayLive = true;
                                    playerView.playLive(liveInfo, false, HeadPlayerView.this);
                                } else {
                                    onComplete();
                                }
                            }

                            @Override
                            public void onSubContentResult(@NotNull String uuid, @org.jetbrains
                                    .annotations.Nullable
                                    ArrayList<SubContent> result) {
                            }

                            @Override
                            public void tip(@NotNull Context context, @NotNull String message) {
                            }

                            @Override
                            public void onError(@NotNull Context context, @org.jetbrains.annotations
                                    .Nullable String desc) {
                                play();
                            }
                        });
                    } else {
                        play();
                    }
                } else {
                    play();
                }
            } else {
                play();
            }
        }
    }

    private void play() {
        if (TimeDialog.isDisMiss){
            TimeDialog.dismiss();
        }
        playerView.beginChange();
        playerView.setSeriesInfo(mInfo);
        playerView.playSingleOrSeries(currentPlayIndex, currentPosition);
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
    }

    @Override
    public void onContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable Content
            content) {
        mBuilder.infoResult.onResult(content);
        setProgramSeriesInfo(content);
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable
            ArrayList<SubContent> result) {

    }

    @Override
    public void onTimeChange(String current, String end) {
        //TODO 直播时间改变

        if (playerView != null) {
            if (Libs.get().isDebug()) {
                playerView.setTipText(String.format("正在播放 %s/%s", current, end));
            }
        }
    }

    @Override
    public void onComplete() {
        defaultConfig = playerView.getDefaultConfig();
        //栏目化直播结束，继续播放点播视频
        TimeDialog.showBuilder(getContext(),this);

    }
    public void continuePlayVideo(){
        if (playerView != null) {
            playerView.release();
            playerView.destory();
            playerView = null;
        }

        if (mInfo != null) {
            startPlayerView(false);
        }
    }


    public interface InfoResult {
        void onResult(@Nullable Content info);
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
        private String childContentUUID;
        private int ProgramType;
        private boolean autoGetSub = false;
        private List<CustomFrame> dbTypes;
        private int defaultFocusID = 0;
        private boolean fromOuter;

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

        public Builder SetContentUUID(String uuid, String subContentUUID) {
            contentUUid = uuid;
            childContentUUID = subContentUUID;
            return this;
        }

        public Builder setTopView(boolean fromOuter) {
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
