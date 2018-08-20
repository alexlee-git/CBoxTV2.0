package tv.newtv.cboxtv.cms.details.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class MediaCDNInfo implements Serializable{


    private static final long serialVersionUID = 2671207177424139367L;
    private int CDNId ;
    private String mediaType ;
    private String playURL ;

    public MediaCDNInfo() {
    }

    public MediaCDNInfo(int CDNId, String mediaType, String playURL) {
        this.CDNId = CDNId;
        this.mediaType = mediaType;
        this.playURL = playURL;
    }

    public int getCDNId() {
        return CDNId;
    }

    public void setCDNId(int CDNId) {
        this.CDNId = CDNId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPlayURL() {
        return playURL;
    }

    public void setPlayURL(String playURL) {
        this.playURL = playURL;
    }

    @Override
    public String toString() {
        return "MediaCDNInfo{" +
                "CDNId=" + CDNId +
                ", mediaType='" + mediaType + '\'' +
                ", playUrl='" + playURL + '\'' +
                '}';
    }
}
