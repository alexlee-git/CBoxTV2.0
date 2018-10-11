package com.newtv.libs.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by TCP on 2018/4/11.
 */

public class SPrefUtils {
    private static final String FILE_NAME = "config";
    public static final String KEY_SERVER_ADDRESS = "server_address";

    public static <T>void setValue(Context context,String key,T value){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        if(value instanceof String){
            sp.edit().putString(key, (String) value).apply();
        }else if(value instanceof Integer){
            sp.edit().putInt(key, (Integer) value).apply();
        }else if(value instanceof Float){
            sp.edit().putFloat(key, (Float) value).apply();
        }else if(value instanceof Boolean){
            sp.edit().putBoolean(key, (Boolean) value).apply();
        }else if(value instanceof Long){
            sp.edit().putLong(key, (Long) value).apply();
        }
    }

    public static Object getValue(Context context,String key,Object defValue){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        if(defValue instanceof String){
            return sp.getString(key, (String) defValue);
        }else if(defValue instanceof Integer){
            return sp.getInt(key, (Integer) defValue);
        }else if(defValue instanceof Float){
            return sp.getFloat(key, (Float) defValue);
        }else if(defValue instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defValue);
        }else if(defValue instanceof Long){
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }
}
