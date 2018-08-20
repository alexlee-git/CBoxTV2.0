package tv.newtv.cboxtv.player.menu.model;

import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;

/**
 * Created by TCP on 2018/4/18.
 */

public class Program {
    private String contentUUID;
    private String contentType;
    private String hImage;
    private String vImage;
    private String title;
    private String subTitle;
    private String actionType;
    private String actionUri;
    private String grade;
    private String lSuperScript;
    private String rSuperSctipt;
    private String lSubScript;
    private String rSubScript;
    private String periods;
    private String premiereTime;
    /**
     * 节目集或者栏目的id，相当于
     */
    private String seriesSubUUID;

    private Node parent;

    public Node getParent() {
        return parent;
    }

    public boolean checkNode(String title){
        Node node = parent;
        while(node != null){
            if(title.equals(node.getTitle())){
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getSeriesSubUUID() {
        return seriesSubUUID;
    }

    public void setSeriesSubUUID(String seriesSubUUID) {
        this.seriesSubUUID = seriesSubUUID;
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
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

    public String getrSuperSctipt() {
        return rSuperSctipt;
    }

    public void setrSuperSctipt(String rSuperSctipt) {
        this.rSuperSctipt = rSuperSctipt;
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

    public String getPremiereTime() {
        return premiereTime;
    }

    public void setPremiereTime(String premiereTime) {
        this.premiereTime = premiereTime;
    }

    public ProgramInfo convertProgramInfo(){
        ProgramInfo info = new ProgramInfo();
        info.setContentUUID(contentUUID);
        info.setContentType(contentType);
        info.sethImage(hImage);
        info.setvImage(vImage);
        info.setTitle(title);
        info.setSubTitle(subTitle);
        info.setActionType(actionType);
        info.setActionUri(actionUri);
        info.setGrade(grade);
        info.setlSuperScript(lSuperScript);
        info.setrSuperScript(rSuperSctipt);
        info.setlSubScript(lSubScript);
        info.setrSubScript(rSubScript);
        return info;
    }

    public ProgramSeriesInfo.ProgramsInfo convertProgramsInfo(){
        ProgramSeriesInfo.ProgramsInfo info = new ProgramSeriesInfo.ProgramsInfo();
        info.setContentUUID(contentUUID);
        info.setContentType(contentType);
        info.sethImage(hImage);
        info.setvImage(vImage);
        info.setTitle(title);
        info.setSubTitle(subTitle);
        info.setActionType(actionType);
        info.setActionUri(actionUri);
        info.setGrade(grade);
        info.setlSuperScript(lSuperScript);
        info.setrSuperScript(rSuperSctipt);
        info.setlSubScript(lSubScript);
        info.setrSubScript(rSubScript);
        info.setSeriesSubUUID(seriesSubUUID);
        info.setMenuGroupHistory(true);
        return info;
    }

    @Override
    public String toString() {
        return "Program{" +
                "contentUUID='" + contentUUID + '\'' +
                ", contentType='" + contentType + '\'' +
                ", hImage='" + hImage + '\'' +
                ", vImage='" + vImage + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionUri='" + actionUri + '\'' +
                ", grade='" + grade + '\'' +
                ", lSuperScript='" + lSuperScript + '\'' +
                ", rSuperSctipt='" + rSuperSctipt + '\'' +
                ", lSubScript='" + lSubScript + '\'' +
                ", rSubScript='" + rSubScript + '\'' +
                ", periods='" + periods + '\'' +
                ", premiereTime='" + premiereTime + '\'' +
                ", seriesSubUUID='" + seriesSubUUID + '\'' +
                ", parent=" + parent +
                '}';
    }
}
