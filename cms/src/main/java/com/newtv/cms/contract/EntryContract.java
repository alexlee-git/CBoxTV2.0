package com.newtv.cms.contract;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gridsum.tracker.GridsumWebDissector;
import com.gridsum.videotracker.VideoTracker;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IBootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.CNTVLogUtils;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.SPrefUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         13:26
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class EntryContract {
    public interface View extends ICmsView {
        void bootGuildResult();
    }

    public interface Presenter extends ICmsPresenter {
        void initCNTVLog(Application application);
    }

    public static class EntryPresenter extends CmsServicePresenter<View> implements Presenter {

        private static final String TAG = "EntryPresenter";

        public EntryPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
            getBootGuide();
        }

        // 央视网日志初始化
        @Override
        public void initCNTVLog(Application application) {
            // TODO Auto-generated method stub
            // 央视网日志初始化
            String urls[] = {"http://wdrecv.app.cntvwb.cn/gs.gif"};
            GridsumWebDissector.getInstance().setUrls(urls);
            GridsumWebDissector.getInstance().setApplication(application);
            Log.i(TAG, "---入口activity" + application);
            String AppVersionName = CNTVLogUtils.getVersionName(application.getApplicationContext
                    ());
            Log.i(TAG, "---版本号" + AppVersionName);
            Log.i(TAG, "---渠道号" + Libs.get().getChannelId());
            GridsumWebDissector.getInstance().setAppVersion(AppVersionName);// 设置App版本号
            GridsumWebDissector.getInstance().setServiceId("GWD-005100");// 设置统计服务ID
            GridsumWebDissector.getInstance().setChannel(Libs.get().getChannelId());//
            // 设置来源渠道（不适用于多渠道打包）
            // 央视网日志： （传入设备型号，如：MI 2S）
            VideoTracker.setMfrs(android.os.Build.MODEL);
            // 央视网日志：（传入播放平台，如：Android）
            VideoTracker.setDevice("Android");
            // 央视网日志：（传入操作系统，如：Android_4.4.4）
            VideoTracker.setChip(android.os.Build.VERSION.RELEASE);
        }

        @SuppressWarnings("ConstantConditions")
        void getBootGuide() {
            if (!DeviceUtil.CBOXTEST.equals(Libs.get().getFlavor())) {
                IBootGuide bootGuide = getService(SERVICE_BOOT_GUIDE);
                if (bootGuide != null) {
                    String platform = Libs.get().getAppKey() + Libs.get().getChannelId();
                    bootGuide.getBootGuide(platform, new DataObserver<String>() {
                        @Override
                        public void onResult(String result) {
                            String cacheValue = (String) SPrefUtils.getValue(getContext(),
                                    SPrefUtils.KEY_SERVER_ADDRESS, "");
                            if (!TextUtils.equals(cacheValue, result)) {
                                SPrefUtils.setValue(getContext(), SPrefUtils
                                        .KEY_SERVER_ADDRESS, result);
                                Constant.parseServerAddress(result);
                            }
                            getView().bootGuildResult();
                        }

                        @Override
                        public void onError(@Nullable String desc) {
                        }
                    });
                }
            } else {
                getView().bootGuildResult();
            }
        }
    }
}
