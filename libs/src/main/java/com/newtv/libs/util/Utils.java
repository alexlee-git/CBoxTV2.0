package com.newtv.libs.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.newtv.libs.AnimationBuilder;
import com.newtv.libs.Constant;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;


public class Utils {
    private static final String TAG = Utils.class.getName();

    //语音助手
    static final String AIASSIST_PACKAGE_NAME = "tv.newtv.aiassist";
    static final String AIASSIST_ACTIVITY_NAME = "AIActivity";

    //通过解析这个文件来获取MAC,不同厂家的芯片有可能不同
    private static final String ETH0_MAC_ADDR = "/sys/class/net/eth0/address";
    private static final String WIFI_MAC_ADDR = "/sys/class/net/wlan0/address";
    private static String mac;

    public static boolean isTopActivityIsAiassist(){
        Log.i(TAG, "isTopActivityIsAiassist: ");
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p =  Runtime.getRuntime().exec("sh");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes("dumpsys activity | grep \"mFocusedActivity\"" + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(TAG, "isTopActivityIsAiassist: result="+result);
        if(!TextUtils.isEmpty(result)
                && result.contains(AIASSIST_PACKAGE_NAME)
                && result.contains(AIASSIST_ACTIVITY_NAME)){
            return true;
        }
        return false;
    }

    public static void zoomByFactor(View view, float factor, int duration) {
        if (!view.isFocusable()) {
            return;
        }
        ScaleAnimation animation = AnimationBuilder.getInstance()
                .getScaleAnimation(1.0f, factor, 1.0f, factor,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, duration);
        TextView name = (TextView) view.findViewWithTag("");
        if (name != null) {
            name.setVisibility(View.VISIBLE);
        }
        if (animation != null) {
            view.startAnimation(animation);
        }
    }

    public static void scaleToOriginalDimension(View view, float factor, int duration) {
        ScaleAnimation animation = AnimationBuilder.getInstance()
                .getScaleAnimation(factor, 1.0f, factor, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, duration);
        TextView name = (TextView) view.findViewWithTag("");
        if (name != null) {
            name.setSelected(false);
        }
        if (animation != null) {
            view.startAnimation(animation);
        }
    }

    public static String getSplitFirst(String s) {
        String result = "";
        String[] sp = s.split("|");
        if (sp != null && sp.length > 0) {
            result = sp[0];
        }

        return result;
    }



    public static long getSysTime(){
        return System.currentTimeMillis()/1000;
    }

    /**
     * 判断某一个类是否存在任务栈里面
     * @return
     */
    public static boolean isExsitActivity(Context context, Class<?> cls){
        boolean flag = false;
        try {
            Intent intent = new Intent(context, cls);
            ComponentName cmpName = intent.resolveActivity(context.getPackageManager());

            if (cmpName != null) { // 说明系统中存在这个activity
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                        flag = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

        return flag;
    }



    //用于传入播放器，播放器传入广告sdk后获取广告
    //用于传入播放器，播放器传入广告sdk后获取广告
    public static String buildExtendString(String columnId, String secondColumnId, String
            parentId, String contentId, String topic,String alterId) {
        String extend = "";

        if (!TextUtils.isEmpty(columnId)) {
            if (!TextUtils.isEmpty(columnId)) {
                extend = "panel=" + columnId;
            }
            if (!TextUtils.isEmpty(secondColumnId)) {
                if (TextUtils.isEmpty(extend)) {
                    extend += "secondpanel=" + secondColumnId;
                } else {
                    extend += "&secondpanel=" + secondColumnId;
                }
            }
        } else {
            if (!TextUtils.isEmpty(parentId)) {
                extend = "panel=" + parentId;
            }

            if (!TextUtils.isEmpty(contentId)) {
                if (TextUtils.isEmpty(extend)) {
                    extend = "secondpanel=" + contentId;
                } else {
                    extend += "&secondpanel=" + contentId;
                }
            }
        }

        if (!TextUtils.isEmpty(topic)) {
            extend += "&topic=" + topic;
        }

        if (!TextUtils.isEmpty(alterId)) {
            extend += "&carousel=" + alterId;
        }

        LogUtils.i(TAG, "extend=" + extend);
        return extend;
    }


    /*
   * 获取mac号
   * */
    public static String getWireMacAddr() {
        try {
//            return readLine(ETH0_MAC_ADDR);
            return readLine(WIFI_MAC_ADDR);
        } catch (IOException e) {
            Log.e(TAG,
                    "IO Exception when getting eth0 mac address",
                    e);
            e.printStackTrace();
            return "";
        }
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String getAuthorization(Context context) {
        String icntvId;
        if (!TextUtils.isEmpty(Constant.UUID)) {
            icntvId = Constant.UUID;
        } else {
            icntvId = (String) SPrefUtils.getValue(context, Constant.UUID_KEY, "");
        }

        Log.e(TAG, "icntvId---is:" + icntvId);
//        String MAC = getWireMacAddr();
        String MAC = SystemUtils.getMac(context);
        if (TextUtils.isEmpty(MAC)) {
            Log.e(TAG, "mac---is---null");
        } else {
            mac = MAC.replace(":", "").toUpperCase();
        }
        if (TextUtils.isEmpty(icntvId) || TextUtils.isEmpty(mac)) {
            Log.d(TAG, "getAuthorization: encodeAuthorization = ");
            return null;
        } else {
            String authorization = mac + ":" + icntvId;
            String encodeAuthorization = "Basic ";
            try {
                encodeAuthorization = encodeAuthorization + new String(Base64.encode(authorization.getBytes("utf-8"), Base64.DEFAULT), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            Log.d(TAG, "getAuthorization: encodeAuthorization = " + encodeAuthorization);

            // TODO 切换到正式发环境后修改
            //return "Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW";

            // 正式逻辑
            return encodeAuthorization.replaceAll("\r|\n", "");
        }
    }
}
