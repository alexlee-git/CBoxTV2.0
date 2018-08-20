package tv.newtv.cboxtv.cms.ad.model;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.util.Md5Utils;
import tv.newtv.cboxtv.cms.util.SystemUtils;

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
