package tv.newtv.cboxtv.player;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.details.model
 * 创建事件:         14:19
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class ProgramsInfo implements Serializable {

    private static final long serialVersionUID = 3461360387779387505L;
    private String contentUUID;
    private String title;
    private String contentType;
    private String hImage;
    private String vImage;
    private String subTitle;
    private String actionType;
    private String actionUri;        //动作地址
    private String grade;
    private String lSuperScript;    //左上角标ID
    private String rSuperScript;    //右上角标ID
    private String lSubScript;      //左下角标ID
    private String rSubScript;      //右下角标ID
    private String periods;         //集号
    private String des;         //集号
    private String liveUrl;
    private String playUrl;
    private String seriesSubUUID;
    private String playStartTime;
    private String playEndTime;

    //是否从栏目树观看历史进入的，如果是鉴权的AlbumId传seriesSubUUID
    private boolean isMenuGroupHistory;
    private String liveLoopType;
    private String liveParam;
    private String isTimeShift;
    private KeyAction recommendedType;
    private List<ExtendAttr> extendAttr;
    private String img;

    public ProgramsInfo() {
    }

    public ProgramsInfo(String contentUUID, String title, String contentType, String hImage,
                        String vImage, String subTitle, String actionType, String actionUri,
                        String grade, String lSuperScript, String rSuperScript, String
                                lSubScript, String rSubScript, String periods, String des) {
        this.contentUUID = contentUUID;
        this.title = title;
        this.contentType = contentType;
        this.hImage = hImage;
        this.vImage = vImage;
        this.subTitle = subTitle;
        this.actionType = actionType;
        this.actionUri = actionUri;
        this.grade = grade;
        this.lSuperScript = lSuperScript;
        this.rSuperScript = rSuperScript;
        this.lSubScript = lSubScript;
        this.rSubScript = rSubScript;
        this.periods = periods;
        this.des = des;
    }

    public boolean isMenuGroupHistory() {
        return isMenuGroupHistory;
    }

    public void setMenuGroupHistory(boolean menuGroupHistory) {
        isMenuGroupHistory = menuGroupHistory;
    }

    public String getPlayStartTime() {
        return playStartTime;
    }

    public void setPlayStartTime(String playStartTime) {
        this.playStartTime = playStartTime;
    }

    public String getPlayEndTime() {
        return playEndTime;
    }

    public void setPlayEndTime(String playEndTime) {
        this.playEndTime = playEndTime;
    }

    public String getSeriesSubUUID() {
        return seriesSubUUID;
    }

    public void setSeriesSubUUID(String seriesSubUUID) {
        this.seriesSubUUID = seriesSubUUID;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
    }

    public String getActionUri() {
        return actionUri;
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    @Override
    public String toString() {
        return "ProgramsInfo{" +
                "contentUUID='" + contentUUID + '\'' +
                ", title='" + title + '\'' +
                ", contentType='" + contentType + '\'' +
                ", hImage='" + hImage + '\'' +
                ", vImage='" + vImage + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionUri='" + actionUri + '\'' +
                ", grade='" + grade + '\'' +
                ", lSuperScript='" + lSuperScript + '\'' +
                ", rSuperScript='" + rSuperScript + '\'' +
                ", lSubScript='" + lSubScript + '\'' +
                ", rSubScript='" + rSubScript + '\'' +
                ", seriesSubUUID='" + seriesSubUUID + '\'' +
                ", periods='" + periods + '\'' +
                '}';
    }

    public String getLiveLoopType() {
        return liveLoopType;
    }

    public void setLiveLoopType(String liveLoopType) {
        this.liveLoopType = liveLoopType;
    }

    public String getLiveParam() {
        return liveParam;
    }

    public void setLiveParam(String liveParam) {
        this.liveParam = liveParam;
    }

    public String getIsTimeShift() {
        return isTimeShift;
    }

    public void setIsTimeShift(String isTimeShift) {
        this.isTimeShift = isTimeShift;
    }

    public KeyAction getRecommendedType() {
        return recommendedType;
    }

    public void setRecommendedType(KeyAction recommendedType) {
        this.recommendedType = recommendedType;
    }

    public List<ExtendAttr> getExtendAttr() {

        return extendAttr;
    }

    public void setExtendAttr(List<ExtendAttr> extendAttr) {
        this.extendAttr = extendAttr;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
