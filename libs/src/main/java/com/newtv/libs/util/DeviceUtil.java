package com.newtv.libs.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.letv.LetvDeviceUtil;
import com.newtv.libs.BuildConfig;


/**
 * Created by cuiwj on 2018/5/16.
 */

public class DeviceUtil {
    private static final String XIAO_MI = "xiaomi";
    private static final String LETV = "letv";
    public static final String XIONG_MAO = "panda";
    public static final String XUN_MA = "xunma";
    public static final String XSJ = "xsj";
    public static final String CBOXTEST = "cboxtest";
    public static final String FEILIERDE = "feilierde";
    public static final String AILANG = "ailang"; //爱浪
    public static final String XUNMATOUYINGYI = "xunmatouyingyi"; //迅码投影仪
    public static final String VENDORTEST = "vendortest"; //厂家测试
    public static final String YSTEN_VOICE = "ysten_voice"; //易视腾
    @SuppressWarnings("ConstantConditions")
    public static boolean isSelfDevice() {
        if (BuildConfig.DEBUG) {
            return true;
        }

        if (BuildConfig.FLAVOR.equals(XIAO_MI)) {
            String brand = Build.BRAND;
            Log.i("device", brand);
            return !TextUtils.isEmpty(brand) && XIAO_MI.equalsIgnoreCase(brand);
        } else if (BuildConfig.FLAVOR.equals(LETV)) {
            LogUtils.i("device", "letv=" + LetvDeviceUtil.isLetvDevice());
            return LetvDeviceUtil.isLetvDevice();
        } else if (BuildConfig.FLAVOR.equals(XIONG_MAO)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(XUN_MA)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(XSJ)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(CBOXTEST)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(FEILIERDE)) {
            String fled = "pled-3229-newtv";
            String fModel = Build.MODEL;
            Log.i("device", fModel);
            return !TextUtils.isEmpty(fModel) && fled.equalsIgnoreCase(fModel);
        } else if (BuildConfig.FLAVOR.equals(AILANG)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(XUNMATOUYINGYI)) {
            String fvid = "unknown";
            String vid = SystemPropertiesProxy.getProperty("hw.yunos.vendorID", "");
            if (!TextUtils.isEmpty(vid)&&!fvid.equalsIgnoreCase(vid)){
                return true;
            }
        } else if (BuildConfig.FLAVOR.equals(VENDORTEST)) {
            return true;
        } else if (BuildConfig.FLAVOR.equals(YSTEN_VOICE)) {
            String fvid = "ysten";
            String vid = SystemPropertiesProxy.getProperty("ro.ftserialno", "");
            if (!TextUtils.isEmpty(vid)&&fvid.equalsIgnoreCase(vid)){
                return true;
            }
        }
        return false;
    }

    public static String getAppVersion(Context context) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

        return localVersion;
    }
}
