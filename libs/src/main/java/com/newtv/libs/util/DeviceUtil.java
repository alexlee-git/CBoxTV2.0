package com.newtv.libs.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.letv.LetvDeviceUtil;
import com.newtv.libs.Libs;


/**
 * Created by cuiwj on 2018/5/16.
 */

public class DeviceUtil {
    private static final String XIAO_MI = "xiaomi";
    private static final String XIAO_MI_STAGE = "xiaomi_stage";
    public static final String LETV = "letv";
    private static final String LETV_STAGE ="letv_stage";
    public static final String XIONG_MAO = "panda";
    public static final String XUN_MA = "xunma";
    public static final String XSJ = "xsj";
    public static final String CBOXTEST = "cboxtest";
    public static final String FEILIERDE = "feilierde";
    public static final String AILANG = "ailang"; //爱浪
    public static final String XUNMATOUYINGYI = "xunmatouyingyi"; //迅码投影仪
    public static final String VENDORTEST = "vendortest"; //厂家测试
    public static final String YSTEN_VOICE = "ysten_voice"; //易视腾
    public static final String CHUANGWEI = "coocaa"; //创维
    public static final String HAIER = "haier"; //海尔
    public static final String KANGJIA = "konka"; //康佳
    public static final String PHILIPS = "philips"; //飞利浦
    public static final String CHANGHONG = "changhong"; //长虹
    @SuppressWarnings("ConstantConditions")
    public static boolean isSelfDevice() {
        if (Libs.get().isDebug()) {
            return true;
        }

        if (Libs.get().getFlavor().equals(XIAO_MI) || Libs.get().getFlavor().equals(XIAO_MI_STAGE)) {
            String brand = Build.BRAND;
            Log.i("device", brand);
            return !TextUtils.isEmpty(brand) && XIAO_MI.equalsIgnoreCase(brand);
        } else if (LETV.equals(Libs.get().getFlavor()) || LETV_STAGE.equals(Libs.get().getFlavor())) {
            LogUtils.i("device", "letv=" + LetvDeviceUtil.isLetvDevice());
            return LetvDeviceUtil.isLetvDevice();
        } else if (Libs.get().getFlavor().equals(XIONG_MAO)) {
            return true;
        } else if (Libs.get().getFlavor().equals(XUN_MA)) {
            return true;
        } else if (Libs.get().getFlavor().equals(XSJ)) {
            return true;
        } else if (Libs.get().getFlavor().equals(CBOXTEST)) {
            return true;
        } else if (Libs.get().getFlavor().equals(FEILIERDE)) {
            String fled = "pled-3229-newtv";
            String fModel = Build.MODEL;
            Log.i("device", fModel);
            return !TextUtils.isEmpty(fModel) && fled.equalsIgnoreCase(fModel);
        } else if (Libs.get().getFlavor().equals(AILANG)) {
            return true;
        } else if (Libs.get().getFlavor().equals(XUNMATOUYINGYI)) {
            String fvid = "unknown";
            String vid = SystemPropertiesProxy.getProperty("hw.yunos.vendorID", "");
            if (!TextUtils.isEmpty(vid)&&!fvid.equalsIgnoreCase(vid)){
                return true;
            }
        } else if (Libs.get().getFlavor().equals(VENDORTEST)) {
            return true;
        } else if (Libs.get().getFlavor().equals(YSTEN_VOICE)) {
            String fvid = "ysten";
            String vid = SystemPropertiesProxy.getProperty("ro.ftserialno", "");
            if (!TextUtils.isEmpty(vid)&&fvid.equalsIgnoreCase(vid)){
                return true;
            }
        } else if (Libs.get().getFlavor().equals(CHUANGWEI)) {
            return true;
        } else if (Libs.get().getFlavor().equals(HAIER)) {
            return true;
        } else if (Libs.get().getFlavor().equals(KANGJIA)) {
            return true;
        } else if (Libs.get().getFlavor().equals(PHILIPS)) {
            return true;
        } else if (Libs.get().getFlavor().equals(CHANGHONG)) {
            return true;
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
