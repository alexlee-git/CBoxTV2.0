package com.newtv.cms.contract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Build;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IClock;
import com.newtv.cms.api.INav;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Nav;
import com.newtv.cms.bean.Time;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         10:25
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class AppMainContract {
    public interface View extends ICmsView {
        void syncServerTime(Time result);
    }

    public interface Presenter extends ICmsPresenter {
        void syncServiceTime();
    }

    public static class MainPresenter extends CmsServicePresenter<View> implements Presenter {

        //广播显示系统时间
        private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    syncServiceTime();
                }
            }
        };

        public MainPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
            initLogUpload(context);
            syncServiceTime();
            registTimeSync(context);
        }

        @Override
        public void destroy() {
            super.destroy();

            if (mTimeRefreshReceiver != null) {
                getContext().unregisterReceiver(mTimeRefreshReceiver);
            }
        }

        @Override
        public void syncServiceTime() {
            IClock clock = getService(SERVICE_CLOCK);
            if (clock != null) {
                clock.sync(new DataObserver<Time>() {
                    @Override
                    public void onResult(Time result) {
                        if ("1".equals(result.getStatusCode())) {
                            getView().syncServerTime(result);
                        } else {
                            getView().syncServerTime(null);
                        }
                    }

                    @Override
                    public void onError(@Nullable String desc) {
                        getView().syncServerTime(null);
                    }
                });
            }
        }

        private void registTimeSync(Context context) {
            context.registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent
                    .ACTION_TIME_TICK));
        }

        private void initLogUpload(Context context) {
            try {
                StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
                PackageInfo pckInfo = context.getPackageManager().getPackageInfo(context
                        .getPackageName(), 0);

                dataBuff.append("0,")
                        .append(pckInfo.versionName)
                        .trimToSize();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, dataBuff.toString());//进入应用


                dataBuff.delete(0, dataBuff.length());
                dataBuff.append(Build.MANUFACTURER)
                        .append(",")
                        .append(Build.MODEL)
                        .append(",")
                        .append(Build.VERSION.RELEASE)
                        .trimToSize(); // 设备信息
                LogUploadUtils.uploadLog(Constant.LOG_NODE_DEVICE_INFO, dataBuff.toString());

                dataBuff.delete(0, dataBuff.length());

                dataBuff.append(pckInfo.applicationInfo.loadLabel(context.getPackageManager()))
                        .append(",")
                        .append(pckInfo.versionName)
                        .append(",")
                        .trimToSize(); // 版本信息

                LogUploadUtils.uploadLog(Constant.LOG_NODE_APP_VERSION, dataBuff.toString());
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
    }
}
