package tv.newtv.cboxtv.player.newtv;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.gridsum.videotracker.GSVideoState;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.Utils;
import com.newtv.libs.util.YSLogUtils;

import java.util.LinkedHashMap;

import tv.icntv.been.IcntvPlayerInfo;
import tv.icntv.icntvplayersdk.IcntvPlayer;
import tv.icntv.icntvplayersdk.iICntvPlayInterface;
import tv.newtv.cboxtv.player.IVodVideoPlayerInterface;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.PlayerUrlConfig;
import tv.newtv.cboxtv.player.iPlayCallBackEvent;
import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by wangkun on 2018/1/15.
 */

public class NewTVVodVideoPlayer implements IVodVideoPlayerInterface {

    private static final String TAG = "NewTVVodVideoPlayer";
    private static NewTVVodVideoPlayer mNewTVVodVideoPlayer;
    private iPlayCallBackEvent mIPlayCallBackEvent;
    private IcntvPlayer mIcntvPlayer;
    private Context mContext;

//    private VideoTracker mVideoTracker;
//    private IVodInfoProvider mIVodInfoProvider;
//    private VodPlay mVodPlay;
//    private VideoInfo mVideoInfo;
//    private VodMetaInfo mVodMetaInfo;


    private boolean isSuccessful = false;
    // 央视网日志所需参数信息
    private String ColumnName;
    private String outSourceId, outSourcePlayUrl;
    private String videoName;
    // 判断正片是否开始启播，目的是与前贴片广告播放分离
    private boolean mIsPositiveOnPrepared = false;
    private iICntvPlayInterface mIcntvPlayerCallback = new iICntvPlayInterface() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> linkedHashMap) {
            Log.i(TAG, "onPrepared: ");
            // 点播起播日志上传
            if (mIcntvPlayer != null) {
                // 央视网日志：加载视频成功标识
                isSuccessful = true;
//                if (mVodPlay != null && mVodMetaInfo != null) {
//                    mVodMetaInfo.videoDuration = mIcntvPlayer.getDuration() / 1000;
//                    Log.i(TAG,
//                            "---播放原始时长:"
//                                    + mIcntvPlayer.getDuration());
//                    Log.i(TAG, "---播放时长:"
//                            + mVodMetaInfo.videoDuration);
//
//                    // 央视网日志：结束加载
//                    mVodPlay.endPreparing(isSuccessful, mVodMetaInfo);
//                    Log.i(TAG, "---结束加载开始播放正片");
//                    mVodPlay.onStateChanged(GSVideoState.PLAYING); // 播放时调用
//                    Log.i(TAG,
//                            "---onPrepared:mVodPlay.onStateChanged(GSVideoState.PLAYING)");
//                }

                if (YSLogUtils.getInstance(mContext).getmVodPlay() != null && YSLogUtils
                        .getInstance(mContext).getmVodMetaInfo() != null) {
                    YSLogUtils.getInstance(mContext).getmVodMetaInfo().videoDuration =
                            mIcntvPlayer.getDuration() / 1000;
                    Log.i(TAG,
                            "---播放原始时长:"
                                    + mIcntvPlayer.getDuration());
                    Log.i(TAG, "---播放时长:"
                            + YSLogUtils.getInstance(mContext).getmVodMetaInfo().videoDuration);

                    // 央视网日志：结束加载
                    YSLogUtils.getInstance(mContext).getmVodPlay().endPreparing(isSuccessful,
                            YSLogUtils.getInstance(mContext).getmVodMetaInfo());
                    Log.i(TAG, "---结束加载开始播放正片");
                    YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                            .PLAYING); // 播放时调用
                    Log.i(TAG,
                            "---onPrepared:mVodPlay.onStateChanged(GSVideoState.PLAYING)");
                }
            }
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onPrepared(linkedHashMap);
            }
        }

        @Override
        public void onCompletion(int type) {
            Log.i(TAG, "onCompletion: " + type);
            if(type ==  iICntvPlayInterface.VIDEO_COMPLETE_TYPE ||
                    type == iICntvPlayInterface.AFTER_AD_COMPLETE_TYPE) {
                // 点播结束日志上传
                if (mIcntvPlayer != null) {

                    //slp
                    if (YSLogUtils.getInstance(mContext).getmVodPlay() != null &&
                            mIsPositiveOnPrepared) {

                        // 央视网日志：
                        YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged
                                (GSVideoState.STOPPED); // 视频播放完成时调用
                        YSLogUtils.getInstance(mContext).getmVodPlay().endPlay();
                        Log.i(TAG,
                                "---mVodPlay.onStateChanged(GSVideoState.STOPPED)");
                        Log.i(TAG, "---结束播放endPlay()");
                    }
                }
                if (mIPlayCallBackEvent != null) {
                    mIPlayCallBackEvent.onCompletion();
                }
            }
        }

        @Override
        public void onBufferStart(String s) {
            Log.i(TAG, "onBufferStart: " + s);
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onVideoBufferStart(s);
            }
            if (s.equals("VideoStartBuffer")) {
                mIsPositiveOnPrepared = true;
                Log.i(TAG, "---正片播放的状态:mIsPositiveOnPrepared = true");
                // 正片播放后，央视网日志初始化
                if (mIcntvPlayer != null) {
                    // 初始化央视网日志SDK，开始上传信息

                    YSLogUtils.getInstance(mContext).setmIcntvPlayer(mIcntvPlayer);
                    // 央视网日志：视频开始加载
                    if (!outSourceId.equals(PlayerUrlConfig.getInstance().getPlayingContentId()))
                    {//判断小屏时是否已经播放，空时未播放
                        //slp
                        YSLogUtils.getInstance(mContext).initCNTVVideoTracker(YSLogUtils
                                .YS_VOD_LOG);//初始化
                        if (YSLogUtils.getInstance(mContext).getmVodPlay() != null) {
                            YSLogUtils.getInstance(mContext).getmVodPlay().beginPreparing();
                            PlayerUrlConfig.getInstance().setPlayingContentId(outSourceId);
                            PlayerUrlConfig.getInstance().setPlayUrl(outSourcePlayUrl);
                            Log.i(TAG, "----onBufferStart:视频开始加载");
                        }
                    }

                }

            }
            if (s.equals(ON_BUFFER_START_TYPE_701_STATUS)
                    && mIcntvPlayer != null) {
//                if (mVodPlay != null && mIsPositiveOnPrepared) {
//                    // 央视网日志：卡顿时调用
//                    mVodPlay.onStateChanged(GSVideoState.BUFFERING); // 卡顿时调用
//                    Log.i(TAG,
//                            "----onBufferStart:mVodPlay.onStateChanged(GSVideoState.BUFFERING)");
//                }


                //slp
                if (YSLogUtils.getInstance(mContext).getmVodPlay() != null &&
                        mIsPositiveOnPrepared) {
                    // 央视网日志：卡顿时调用
                    YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                            .BUFFERING); // 卡顿时调用
                    Log.i(TAG,
                            "----onBufferStart:mVodPlay.onStateChanged(GSVideoState.BUFFERING)");
                }
            }
        }

        @Override
        public void onBufferEnd(String s) {
            Log.i(TAG, "onBufferEnd: " + s);
//            if (s.equals(ON_BUFFER_END_TYPE_702_STATUS)
//                    && mIcntvPlayer != null) {
//                if (mVodPlay != null && mIsPositiveOnPrepared) {
//                    // 央视网日志：播放时调用
//                    mVodPlay.onStateChanged(GSVideoState.PLAYING); // 播放时调用
//                    Log.i(TAG,
//                            "---mVodPlay.onStateChanged(GSVideoState.PLAYING)");
//                }
//            }

            if (s.equals(ON_BUFFER_END_TYPE_702_STATUS)
                    && mIcntvPlayer != null) {
                if (YSLogUtils.getInstance(mContext).getmVodPlay() != null &&
                        mIsPositiveOnPrepared) {
                    // 央视网日志：播放时调用
                    YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                            .PLAYING); // 播放时调用
                    Log.i(TAG,
                            "---mVodPlay.onStateChanged(GSVideoState.PLAYING)");
                }
            }
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onVideoBufferEnd(s);
            }
        }

        @Override
        public void onError(int i, int i1, String s) {
            Log.i(TAG, "onError: i=" + i + " i1=" + i1 + " s=" + s);
            // 央视网日志：播放结束
//            if (mVodPlay != null && mIsPositiveOnPrepared) {
//                mVodPlay.endPlay();// 播放结束
//                Log.i(TAG, "---onError:结束播放endPlay()");
//            }

            //slp
            if (YSLogUtils.getInstance(mContext).getmVodPlay() != null && mIsPositiveOnPrepared) {
                YSLogUtils.getInstance(mContext).getmVodPlay().endPlay();// 播放结束
                Log.i(TAG, "---onError:结束播放endPlay()");
            }

            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onError(i, i1, s);
            }
        }

        @Override
        public void onTimeout(int i) {
            Log.i(TAG, "onTimeout: ");
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onTimeout(i);
            }
        }
    };

    private NewTVVodVideoPlayer(Context context) {
        mContext = context;
    }

    public static NewTVVodVideoPlayer getInstance(Context context) {
        if (mNewTVVodVideoPlayer == null) {
            synchronized (NewTVVodVideoPlayer.class) {
                if (mNewTVVodVideoPlayer == null) {
                    mNewTVVodVideoPlayer = new NewTVVodVideoPlayer(context);
                }
            }
        }

        return mNewTVVodVideoPlayer;
    }

    @Override
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent
            callBack, VideoDataStruct videoDataStruct) {

        if (context == null) {
            Log.i(TAG, "playVideo: context==null");
            return false;
        }
        if (frameLayout == null) {
            Log.i(TAG, "playVideo: frameLayout==null");
            return false;
        }
        if (callBack != null) {
            mIPlayCallBackEvent = callBack;
        }
        if (videoDataStruct == null) {
            Log.i(TAG, "playVideo: videoDataStruct==null");
            return false;
        }
        Log.i(TAG, "playVideo: " + videoDataStruct);
        IcntvPlayerInfo icntvPlayerInfo = new IcntvPlayerInfo();
        icntvPlayerInfo.setAppKey(Libs.get().getAppKey());
        icntvPlayerInfo.setChannalId(Libs.get().getChannelId());
        icntvPlayerInfo.setCdnDispatchUrl(Constant.BASE_URL_CDN);
        icntvPlayerInfo.setDynamicKeyUrl(Constant.DYNAMIC_KEY);
        icntvPlayerInfo.setPlayUrl(videoDataStruct.getPlayUrl());
        icntvPlayerInfo.setProgramListID(videoDataStruct.getSeriesId());
        icntvPlayerInfo.setDuration(videoDataStruct.getDuration() * 60 * 1000);
        icntvPlayerInfo.setProgramID(videoDataStruct.getProgramId());
        icntvPlayerInfo.setAdModel(PlayerConfig.getInstance().getJumpAD());
        icntvPlayerInfo.setKey(videoDataStruct.getKey());
        icntvPlayerInfo.setDeviceID(Constant.UUID);
        //extend字段
        icntvPlayerInfo.setExtend(Utils.buildExtendString(PlayerConfig.getInstance().getColumnId
                (), PlayerConfig.getInstance().getSecondColumnId(), PlayerConfig.getInstance()
                .getFirstChannelId(), PlayerConfig.getInstance().getSecondChannelId(), PlayerConfig
                .getInstance().getTopicId()));
        String secondColumn = parseCategoryIds(videoDataStruct.getCategoryIds(), icntvPlayerInfo);
        addExtend(icntvPlayerInfo, "secondcolumn", secondColumn);
        addExtend(icntvPlayerInfo, "program", videoDataStruct.getProgramId());

        Log.i(TAG, "setExtend: " + icntvPlayerInfo.getExtend() + ",columnId:" + icntvPlayerInfo
                .getColumnId());
        // 央视网日志：初始化Tracker对象
//        mVideoTracker = VideoTracker.getInstance("GVD-200099", "GSD-200098",
//                context.getApplicationContext());
        // 央视网日志：令正片开始播放的状态为false
        mIsPositiveOnPrepared = false;
        // 央视网日志：保存央视网所需要的信息
        outSourceId = videoDataStruct.getProgramId();
        outSourcePlayUrl = videoDataStruct.getPlayUrl();
        videoName = videoDataStruct.getTitle();

        //slp
        YSLogUtils.getInstance(mContext).setOutSourceId(videoDataStruct.getProgramId());
        YSLogUtils.getInstance(mContext).setVideoName(videoDataStruct.getTitle());
        YSLogUtils.getInstance(mContext).setOutSourcePlayUrl(videoDataStruct.getPlayUrl());
        mIcntvPlayer = new IcntvPlayer(context, frameLayout, icntvPlayerInfo, mIcntvPlayerCallback);
        return true;
    }

    private void addExtend(IcntvPlayerInfo info, String key, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        String extend = info.getExtend();
        if (TextUtils.isEmpty(extend)) {
            info.setExtend(key + "=" + value);
        } else {
            info.setExtend(extend + "&" + key + "=" + value);
        }
    }

    /**
     * 一级栏目id集合直接设置，并返回二级栏目id集合，有可能为空
     *
     * @param categoryIds
     * @param info
     * @return
     */
    private String parseCategoryIds(String categoryIds, IcntvPlayerInfo info) {
        if(TextUtils.isEmpty(categoryIds)){
            return "";
        }
        StringBuilder secondColumn = new StringBuilder();

        String[] categoryArr = categoryIds.split("\\|");
        for(String categoryId : categoryArr){
            secondColumn.append(categoryId);
            secondColumn.append(",");
        }
        if(secondColumn.length() > 0){
            secondColumn.delete(secondColumn.length()-1,secondColumn.length());
        }
        return secondColumn.toString();
    }

    @Override
    public boolean stopVideo() {
        Log.i(TAG, "stopVideo: ");
//        if (mVodPlay != null && mIsPositiveOnPrepared) {
//            // 央视网日志：
//            mVodPlay.onStateChanged(GSVideoState.STOPPED); // 视频播放完成时调用
//            mVodPlay.endPlay();
//            Log.i(TAG,
//                    "---stopVideo:mVodPlay.onStateChanged(GSVideoState.STOPPED)");
//            Log.i(TAG, "---stopVideo:结束播放endPlay()");
//        }

        //slp
        if (YSLogUtils.getInstance(mContext).getmVodPlay() != null && mIsPositiveOnPrepared) {

            if (PlayerUrlConfig.getInstance().isFromDetailPage()) {
                YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                        .BUFFERING);
            } else {
                // 央视网日志：
                YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                        .STOPPED); // 视频播放完成时调用
                YSLogUtils.getInstance(mContext).getmVodPlay().endPlay();
            }

            Log.i(TAG,
                    "---stopVideo:mVodPlay.onStateChanged(GSVideoState.STOPPED)");
            Log.i(TAG, "---stopVideo:结束播放endPlay()");
        }
        if (mIcntvPlayer == null)
            return false;
        try {
            if (mIcntvPlayer.isPlaying() || mIcntvPlayer.isADPlaying()) {
                mIcntvPlayer.stopVideo();
                return true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return false;
    }

    @Override
    public boolean pauseVideo() {
        Log.i(TAG, "pauseVideo: ");
//        if (mVodPlay != null) {
//            // 央视网日志：
//            mVodPlay.onStateChanged(GSVideoState.PAUSED); // 暂停时调用
//            Log.i(TAG,
//                    "---pauseVideo:mVodPlay.onStateChanged(GSVideoState.PAUSED)");
//        }

        //slp
        if (YSLogUtils.getInstance(mContext).getmVodPlay() != null) {
            // 央视网日志：
            YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState.PAUSED);
            // 暂停时调用
            Log.i(TAG,
                    "---pauseVideo:mVodPlay.onStateChanged(GSVideoState.PAUSED)");
        }
        if (mIcntvPlayer == null)
            return false;
        if (mIcntvPlayer.isPlaying()) {
            mIcntvPlayer.pauseVideo();
            return true;
        }
        return false;
    }

    @Override
    public boolean start() {
        Log.i(TAG, "start: ");
//        if (mVodPlay != null) {
//            mVodPlay.onStateChanged(GSVideoState.PLAYING); // 播放时调用
//            Log.i(TAG, "---start:mVodPlay.onStateChanged(GSVideoState.PLAYING)");
//        }

        //slp
        if (YSLogUtils.getInstance(mContext).getmVodPlay() != null) {
            YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState.PLAYING);
            // 播放时调用
            Log.i(TAG, "---start:mVodPlay.onStateChanged(GSVideoState.PLAYING)");
        }
        if (mIcntvPlayer == null)
            return false;
        if (!mIcntvPlayer.isPlaying()) {
            mIcntvPlayer.startVideo();
            return true;
        }

        return false;
    }

    @Override
    public boolean isPlaying() {
        Log.i(TAG, "isPlaying: ");
        if (mIcntvPlayer != null) {
            return mIcntvPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void setDataSource(String definition) {
        Log.i(TAG, "setDataSource: ");
        if (mIcntvPlayer != null) {
            mIcntvPlayer.setDataSource(definition);
        }
    }

    @Override
    public boolean isADPlaying() {
        Log.i(TAG, "isADPlaying: ");
        if (mIcntvPlayer != null) {
            return mIcntvPlayer.isADPlaying();
        }
        return false;
    }

    @Override
    public int getDuration() {
//        Log.i(TAG, "getDuration: ");
        if (mIcntvPlayer != null) {
            return mIcntvPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
//        Log.i(TAG, "getCurrentPosition: ");
        if (mIcntvPlayer != null) {
            return mIcntvPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mIcntvPlayer != null) {
            Log.i(TAG, "seekTo------------->seekTo:" + position);
//            if (mVodPlay != null && mIsPositiveOnPrepared) {
//                // 央视网日志：快进拖拽时
//                mVodPlay.onStateChanged(GSVideoState.SEEKING); // 拖拽时调用
//                Log.i(TAG,
//                        "---seekTo:mVodPlay.onStateChanged(GSVideoState.SEEKING)");
//            }
//            YSLogUtils.getInstance(mContext).mIVodInfoProvider.getPosition();
            //slp
            if (YSLogUtils.getInstance(mContext).getmVodPlay() != null && mIsPositiveOnPrepared) {
                // 央视网日志：快进拖拽时
                YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                        .SEEKING); // 拖拽时调用
                Log.i(TAG,
                        "---seekTo:mVodPlay.onStateChanged(GSVideoState.SEEKING)");
            }
            try {
                mIcntvPlayer.seekTo(position);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    @Override
    public void releaseVideo() {
        Log.i(TAG, "releaseVideo------------->start!");
        if (mIcntvPlayer != null) {
//            if (mVodPlay != null && mIsPositiveOnPrepared) {
//                // 央视网日志：
//                mVodPlay.onStateChanged(GSVideoState.STOPPED); // 视频播放完成时调用
//                mVodPlay.endPlay();
//                Log.i(TAG,
//                        "---mVodPlay.onStateChanged(GSVideoState.STOPPED)");
//                Log.i(TAG, "---结束播放endPlay()");
//            }


            //slp
            if (YSLogUtils.getInstance(mContext).getmVodPlay() != null && mIsPositiveOnPrepared) {
                // 央视网日志：

                if (!PlayerUrlConfig.getInstance().isFromDetailPage()) {
                    YSLogUtils.getInstance(mContext).getmVodPlay().onStateChanged(GSVideoState
                            .STOPPED); // 视频播放完成时调用
                    YSLogUtils.getInstance(mContext).getmVodPlay().endPlay();
                }

                Log.i(TAG,
                        "---mVodPlay.onStateChanged(GSVideoState.STOPPED)");
                Log.i(TAG, "---结束播放endPlay()");
            }

            YSLogUtils.getInstance(mContext).release();


            mIcntvPlayer.release();
            mIcntvPlayer = null;
            // mNewTVVodVideoPlayer = null;
            mIPlayCallBackEvent = null;
        }
        mContext = null;
        mNewTVVodVideoPlayer = null;
    }

    @Override
    public void setVideoSize(int sizeType) {
        Log.i(TAG, "setVideoSize: ");
        if (mIcntvPlayer != null) {
            mIcntvPlayer.setVideoSize(sizeType);
        }
    }

    @Override
    public void setVideoSilent(boolean isSilent) {
        Log.i(TAG, "setVideoSilent:" + isSilent);
        if (mIcntvPlayer != null) {
            mIcntvPlayer.setVideoSilent(isSilent);
        }
    }


//    // 初始化央视网日志对象
//    private void initCNTVVideoTracker() {
//        // TODO Auto-generated method stub
//        mIVodInfoProvider = new IVodInfoProvider() {
//
//            @Override
//            public double getFramesPerSecond() {
//                // TODO Auto-generated method stub
//                return 0;
//            }
//
//            @Override
//            public double getBitrate() {
//                // TODO Auto-generated method stub
//                return 0;
//            }
//
//            @Override
//            public double getPosition() {
//                // TODO Auto-generated method stub
//                if (mIcntvPlayer != null) {
//                    // 获得当前播放头位置，返回值为秒，精确到小数点后2位
//                    double f1 = new BigDecimal(
//                            (float) mIcntvPlayer.getCurrentPosition() / 1000)
//                            .setScale(2, BigDecimal.ROUND_HALF_UP)
//                            .doubleValue();
//                    return f1;
//
//                }
//                return 0;
//            }
//        };
//        // 央视网日志：创建央视网日志videoInfo，"playID001"为视频的ID
//        if (!TextUtils.isEmpty(outSourceId)) {
//            mVideoInfo = new VideoInfo(outSourceId);
//        } else {
//            mVideoInfo = new VideoInfo("-");
//        }
//
//        Log.i(TAG, "---VideoID：" + mVideoInfo.VideoID);
//        mVideoInfo.VideoOriginalName = "-";
//        if (!videoName.equals("")) {
//            mVideoInfo.VideoName = videoName;
//        } else {
//            mVideoInfo.VideoName = "-";
//        }
//        Log.i(TAG, "---videoName:" + videoName);
//        // mVideoInfo.VideoUrl = videoDataStruct.getPlayUrl();
//        // Log.e("-------央视网日志：----",
//        // "-------------" + videoDataStruct.getPlayUrl());
//        if (!TextUtils.isEmpty(ColumnName)) {
//            mVideoInfo.setVideoWebChannel(ColumnName);
//        } else {
//            mVideoInfo.setVideoWebChannel("-");
//        }
//        Log.i(TAG, "---栏目分类名称:" + mVideoInfo.VideoWebChannel);
//        mVideoInfo.VideoTVChannel = "-";
//        // 央视网日志：设置视频加速对应的加速渠道厂商
//        mVideoInfo.Cdn = "-";
//        String AppVersionName = CNTVLogUtils.getVersionName(mContext);
//        String APPName = CNTVLogUtils.getProgramNameByPackageName(mContext,
//                mContext.getPackageName());
//        Log.i(TAG, "---包名：" + mContext.getPackageName());
//        Log.i(TAG, "------应用名称--------" + APPName);
//        mVideoInfo.extendProperty1 = APPName + "APP_Android";
//        mVideoInfo.extendProperty2 = APPName + "APP_Android_" + AppVersionName;
//        Log.i(TAG, "--" + mVideoInfo.extendProperty1
//                + "---" + mVideoInfo.extendProperty2);
//        boolean mIsWifi = CNTVLogUtils.isWifi(mContext);
//        if (mIsWifi) {
//            mVideoInfo.extendProperty3 = "WIFI";
//        } else {
//            boolean mIsEthernet = CNTVLogUtils.isEthernet(mContext.getApplicationContext());
//            if (mIsEthernet) {
//                mVideoInfo.extendProperty3 = "ETHERNET";
//            }
//        }
//        Log.i(TAG, "---网络状态:"
//                + mVideoInfo.extendProperty3);
//
//        // 央视网日志创建点播play对象
//        if (mVideoTracker != null) {
//            mVodPlay = mVideoTracker.newVodPlay(mVideoInfo, mIVodInfoProvider);
//        }
//
//        mVodMetaInfo = new VodMetaInfo();
//
//    }
}
