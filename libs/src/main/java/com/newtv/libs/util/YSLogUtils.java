package com.newtv.libs.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gridsum.videotracker.VideoTracker;
import com.gridsum.videotracker.entity.LiveMetaInfo;
import com.gridsum.videotracker.entity.VideoInfo;
import com.gridsum.videotracker.entity.VodMetaInfo;
import com.gridsum.videotracker.play.LivePlay;
import com.gridsum.videotracker.play.VodPlay;
import com.gridsum.videotracker.provider.ILiveInfoProvider;
import com.gridsum.videotracker.provider.IVodInfoProvider;

import java.math.BigDecimal;

import tv.icntv.icntvplayersdk.IcntvPlayer;
/**
 * Created by Administrator on 2018/6/6.
 */

public class YSLogUtils {


    public final static int YS_VOD_LOG = 0;//点播
    public final static int YS_LIVE_LOG = 1;//直播播
    private static YSLogUtils ysLogUtils;
    public IVodInfoProvider mIVodInfoProvider;
    private VideoTracker mVideoTracker;
    private Context mContext;
    private String outSourceId, videoName, ColumnName, outSourcePlayUrl;
    private VodPlay mVodPlay;
    private VideoInfo mVideoInfo;
    private VodMetaInfo mVodMetaInfo;
    private String TAG = "ysLogUtils";

    //直播
    private LiveMetaInfo mLiveMetaInfo;
    private LivePlay mLivePlay;
    private ILiveInfoProvider mILiveInfoProvider;

    private IcntvPlayer mIcntvPlayer;

    private YSLogUtils(Context context) {
        this.mContext = context;
    }

    public static YSLogUtils getInstance(Context context) {
        if (ysLogUtils == null) {
            synchronized (YSLogUtils.class) {
                if (ysLogUtils == null) ysLogUtils = new YSLogUtils(context);
            }
        }

        return ysLogUtils;
    }

    public void release() {

        Log.e("slp", "release YSLogUtil");

        clearData();

        mVideoTracker = null;
        mIVodInfoProvider = null;

        mVodPlay = null;
        mVideoInfo = null;
        mVodMetaInfo = null;

        mLiveMetaInfo = null;
        mLivePlay = null;
        mILiveInfoProvider = null;

        mIcntvPlayer = null;
    }

    public void initTracker() {
        // 央视网日志：初始化Tracker对象
        mVideoTracker = VideoTracker.getInstance("GVD-200099", "GSD-200098",
                mContext.getApplicationContext());
    }

    // 初始化央视网日志对象
    public void initCNTVVideoTracker(int videoType) {
        // TODO Auto-generated method stub
        Log.e("slp", "initCNTVVideoTracker");


        // 央视网日志：创建央视网日志videoInfo，"playID001"为视频的ID
        if (!TextUtils.isEmpty(outSourceId)) {
            mVideoInfo = new VideoInfo(outSourceId);

        } else {
            mVideoInfo = new VideoInfo("-");
        }

        LogUtils.i(TAG, "---VideoID：" + mVideoInfo.VideoID);
        mVideoInfo.VideoOriginalName = "-";
        if (!"".equals(videoName)) {
            mVideoInfo.VideoName = videoName;
        } else {
            mVideoInfo.VideoName = "-";
        }
        LogUtils.i(TAG, "---videoName:" + videoName);
        if (!TextUtils.isEmpty(ColumnName)) {
            mVideoInfo.setVideoWebChannel(ColumnName);
        } else {
            mVideoInfo.setVideoWebChannel("-");
        }
        LogUtils.i(TAG, "---栏目分类名称:" + mVideoInfo.VideoWebChannel);
        mVideoInfo.VideoTVChannel = "-";
        // 央视网日志：设置视频加速对应的加速渠道厂商
        mVideoInfo.Cdn = "-";
        String AppVersionName = CNTVLogUtils.getVersionName(mContext);
        String APPName = CNTVLogUtils.getProgramNameByPackageName(mContext,
                mContext.getPackageName());
        LogUtils.i(TAG, "---包名：" + mContext.getPackageName());
        LogUtils.i(TAG, "------应用名称--------" + APPName);
        mVideoInfo.extendProperty1 = APPName + "APP_Android";
        mVideoInfo.extendProperty2 = APPName + "APP_Android_" + AppVersionName;
        LogUtils.i(TAG, "--" + mVideoInfo.extendProperty1
                + "---" + mVideoInfo.extendProperty2);
        boolean mIsWifi = CNTVLogUtils.isWifi(mContext);
        if (mIsWifi) {
            mVideoInfo.extendProperty3 = "WIFI";
        } else {
            boolean mIsEthernet = CNTVLogUtils.isEthernet(mContext);
            if (mIsEthernet) {
                mVideoInfo.extendProperty3 = "ETHERNET";
            }
        }
        LogUtils.i(TAG, "---网络状态:"
                + mVideoInfo.extendProperty3);

        switch (videoType) {
            case YS_VOD_LOG://点播

                mVodMetaInfo = new VodMetaInfo();
                mIVodInfoProvider = new IVodInfoProvider() {

                    @Override
                    public double getFramesPerSecond() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public double getBitrate() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public double getPosition() {
                        // TODO Auto-generated method stub
                        if (mIcntvPlayer != null) {
                            float position = mIcntvPlayer.getCurrentPosition() / 1000;
                            BigDecimal bd1 = BigDecimal.valueOf(position);
                            bd1.setScale(2, BigDecimal.ROUND_HALF_UP);
                            double f1 = bd1.doubleValue();
                            return f1;
                        }
                        return 0;
                    }
                };
                if (mVideoTracker != null) {
                    if (mVodPlay == null) {
                        mVodPlay = mVideoTracker.newVodPlay(mVideoInfo, mIVodInfoProvider);
                    }
                }
                break;

            case YS_LIVE_LOG://直播

                mLiveMetaInfo = new LiveMetaInfo();
                mILiveInfoProvider = new ILiveInfoProvider() {
                    @Override
                    public double getFramesPerSecond() {
                        return 0;
                    }

                    @Override
                    public double getBitrate() {
                        return 0;
                    }
                };
                if (mVideoTracker != null) {
                    mLivePlay = mVideoTracker.newLivePlay(mVideoInfo, mILiveInfoProvider);
                }
                break;
        }

    }

    public String getOutSourceId() {
        return outSourceId;
    }

    public void setOutSourceId(String outSourceId) {
        this.outSourceId = outSourceId;
    }


    public IcntvPlayer getmIcntvPlayer() {
        return mIcntvPlayer;
    }

    public void setmIcntvPlayer(IcntvPlayer mIcntvPlayer) {

        this.mIcntvPlayer = mIcntvPlayer;

    }

    public VodPlay getmVodPlay() {
        return mVodPlay;
    }

    public void setmVodPlay(VodPlay mVodPlay) {
        this.mVodPlay = mVodPlay;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getOutSourcePlayUrl() {
        return outSourcePlayUrl;
    }

    public void setOutSourcePlayUrl(String outSourcePlayUrl) {
        this.outSourcePlayUrl = outSourcePlayUrl;
    }

    public VodMetaInfo getmVodMetaInfo() {
        return mVodMetaInfo;
    }

    public void setmVodMetaInfo(VodMetaInfo mVodMetaInfo) {
        this.mVodMetaInfo = mVodMetaInfo;
    }


    public LiveMetaInfo getmLiveMetaInfo() {
        return mLiveMetaInfo;
    }

    public void setmLiveMetaInfo(LiveMetaInfo mLiveMetaInfo) {
        this.mLiveMetaInfo = mLiveMetaInfo;
    }

    public LivePlay getmLivePlay() {
        return mLivePlay;
    }

    public void setmLivePlay(LivePlay mLivePlay) {
        this.mLivePlay = mLivePlay;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public void setColumnName(String columnName) {
        ColumnName = columnName;
    }

    public void clearData() {
        Log.e("slp", "CNTVVideoTracker clearData");

        mIVodInfoProvider = null;
        mILiveInfoProvider = null;
        mVodPlay = null;
        mVodMetaInfo = null;
    }
}
