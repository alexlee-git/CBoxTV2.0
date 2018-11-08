package com.newtv.libs.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;


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

    /**
     * 获取该文件的md5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                Log.e("StringUtils", "fileIsExists:=false " );
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("StringUtils", "fileIsExists:=false---Exception");
            return false;
        }
        Log.e("StringUtils", "fileIsExists:=true " );
        return true;
    }
}
