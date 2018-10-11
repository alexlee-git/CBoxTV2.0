package com.newtv.libs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Collection;
import java.util.WeakHashMap;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         17:55
 * 创建人:           weihaichao
 * 创建日期:          2018/5/8
 */
public class UsefulBitmapFactory {
    private static WeakHashMap<Integer, Bitmap> bitmapHashMap = new WeakHashMap<>();

    public static Bitmap findBitmap(Context context, int resId) {
        if (resId == 0) return null;
        if (bitmapHashMap.containsKey(resId)) {
            if (bitmapHashMap.get(resId) != null && !bitmapHashMap.get(resId).isRecycled()) {
                Bitmap bitmap = bitmapHashMap.get(resId);
                if(bitmap != null && !bitmap.isRecycled()){
                    return bitmap;
                }
                bitmapHashMap.remove(resId);
            }
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        bitmapHashMap.put(resId, bitmap);
        return bitmap;
    }

    public static void recycle() {
        if (bitmapHashMap == null || bitmapHashMap.size() <= 0) return;
        Collection<Bitmap> bitmaps = bitmapHashMap.values();
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        bitmapHashMap.clear();
    }
}
