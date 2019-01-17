package com.newtv.libs.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
     *
     * @return
     */
    public static boolean isFastWork() {
        long curTime = System.currentTimeMillis();
        long timeD = curTime - lastClickTime;
        if (0 < timeD && timeD < 200) {
            return true;
        }
        lastClickTime = curTime;
        return false;
    }

    public static void initScreen(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        Point mPoint = new Point();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(metric);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.getDefaultDisplay().getRealSize(mPoint);
                screenW = mPoint.x;
                screenH = mPoint.y;
            }else {
                screenW = metric.widthPixels;
                screenH = metric.heightPixels;
            }
        } else {
            metric = context.getResources().getDisplayMetrics();
            screenW = metric.widthPixels;
            screenH = metric.heightPixels;
        }
        screenDensity = metric.density;
        LogUtils.d("ScreenUtils", "width=" + screenW + " height=" + screenH + " density=" +
                screenDensity);
    }

    private static void checkInit(int size) {
        if (size <= 0) {
            initScreen(Libs.get().getContext());
        }
    }

    public static int getScreenW() {
        checkInit(screenW);
        return screenW;
    }

    public static int getScreenH() {
        checkInit(screenH);
        if (screenH == 1024) {
            screenH = 1080;
        }
        return screenH;
    }

    public static float getScreenDensity() {
        return screenDensity;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * getScreenDensity() + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }
}
