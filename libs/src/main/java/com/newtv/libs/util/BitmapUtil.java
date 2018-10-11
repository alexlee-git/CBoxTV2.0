package com.newtv.libs.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         13:58
 * 创建人:           weihaichao
 * 创建日期:          2018/5/7
 */
public class BitmapUtil {
    // 等比缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) return null;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.setTranslate(0, 0);
        matrix.postScale(scaleWidth, scaleHeight, 0, 0);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    // 等比缩放图片
    public static Bitmap zoomImg(Bitmap bm, float scaleX, float scaleY) {
        if (bm == null) return null;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.setTranslate(0, 0);
        matrix.postScale(scaleX, scaleY, 0, 0);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static void recycleImageBitmap(ViewGroup viewGroup) {
//        if (viewGroup == null) return;
//        int count = viewGroup.getChildCount();
//        for (int index = 0; index < count; index++) {
//            View view = viewGroup.getChildAt(index);
//            if (view instanceof RecycleImageView) {
//                ((RecycleImageView) view).recycle();
//            }else if(view instanceof CurrentPlayImageViewWorldCup){
//                ((CurrentPlayImageViewWorldCup) view).recycle();
//            } else if (view instanceof ViewGroup) {
//                recycleImageBitmap((ViewGroup) view);
//            }
//        }
    }
}




