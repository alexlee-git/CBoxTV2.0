package tv.newtv.cboxtv.player.newtv;

import android.content.Context;
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
import tv.icntv.icntvplayersdk.IcntvLive;
import tv.icntv.icntvplayersdk.iICntvPlayInterface;
import tv.newtv.cboxtv.player.ILiveVideoPlayerInterface;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.PlayerUrlConfig;
import tv.newtv.cboxtv.player.iPlayCallBackEvent;
import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by wangkun on 2018/1/15.
 */

public class NewTVLiveVideoPlayer implements ILiveVideoPlayerInterface {
    private static final String TAG = "NewTVLiveVideoPlayer";
    private static NewTVLiveVideoPlayer mNewTVLiveVideoPlayer;
    private iPlayCallBackEvent mIPlayCallBackEvent;
    private IcntvLive mIcntvLive;
    private Context mContext;
    private boolean isSuccessful = false;
    private boolean mIsLivePositiveOnPrepared = false;//直播
    private boolean mIsLiveInit = false;//是否已经初始化
    private String outSourceId;
    private String liveName;
    private String liveUrl;
    private iICntvPlayInterface mIcntvPlayerCallback = new iICntvPlayInterface() {
        @Override
        public void onPrepared(LinkedHashMap<String, String> linkedHashMap) {
            Log.i(TAG, "onPrepared: ");
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onPrepared(linkedHashMap);
            }

            PlayerConfig.getInstance().setJumpAD(true);
            //央视网日志
            isSuccessful = true;
            if (YSLogUtils.getInstance(mContext).getmLivePlay() != null && YSLogUtils.getInstance
                    (mContext).getmLiveMetaInfo() != null) {
                // 央视网日志：结束加载
                YSLogUtils.getInstance(mContext).getmLivePlay().endPreparing(isSuccessful,
                        YSLogUtils.getInstance(mContext).getmLiveMetaInfo());
                YSLogUtils.getInstance(mContext).getmLivePlay().onStateChanged(GSVideoState
                        .PLAYING);
                Log.i(TAG, "---结束加载开始播放正片");
            }

        }

        @Override
        public void onCompletion(int type) {
            Log.i(TAG, "onCompletion: " + type);
            if(type ==  iICntvPlayInterface.VIDEO_COMPLETE_TYPE ||
                    type == iICntvPlayInterface.AFTER_AD_COMPLETE_TYPE) {
                if (mIPlayCallBackEvent != null) {
                    mIPlayCallBackEvent.onCompletion();
                }

                PlayerConfig.getInstance().setJumpAD(false);

                //slp
                if (YSLogUtils.getInstance(mContext).getmLivePlay() != null &&
                        mIsLivePositiveOnPrepared) {

                    // 央视网日志：
                    YSLogUtils.getInstance(mContext).getmLivePlay().onStateChanged(GSVideoState
                            .STOPPED); // 视频播放完成时调用
                    YSLogUtils.getInstance(mContext).getmLivePlay().endPlay();
                    Log.i(TAG,
                            "---mVodPlay.onStateChanged(GSVideoState.STOPPED)");
                    Log.i(TAG, "---结束播放endPlay()");
                }
            }

        }

        @Override
        public void onBufferStart(String s) {
            Log.i(TAG, "onBufferStart: ");
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onVideoBufferStart(s);
            }

            if (s.equals("VideoStartBuffer")) {
                mIsLivePositiveOnPrepared = true;//直播 = true;
                Log.i(TAG, "---正片播放的状态:mIsPositiveOnPrepared = true");
                // 正片播放后，央视网日志初始化
                if (mIcntvLive != null) {
                    // 初始化央视网日志SDK，开始上传信息
                    if (!outSourceId.equals(PlayerUrlConfig.getInstance().getPlayingContentId())) {
                        YSLogUtils.getInstance(mContext).setmIcntvPlayer(null);
                        YSLogUtils.getInstance(mContext).initCNTVVideoTracker(YSLogUtils
                                .YS_LIVE_LOG);
                        mIsLiveInit = true;
                        // 央视网日志：视频开始加载
                        if (YSLogUtils.getInstance(mContext).getmLivePlay() != null) {
                            PlayerUrlConfig.getInstance().setPlayUrl(liveUrl);//记录当前播放的URL和id
                            PlayerUrlConfig.getInstance().setPlayingContentId(outSourceId);//节目id

                            YSLogUtils.getInstance(mContext).getmLivePlay().beginPreparing();
                            Log.i(TAG, "----onBufferStart:视频开始加载");
                        }
                    }

                }

            }

            if (s.equals(ON_BUFFER_START_TYPE_701_STATUS)
                    && mIcntvLive != null) {
                if (YSLogUtils.getInstance(mContext).getmLivePlay() != null &&
                        mIsLivePositiveOnPrepared) {
                    // 央视网日志：卡顿时调用
                    YSLogUtils.getInstance(mContext).getmLivePlay().onStateChanged(GSVideoState
                            .BUFFERING); // 卡顿时调用
                    Log.i(TAG,
                            "----onBufferStart:mLivePlay.onStateChanged(GSVideoState.BUFFERING)");
                }
            }
        }

        @Override
        public void onBufferEnd(String s) {
            Log.i(TAG, "onBufferEnd: ");
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onVideoBufferEnd(s);
            }

            if (s.equals(ON_BUFFER_END_TYPE_702_STATUS)
                    && mIcntvLive != null) {
                if (YSLogUtils.getInstance(mContext).getmLivePlay() != null &&
                        mIsLivePositiveOnPrepared) {
                    // 央视网日志：播放时调用
                    YSLogUtils.getInstance(mContext).getmLivePlay().onStateChanged(GSVideoState
                            .PLAYING); // 播放时调用
                    Log.i(TAG,
                            "---onBufferEnd:mLivePlay.onStateChanged(GSVideoState.PLAYING)");
                }
            }
        }

        @Override
        public void onError(int i, int i1, String s) {
            Log.i(TAG, "onError: ");
            if (mIPlayCallBackEvent != null) {
                mIPlayCallBackEvent.onError(i, i1, s);
            }
        }

        @Override
        public void onTimeout(int i) {
            Log.i(TAG, "onTimeout: " + i);
            if(mIPlayCallBackEvent!=null){
                mIPlayCallBackEvent.onTimeout(i);
            }
        }
    };

    private NewTVLiveVideoPlayer(Context context) {
        this.mContext = context;

    }

    public static NewTVLiveVideoPlayer getInstance(Context context) {
        if (mNewTVLiveVideoPlayer == null) {
            synchronized (NewTVLiveVideoPlayer.class) {
                if (mNewTVLiveVideoPlayer == null) {
                    mNewTVLiveVideoPlayer = new NewTVLiveVideoPlayer(context);
                }
            }
        }
        return mNewTVLiveVideoPlayer;
    }

    @Override
    public boolean playVideo(Context context, FrameLayout frameLayout, iPlayCallBackEvent
            callBack, VideoDataStruct videoDataStruct) {
        Log.i(TAG, "playVideo: ");
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
        IcntvPlayerInfo icntvPlayerInfo = new IcntvPlayerInfo();
        icntvPlayerInfo.setAppKey(Libs.get().getAppKey());
        icntvPlayerInfo.setChannalId(Libs.get().getChannelId());
        icntvPlayerInfo.setCdnDispatchUrl(Constant.BASE_URL_CDN);
        icntvPlayerInfo.setDynamicKeyUrl(Constant.DYNAMIC_KEY);
        icntvPlayerInfo.setPlayUrl(videoDataStruct.getPlayUrl());
        Log.i(TAG, "playVideo: videoDataStruct playUrl=" + videoDataStruct.getPlayUrl());
        icntvPlayerInfo.setProgramListID(videoDataStruct.getContentUUID());

        icntvPlayerInfo.setDuration(videoDataStruct.getDuration());
        icntvPlayerInfo.setProgramID(videoDataStruct.getProgramId());
        icntvPlayerInfo.setKey(videoDataStruct.getKey());
        icntvPlayerInfo.setAdModel(PlayerConfig.getInstance().getJumpAD());
        icntvPlayerInfo.setDeviceID(Constant.UUID);
        icntvPlayerInfo.setExtend(Utils.buildExtendString(PlayerConfig.getInstance().getColumnId
                (), PlayerConfig.getInstance().getSecondColumnId(), PlayerConfig.getInstance()
                .getFirstChannelId(), PlayerConfig.getInstance().getSecondChannelId(), PlayerConfig
                .getInstance().getTopicId()));
        mIcntvLive = new IcntvLive(context, frameLayout, icntvPlayerInfo, mIcntvPlayerCallback);


        outSourceId = videoDataStruct.getContentUUID();

        liveName = videoDataStruct.getTitle();
        liveUrl = videoDataStruct.getPlayUrl();

        // 央视网日志：令正片开始播放的状态为false
        mIsLivePositiveOnPrepared = false;
        // 央视网日志：保存央视网所需要的信息

        YSLogUtils.getInstance(mContext).setOutSourceId(videoDataStruct.getContentUUID());//直播id
        YSLogUtils.getInstance(mContext).setVideoName(videoDataStruct.getTitle());
        YSLogUtils.getInstance(mContext).setOutSourcePlayUrl(videoDataStruct.getPlayUrl());
        return true;
    }

    @Override
    public boolean isADPlaying() {
        Log.i(TAG, "isADPlaying: ");
        if (mIcntvLive != null) {
            return mIcntvLive.isADPlaying();
        }
        return false;
    }

    @Override
    public void setDataSource(String definition) {
        Log.i(TAG, "setDataSource: ");
        if (mIcntvLive != null) {
            mIcntvLive.setDataSource(definition);
        }
    }

    @Override
    public void releaseVideo() {
        Log.i(TAG, "releaseVideo------------->start!");
        if (mIcntvLive != null) {
            try {
                mIcntvLive.release();
                mIcntvLive = null;
                // mNewTVLiveVideoPlayer = null;
            } catch (Exception e) {
                LogUtils.e(e.toString());
            } finally {
                mIcntvLive = null;
            }

            if (YSLogUtils.getInstance(mContext).getmLivePlay() != null &&
                    mIsLivePositiveOnPrepared) {
                // 央视网日志：

                if (!PlayerUrlConfig.getInstance().isFromDetailPage()) {

                    YSLogUtils.getInstance(mContext).getmLivePlay().onStateChanged(GSVideoState
                            .STOPPED); // 视频播放完成时调用
                    YSLogUtils.getInstance(mContext).getmLivePlay().endPlay();
                }
                Log.i(TAG,
                        "---releaseVideo:mLivePlay.onStateChanged(GSVideoState.STOPPED)");
                Log.i(TAG, "---releaseVideo:结束播放endPlay()");
            }
            YSLogUtils.getInstance(mContext).release();


        }

        PlayerConfig.getInstance().setJumpAD(false);
        mNewTVLiveVideoPlayer = null;
        mIcntvPlayerCallback = null;
        mIPlayCallBackEvent = null;
    }

    @Override
    public void setVideoSilent(boolean isSilent) {
        Log.i(TAG, "setVideoSilent:" + isSilent);
        if (mIcntvLive != null) {
            mIcntvLive.setVideoSilent(isSilent);
        }
    }
}
