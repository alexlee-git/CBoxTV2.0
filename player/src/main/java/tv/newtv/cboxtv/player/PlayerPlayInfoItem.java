package tv.newtv.cboxtv.player;

import java.util.List;

import tv.newtv.cboxtv.player.model.CdnInfo;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         13:18
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class PlayerPlayInfoItem {
    private String title;
    private String contentUUID;
    private String hImage;
    private String actionType;
    private String seriesSubUUID;
    private String duration;
    private String programSeriesUUIDs;
    private String categoryIds;
    private Boolean encryptFlag;
    private String decryptKey;
    private String playStartTime;
    private String playEndTime;
    private Boolean isMenuGroupHistory;
    private List<CdnInfo> data;

    public Boolean isMenuGroupHistory() {
        return isMenuGroupHistory;
    }

    public String getPlayStartTime() {
        return playStartTime;
    }

    public String getPlayEndTime() {
        return playEndTime;
    }

    public String getDecryptKey() {
        return decryptKey;
    }

    public Boolean getEncryptFlag() {
        return encryptFlag;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public String getProgramSeriesUUIDs() {
        return programSeriesUUIDs;
    }

    public String getDuration() {
        return duration;
    }

    public List<CdnInfo> getData() {
        return data;
    }

    public String getActionType() {
        return actionType;
    }

    public String getSeriesSubUUID() {
        return seriesSubUUID;
    }

    public String gethImage() {
        return hImage;
    }

    public String getTitle() {
        return title;
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
    }

    public void sethImage(String hImage) {
        this.hImage = hImage;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setSeriesSubUUID(String seriesSubUUID) {
        this.seriesSubUUID = seriesSubUUID;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setProgramSeriesUUIDs(String programSeriesUUIDs) {
        this.programSeriesUUIDs = programSeriesUUIDs;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public void setEncryptFlag(Boolean encryptFlag) {
        this.encryptFlag = encryptFlag;
    }

    public void setDecryptKey(String decryptKey) {
        this.decryptKey = decryptKey;
    }

    public void setPlayStartTime(String playStartTime) {
        this.playStartTime = playStartTime;
    }

    public void setPlayEndTime(String playEndTime) {
        this.playEndTime = playEndTime;
    }

    public void setMenuGroupHistory(Boolean menuGroupHistory) {
        isMenuGroupHistory = menuGroupHistory;
    }

    public void setData(List<CdnInfo> data) {
        this.data = data;
    }
}
