package com.newtv.libs.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

    public void onItemGetFocus(final View view) {
        //直接放大view
//        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.bringToFront();
//        view.startAnimation(sa);

        if(view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0){
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemGetFocus(view);
                }
            },300);
            return;
        }

        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectXAnimator = ObjectAnimator.ofFloat(view,"scaleX",1.0f,1.1f);
        ObjectAnimator objectYAnimator = ObjectAnimator.ofFloat(view,"scaleY",1.0f,1.1f);
        animatorSet.playTogether(objectXAnimator,objectYAnimator);
        animatorSet.start();
    }

    //用于赛程表的
    public void onItemGetFocus(View view, float value) {
        //直接放大schedule  view
//        ScaleAnimation sa = new ScaleAnimation(1.0f, value, 1.0f, value, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.bringToFront();
//        view.startAnimation(sa);
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, value);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, value);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();


    }


    public void onItemGetFocus(View view, View focusView) {
        if (focusView != null) {
            focusView.setVisibility(View.VISIBLE);
        }
        //直接放大view
//        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.bringToFront();
//        view.startAnimation(sa);
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }

    public void onItemGetFocus(View view,long duration,float values){
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, values);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, values);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }

    //用于赛程表的
    public void onItemLoseFocus(View view, float value) {
        // 直接缩小view
//        ScaleAnimation sa = new ScaleAnimation(value, 1.0f, value, 1.0f, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.startAnimation(sa);
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", value, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", value, 1.0f);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }

    public void onItemLoseFocus(View view) {
        // 直接缩小view
//        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.startAnimation(sa);

        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectXAnimator = ObjectAnimator.ofFloat(view,"scaleX",1.1f,1.0f);
        ObjectAnimator objectYAnimator = ObjectAnimator.ofFloat(view,"scaleY",1.1f,1.0f);
        animatorSet.playTogether(objectXAnimator,objectYAnimator);
        animatorSet.start();
    }

    public void onItemLoseFocus(View view, View focusView) {

        if (focusView != null) {
            focusView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
//        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
//                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        sa.setFillAfter(true);
//        sa.setDuration(400);
//        sa.setInterpolator(mSpringInterpolator);
//        view.startAnimation(sa);
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1.0f);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }

    public void onItemLoseFocus(View view,long duration,float values){
        view.setPivotX(view.getMeasuredWidth()/2);
        view.setPivotY(view.getMeasuredHeight()/2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", values, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", values, 1.0f);
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.start();
    }
}
