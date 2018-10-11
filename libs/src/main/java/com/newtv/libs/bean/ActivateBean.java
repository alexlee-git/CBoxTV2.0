package com.newtv.libs.bean;

import com.newtv.libs.Constant;
import com.newtv.libs.util.Md5Utils;

/**
 * Created by TCP on 2018/4/12.
 */

public class ActivateBean {
    private String mac;
    private String key;
    private String channelId;
    private String ts;
    private String token;

    public ActivateBean(String m,String k,String c,String t){
        mac = m;
        key = k;
        channelId = c;
        ts  = t;
        token = Md5Utils.md5(mac + ts +
                key + channelId + Constant.APPSECRET);
    }
}
