package com.newtv.libs.bean;

import com.newtv.libs.Constant;
import com.newtv.libs.util.Md5Utils;


/**
 * Created by TCP on 2018/4/12.
 */

public class AuthBean {
    private String mac;
    private String key;
    private String channelId;
    private String ts;
    private String uuid;
    private String token;

    public AuthBean(String m,String k,String c,String u,String t){
        mac = m;
        key = k;
        channelId = c;
        ts  = t;
        uuid = u;
        token = Md5Utils.md5(mac + ts +
                key + uuid + channelId + Constant.APPSECRET);
    }
}
