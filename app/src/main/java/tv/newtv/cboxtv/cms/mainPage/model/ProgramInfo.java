package tv.newtv.cboxtv.cms.mainPage.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixin on 2018/2/1.
 */

public class ProgramInfo implements Serializable {
    private String contentUUID;
    private String contentType;
    private String img; // 海报地址
    private String title;
    private String subTitle;
    private String actionType;
    private String actionUri;
    private String cellType;
    private String grade;
    private String playUrl;
    private String liveLoopType;
    private String liveParam;
    private String playStartTime;
    private String playEndTime;
    private String lSuperScript;
    private String rSuperScript;
    private String lSubScript;
    private String rSubScript;
    private String columnPoint;
    private String rowPoint;
    private String columnLength;
    private String hImage;
    private String vImage;
    private String rowLength;
    private String cellCode; // 组件内推荐位id
    private int isAd; // 是否是广告位
    private String focusPageUUID;
    private String focusParam;
    private String isTimeShift;
    private String seriesSubUUID;

    public String getSeriesSubUUID() {
        return seriesSubUUID;
    }

    public void setSeriesSubUUID(String seriesSubUUID) {
        this.seriesSubUUID = seriesSubUUID;
    }

    public String getFocusParam() {
        return focusParam;
    }

    public void setFocusParam(String focusParam) {
        this.focusParam = focusParam;
    }

    private List<SearchConditions> searchConditions;
    private List<ExtendAttr> extendAttr;
    private String recommendedType; //"1" 图片 "2" 播放器

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setLiveLoopType(String liveLoopType) {
        this.liveLoopType = liveLoopType;
    }

    public String getLiveLoopType() {
        return liveLoopType;
    }

    public void setLiveParam(String liveParam) {
        this.liveParam = liveParam;
    }

    public String getLiveParam() {
        return liveParam;
    }

    public void setPlayEndTime(String playEndTime) {
        this.playEndTime = playEndTime;
    }

    public String getPlayEndTime() {
        return playEndTime;
    }

    public void setPlayStartTime(String playStartTime) {
        this.playStartTime = playStartTime;
    }

    public String getPlayStartTime() {
        return playStartTime;
    }

    public void setIsTimeShift(String isTimeShift) {
        this.isTimeShift = isTimeShift;
    }

    public String getIsTimeShift() {
        return isTimeShift;
    }

    public void setFocusPageUUID(String focusPageUUID) {
        this.focusPageUUID = focusPageUUID;
    }

    public String getFocusPageUUID() {
        return focusPageUUID;
    }

    public String gethImage() {
        return hImage;
    }

    public void sethImage(String hImage) {
        this.hImage = hImage;
    }

    public String getvImage() {
        return vImage;
    }

    public void setvImage(String vImage) {
        this.vImage = vImage;
    }

    public List<SearchConditions> getSearchConditions() {
        return searchConditions;
    }

    public void setSearchConditions(List<SearchConditions> searchConditions) {
        searchConditions = searchConditions;
    }

    public void setExtendAttr(List<ExtendAttr> extendAttrs){
        this.extendAttr = extendAttrs;
    }

    public List<ExtendAttr> getExtendAttr() {
        return extendAttr;
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setContentUUID(String contentID) {
        this.contentUUID = contentID;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionUri() {
        return actionUri;
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getlSuperScript() {
        return lSuperScript;
    }

    public void setlSuperScript(String lSuperScript) {
        this.lSuperScript = lSuperScript;
    }

    public String getrSuperScript() {
        return rSuperScript;
    }

    public void setrSuperScript(String rSuperScript) {
        this.rSuperScript = rSuperScript;
    }

    public String getlSubScript() {
        return lSubScript;
    }

    public void setlSubScript(String lSubScript) {
        this.lSubScript = lSubScript;
    }

    public String getrSubScript() {
        return rSubScript;
    }

    public void setrSubScript(String rSubScript) {
        this.rSubScript = rSubScript;
    }

    public String getColumnPoint() {
        return columnPoint;
    }

    public void setColumnPoint(String columnPoint) {
        this.columnPoint = columnPoint;
    }

    public String getRowPoint() {
        return rowPoint;
    }

    public void setRowPoint(String rowPoint) {
        this.rowPoint = rowPoint;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength;
    }

    public String getRowLength() {
        return rowLength;
    }

    public void setRowLength(String rowLength) {
        this.rowLength = rowLength;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public int getIsAd() {
        return isAd;
    }

    public void setIsAd(int isAd) {
        this.isAd = isAd;
    }

    public String getRecommendedType() {
        return recommendedType;
    }

    public void setRecommendedType(String recommendedType) {
        this.recommendedType = recommendedType;
    }

    @Override
    public String toString() {
        return "ProgramInfo{" +
                "contentUUID='" + contentUUID + '\'' +
                ", contentType='" + contentType + '\'' +
                ", img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionUri='" + actionUri + '\'' +
                ", cellType='" + cellType + '\'' +
                ", grade='" + grade + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", liveLoopType='" + liveLoopType + '\'' +
                ", liveParam='" + liveParam + '\'' +
                ", playStartTime='" + playStartTime + '\'' +
                ", playEndTime='" + playEndTime + '\'' +
                ", lSuperScript='" + lSuperScript + '\'' +
                ", rSuperScript='" + rSuperScript + '\'' +
                ", lSubScript='" + lSubScript + '\'' +
                ", rSubScript='" + rSubScript + '\'' +
                ", columnPoint='" + columnPoint + '\'' +
                ", rowPoint='" + rowPoint + '\'' +
                ", columnLength='" + columnLength + '\'' +
                ", hImage='" + hImage + '\'' +
                ", vImage='" + vImage + '\'' +
                ", rowLength='" + rowLength + '\'' +
                ", cellCode='" + cellCode + '\'' +
                ", isAd=" + isAd +
                ", focusPageUUID='" + focusPageUUID + '\'' +
                ", focusParam='" + focusParam + '\'' +
                ", isTimeShift='" + isTimeShift + '\'' +
                ", seriesSubUUID='" + seriesSubUUID + '\'' +
                ", searchConditions=" + searchConditions +
                ", extendAttr=" + extendAttr +
                ", recommendedType='" + recommendedType + '\'' +
                '}';
    }
}
