package tv.newtv.cboxtv.cms.util;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by TCP on 2018/4/11.
 */

public class Md5Utils {
    /**
     * 对传递过来的字符串进行md5加密
     * @param str
     *      待加密的字符串
     * @return
     *      字符串Md5加密后的结果
     */
    public static String md5(String str){

        if (TextUtils.isEmpty(str)){
            return "";
        }
        StringBuilder sb = new StringBuilder();//字符串容器
        try {
            //获取md5加密器.public static MessageDigest getInstance(String algorithm)返回实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();//把要加密的字符串转换成字节数组
            byte[] digest = md.digest(bytes);//使用指定的 【byte 数组】对摘要进行最后更新，然后完成摘要计算。即完成md5的加密

            for (byte b : digest) {
                //把每个字节转换成16进制数
                int d = b & 0xff;//只保留后两位数
                String herString = Integer.toHexString(d);//把int类型数据转为16进制字符串表示
                //如果只有一位，则在前面补0.让其也是两位
                if(herString.length()==1){//字节高4位为0
                    herString = "0"+herString;//拼接字符串，拼成两位表示
                }
                sb.append(herString);
            }
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(e.toString());
        }

        return sb.toString().toUpperCase();//转成大写
    }

    public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            LogUtils.e(ex.toString());
        }
        if (macSerial != null && !TextUtils.isEmpty(macSerial)){
            macSerial = macSerial.replace(":","").toUpperCase();
        }
        return macSerial;
    }
}
