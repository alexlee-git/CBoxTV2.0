package com.newtv.libs.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.newtv.libs.R;

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

    private ScaleUtils() {
        mSpringInterpolator = new OvershootInterpolator(2.2f);
    }

    public static ScaleUtils getInstance() {
        return scaleUtils;
    }

    public void onItemGetFocus(final View view) {
        //直接放大view
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id, "animation");
            return;
        }

        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f);
        ObjectAnimator objectYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f);
        animatorSet.playTogether(objectXAnimator, objectYAnimator);
        view.setTag(R.id.tag_focus_id, "animator");
        animatorSet.start();
    }

    //用于赛程表的
    public void onItemGetFocus(final View view, final float value) {
        if (view == null || value == 0) return;
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            ScaleAnimation sa = new ScaleAnimation(1.0f, value, 1.0f, value, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id, "animation");
            return;
        }

        //直接放大schedule  view
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, value);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, value);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id, "animator");


    }


    public void onItemGetFocus(final View view, final View focusView) {
        if (view == null || focusView == null) return;
        focusView.setVisibility(View.VISIBLE);
        if (focusView.getMeasuredWidth() == 0 || focusView.getMeasuredHeight() == 0) {
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id, "animation");
            return;
        }
        //直接放大view
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id, "animator");
    }

    public void onItemGetFocus(final View view, final long duration, final float values) {

        if (view == null || values == 0 || duration == 0) return;
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            ScaleAnimation sa = new ScaleAnimation(1.0f, values, 1.0f, values, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(duration);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id, "animation");
            return;
        }

        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, values);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, values);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id, "animator");
    }

    //用于赛程表的
    public void onItemLoseFocus(View view, float value) {
        if ("animation".equals(view.getTag(R.id.tag_focus_id))) {
            // 直接缩小view
            ScaleAnimation sa = new ScaleAnimation(value, 1.0f, value, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id,null);
            return;
        }
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", value, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", value, 1.0f);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id,null);
    }

    public void onItemLoseFocus(View view) {
        if ("animation".equals(view.getTag(R.id.tag_focus_id))) {
            // 直接缩小view
            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id,null);
            return;
        }

        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1.0f);
        ObjectAnimator objectYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1.0f);
        animatorSet.playTogether(objectXAnimator, objectYAnimator);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id,null);
    }

    public void onItemLoseFocus(View view, View focusView) {

        if (focusView != null) {
            focusView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
        if ("animation".equals(view.getTag(R.id.tag_focus_id))) {
            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id,null);
            return;
        }
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(mSpringInterpolator);
        animatorSet.setTarget(view);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1.0f);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id,null);
    }

    public void onItemLoseFocus(View view, long duration, float values) {
        // 直接缩小view
        if ("animation".equals(view.getTag(R.id.tag_focus_id))) {
            ScaleAnimation sa = new ScaleAnimation(values, 1.0f, values, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(duration);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
            view.setTag(R.id.tag_focus_id,null);
            return;
        }
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight() / 2);
        view.bringToFront();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setTarget(view);
        animatorSet.setInterpolator(mSpringInterpolator);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", values, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", values, 1.0f);
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.start();
        view.setTag(R.id.tag_focus_id,null);
    }
}
