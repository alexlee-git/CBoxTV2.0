package com.newtv.libs.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by lin on 2018/3/14.
 */

public class SharePreferenceUtils {
    private static final String TAG = SharePreferenceUtils.class.getSimpleName();

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("token", "");
        Log.d(TAG, "SharePreferenceUtils--getToken: accessToken = " + accessToken);

        return accessToken;
    }

    public static void saveToken(Context context, String accessToken, String refreshToken) {

        try {
            String[] accessTokens = accessToken.split("\\.");
            String decodeUserId = new String(Base64.decode(accessTokens[1], Base64.DEFAULT), "utf-8");
            Log.d(TAG, "saveUserId: decodeUserId = " + decodeUserId);

            JSONObject userObject = new JSONObject(decodeUserId);
            String userId = userObject.optString("sub");
            long iat = userObject.optLong("iat");
            long exp = userObject.optLong("exp");

            SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Log.d(TAG, "saveToken: accessToken = " + accessToken);
            editor.putString("token", accessToken);
            Log.d(TAG, "saveToken: accessToken = " + accessToken);
            editor.putString("refreshtoken", refreshToken);
            Log.d(TAG, "saveToken: userId = " + userId);
            editor.putString("userId", userId);
            editor.putLong("iat", iat);
            Log.d(TAG, "saveToken: iat = " + iat);
            editor.putLong("exp", exp);
            Log.d(TAG, "saveToken: exp = " + exp);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearToken(Context context) {
        Log.d(TAG, "clearToken: clear user");
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static String getrefreshToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("refreshtoken", "");
        Log.d(TAG, "SharePreferenceUtils--getrefreshToken: freshToken = " + accessToken);

        return accessToken;
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        Log.d(TAG, "SharePreferenceUtils--getUserId: userId = " + userId);

        return userId;
    }

    public static long getBuildTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        long iat = preferences.getLong("iat", 0);
        Log.d(TAG, "SharePreferenceUtils--getBuildTime: iat = " + iat);

        return iat;
    }

    public static long getInvalidTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        long exp = preferences.getLong("exp", 0);
        Log.d(TAG, "SharePreferenceUtils--getInvalidTime: exp = " + exp);

        return exp;
    }

    /**
     * 用户性别
     */
    public static int getSex(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        int status = preferences.getInt("sex", -1);
        Log.d(TAG, "SharePreferenceUtils--getSex: sex = " + status);
        return status;
    }

    public static void saveSex(Context context, int status) {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "saveSex: sex = " + status);
        editor.putInt("sex", status);
        editor.apply();
    }

    /**
     * 设置中本地行为是否自动同步到已登录账号下
     * 0-是；1-否
     *
     * @param context
     * @return
     */
    public static int getSyncStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        int status = preferences.getInt("syncStatus", 0);
        Log.d(TAG, "SharePreferenceUtils--getSyncStatus: syncStatus = " + status);
        return status;
    }

    public static void saveSyncStatus(Context context, int syncStatus) {
        SharedPreferences preferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "saveSyncStatus: syncStatus = " + syncStatus);
        editor.putInt("syncStatus", syncStatus);
        editor.apply();
    }

    /**
     * 升级数据中的更新描述内容
     */
    public static String getUpdateInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        String updateInfo = preferences.getString("updateInfo", "");
        Log.d(TAG, "SharePreferenceUtils--getUpdateInfo: updateInfo = " + updateInfo);
        return updateInfo;
    }

    public static void saveUpdateInfo(Context context, String updateInfo) {
        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "saveUpdateInfo: updateInfo = " + updateInfo);
        editor.putString("updateInfo", updateInfo);
        editor.apply();
    }

    /**
     * 升级下载apk路径保存
     */
    public static String getUpdateApkPath(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        String apkPath = preferences.getString("apkPath", "");
        Log.d(TAG, "SharePreferenceUtils--getUpdateApkPath: apkPath = " + apkPath);
        return apkPath;
    }

    public static void saveUpdateApkPath(Context context, String apkPath) {
        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "saveUpdateApkPath: apkPath = " + apkPath);
        editor.putString("apkPath", apkPath);
        editor.apply();
    }

    /**
     * 升级下载apk时downloadId获取和保存
     */
    public static Long getDownloadId(Context context) {

        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        Long downloadId = preferences.getLong("downloadId", 0L);
        Log.d(TAG, "SharePreferenceUtils--getDownloadId: downloadId = " + downloadId);
        return downloadId;
    }

    public static void saveDownloadId(Context context, Long downloadId) {
        SharedPreferences preferences = context.getSharedPreferences("Update", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.d(TAG, "saveDownloadId: downloadId = " + downloadId);
        editor.putLong("downloadId", downloadId);
        editor.apply();
    }
}
