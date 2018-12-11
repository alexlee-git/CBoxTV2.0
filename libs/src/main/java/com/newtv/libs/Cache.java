package com.newtv.libs;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         14:56
 * 创建人:           weihaichao
 * 创建日期:          2018/11/30
 */
public class Cache {

    public static final int CACHE_TYPE_ALTERNATE = 0x001;
    public static final int CACHE_TYPE_NAV = 0x002;


    private static final Cache ourInstance = new Cache();
    private HashMap<Integer, HashMap<String, Object>> cacheMaps;

    @SuppressLint("UseSparseArrays")
    private Cache() {
        cacheMaps = new HashMap<>();
    }

    public static Cache getInstance() {
        return ourInstance;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(int type,String key){
        if(cacheMaps.containsKey(type) && cacheMaps.get(type).containsKey(key)){
            Object result = cacheMaps.get(type).get(key);
            if(result != null){
                return (T) result;
            }
        }
        return null;
    }

    public <T> void put(int type, String key, T value) {
        if (!cacheMaps.containsKey(type)) {
            cacheMaps.put(type, new HashMap<String, Object>());
        }
        cacheMaps.get(type).put(key, value);
    }
}
