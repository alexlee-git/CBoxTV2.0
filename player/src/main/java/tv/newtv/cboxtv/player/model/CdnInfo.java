package tv.newtv.cboxtv.player.model;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.model
 * 创建事件:         13:35
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class CdnInfo {
    private String mediaType;
    private String CDNId;
    private String PlayURL;

    public String getPlayURL() {
        return PlayURL;
    }

    public String getCDNId() {
        return CDNId;
    }

    public String getMediaType() {
        return mediaType;
    }
}
