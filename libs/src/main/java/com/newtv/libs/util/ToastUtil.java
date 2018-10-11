package com.newtv.libs.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.libs.util
 * 创建事件:         18:44
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public final class ToastUtil {
    /**
     * Shows a (long) tip
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) tip.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }
}
