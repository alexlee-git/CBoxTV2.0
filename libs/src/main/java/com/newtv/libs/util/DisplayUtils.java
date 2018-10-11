package com.newtv.libs.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.newtv.libs.R;

/**
 * Created by caolonghe on 2018/3/7 0007.
 */

public class DisplayUtils {

    public final static int SCALE_TYPE_WIDTH = 0;
    public final static int SCALE_TYPE_HEIGHT = 1;
    private static float scaleWidth;
    private static float scaleHeight;

    public static void init(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        scaleWidth = (float) dm.widthPixels / 1920f;
        scaleHeight = (float) dm.heightPixels / 1080f;
}

    public static int translate(int px, int type) {
        switch (type) {
            case 0:
                return Math.round(px * scaleWidth);
            case 1:
                return Math.round(px * scaleHeight);
        }
        return px;
    }

    /**
     * px是经过适配后的尺寸，反算成标准尺寸（1920*1080）
     * @param px
     * @param type
     * @return
     */
    public static int reTranslate(int px, int type) {
        switch (type) {
            case 0:
                return Math.round(px / scaleWidth);
            case 1:
                return Math.round(px / scaleHeight);
        }
        return px;
    }

    /**
     * convert px to its equivalent dp
     * <p>
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("MM","width="+context.getResources().getDisplayMetrics().widthPixels);
        Log.e("MM","height="+context.getResources().getDisplayMetrics().heightPixels);
        Log.e("MM","density="+context.getResources().getDisplayMetrics().densityDpi);

        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     * <p>
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     * <p>
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * convert sp to its equivalent px
     * <p>
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     * <p>
     * 将sp转换为px
     */
    public static int getPxByDensity(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().density;
        Log.e("MM","spValue="+spValue );
        Log.e("MM","result="+spValue * fontScale);
        return (int) (spValue * fontScale);
    }



    public static void  adjustView(Context context, ImageView poster,View focusView){
        //适配

        int space = context.getResources().getDimensionPixelOffset(R.dimen.width_17dp);
        FrameLayout.LayoutParams posterPara = new FrameLayout.LayoutParams(poster.getLayoutParams());
        posterPara.setMargins(space,space,0,0);
        poster.setLayoutParams(posterPara);
        poster.requestLayout();


        ViewGroup.LayoutParams focusPara = focusView.getLayoutParams();
        focusPara.width = posterPara.width+2*space;
        focusPara.height = posterPara.height+2*space;
        focusView.setLayoutParams(focusPara);
        focusView.requestLayout();
    }
    public static void  adjustView(Context context, View poster,View focusView,int resouceWidthId,int resouceHeightId){
        //适配
        int spaceWidth = context.getResources().getDimensionPixelOffset(resouceWidthId);
        int spaceHeight = context.getResources().getDimensionPixelOffset(resouceHeightId);

        FrameLayout.LayoutParams posterPara = new FrameLayout.LayoutParams(poster.getLayoutParams());
        posterPara.setMargins(spaceWidth,spaceHeight,0,0);
        poster.setLayoutParams(posterPara);
        poster.requestLayout();


        ViewGroup.LayoutParams focusPara = focusView.getLayoutParams();
        focusPara.width = posterPara.width+2*spaceWidth;
        focusPara.height = posterPara.height+2*spaceHeight;

        focusView.setLayoutParams(focusPara);
        focusView.requestLayout();
    }
}
