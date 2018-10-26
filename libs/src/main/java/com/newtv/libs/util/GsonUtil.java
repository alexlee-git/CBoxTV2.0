package com.newtv.libs.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    public static <T> T fromjson(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromjson(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

}
