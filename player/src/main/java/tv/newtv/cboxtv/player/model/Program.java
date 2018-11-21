package tv.newtv.cboxtv.player.model;

public class Program {
    private String programSeriesUUID;
    private String programSeriesId;
    private String vipNumber;
    private String title;
    private String vipFlag;
    private String isVip;

    public String getProgramSeriesUUID() {
        return programSeriesUUID;
    }

    public void setProgramSeriesUUID(String programSeriesUUID) {
        this.programSeriesUUID = programSeriesUUID;
    }

    public String getProgramSeriesId() {
        return programSeriesId;
    }

    public void setProgramSeriesId(String programSeriesId) {
        this.programSeriesId = programSeriesId;
    }

    public String getVipNumber() {
        return vipNumber;
    }

    public void setVipNumber(String vipNumber) {
        this.vipNumber = vipNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(String vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }
}
