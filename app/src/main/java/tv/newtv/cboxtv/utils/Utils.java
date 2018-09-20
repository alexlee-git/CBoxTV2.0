package tv.newtv.cboxtv.utils;


import android.text.TextUtils;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 熊猫渠道使用
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    //语音助手
    static final String AIASSIST_PACKAGE_NAME = "tv.newtv.aiassist";
    static final String AIASSIST_ACTIVITY_NAME = "AIActivity";

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
}

