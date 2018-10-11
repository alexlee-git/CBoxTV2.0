package com.newtv.libs.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         14:58
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class ScaleUtils {

    private static ScaleUtils scaleUtils = new ScaleUtils();
    private Interpolator mSpringInterpolator;

    public static ScaleUtils getInstance() {
        return scaleUtils;
    }

    private ScaleUtils() {
        mSpringInterpolator = new OvershootInterpolator(2.2f);
    }

    public void onItemGetFocus(View view) {
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    //用于赛程表的
    public void onItemGetFocus(View view, float value) {
        //直接放大schedule  view
        ScaleAnimation sa = new ScaleAnimation(1.0f, value, 1.0f, value, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }


    public void onItemGetFocus(View view, View focusView) {
        if (focusView != null) {
            focusView.setVisibility(View.VISIBLE);
        }
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    //用于赛程表的
    public void onItemLoseFocus(View view, float value) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(value, 1.0f, value, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    public void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    public void onItemLoseFocus(View view, View focusView) {

        if (focusView != null) {
            focusView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }
}
