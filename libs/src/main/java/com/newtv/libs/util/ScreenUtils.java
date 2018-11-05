package com.newtv.libs.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.newtv.libs.Libs;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         15:19
 * 创建人:           weihaichao
 * 创建日期:          2018/3/30
 */

public class ScreenUtils {
    private static int screenW;
    private static int screenH;
    private static float screenDensity;

    private static long lastClickTime;
    /**
     * 是否快速点击
     * @return
     */
    public static boolean isFastWork() {
        long curTime = System.currentTimeMillis();
        long timeD = curTime - lastClickTime;
        if ( 0 < timeD && timeD < 200) {
            return true;
        }
        lastClickTime = curTime;
        return false;
    }

    public static void initScreen(Context context){
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        screenDensity = metric.density;
    }

    private static void checkInit(int size){
        if(size <= 0){
            initScreen(Libs.get().getContext());
        }
    }

    public static int getScreenW(){
        checkInit(screenW);
        return screenW;
    }

    public static int getScreenH() {
        checkInit(screenH);
        if (screenH == 1024){
            screenH = 1080;
        }
        return screenH;
    }

    public static float getScreenDensity(){
        return screenDensity;
    }

    /** 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * getScreenDensity() + 0.5f);
    }

    /** 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }
}
