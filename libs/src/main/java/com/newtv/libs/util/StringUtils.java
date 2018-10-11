package com.newtv.libs.util;

import android.text.TextUtils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         15:48
 * 创建人:           weihaichao
 * 创建日期:          2018/5/8
 */
public class StringUtils {
    public static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value) || "null".equals(value);
    }
}
