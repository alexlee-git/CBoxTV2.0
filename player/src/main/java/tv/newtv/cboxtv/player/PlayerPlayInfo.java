package tv.newtv.cboxtv.player;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         11:38
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class PlayerPlayInfo implements Serializable {
    private String contentUUID;
    private String videoType;
    private String contentType;
    private String hImage;
    private String playStartTime;
    private String playEndTime;
    private String title;
    private String PlayUrl;
    private String isTimeShift;
    private String programSeriesUUIDs;
    private List<PlayerPlayInfoItem> data;

    public String getProgramSeriesUUIDs() {
        return programSeriesUUIDs;
    }

    public String getIsTimeShift() {
        return isTimeShift;
    }

    public String getPlayUrl() {
        return PlayUrl;
    }

    public String gethImage() {
        return hImage;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }

    public String getPlayEndTime() {
        return playEndTime;
    }

    public String getPlayStartTime() {
        return playStartTime;
    }

    public List<PlayerPlayInfoItem> getData() {
        return data;
    }

    public String getContentUUID() {
        return contentUUID;
    }
    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void sethImage(String hImage) {
        this.hImage = hImage;
    }

    public void setPlayStartTime(String playStartTime) {
        this.playStartTime = playStartTime;
    }

    public void setPlayEndTime(String playEndTime) {
        this.playEndTime = playEndTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlayUrl(String playUrl) {
        PlayUrl = playUrl;
    }

    public void setIsTimeShift(String isTimeShift) {
        this.isTimeShift = isTimeShift;
    }

    public void setProgramSeriesUUIDs(String programSeriesUUIDs) {
        this.programSeriesUUIDs = programSeriesUUIDs;
    }

    public void setData(List<PlayerPlayInfoItem> data) {
        this.data = data;
    }

    public void setLiveUrl(String playUrl) {

    }

    public void setLiveLoopType(String liveLoopType) {

    }

    public void setLiveParam(String liveParam) {

    }
}
