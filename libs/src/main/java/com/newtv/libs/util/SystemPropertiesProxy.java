package com.newtv.libs.util;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

/**
 * Created by zhangxianda on 2018/9/29 16:36.
 */
public class SystemPropertiesProxy {

    public static  String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }
}
