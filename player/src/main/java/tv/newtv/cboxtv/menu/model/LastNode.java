package tv.newtv.cboxtv.menu.model;

public class LastNode extends Node {
    public String contentId;
    public String isFinish;
    public String realExclusive;
    public String issuedate;
    public String lastPublishDate;
    public String subTitle;
    public String vImage;
    public String contentUUID;
    public String hImage;
    public String vipFlag;

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String getId() {
        return contentId;
    }

    @Override
    public void setId(String id) {
        this.contentId = id;
    }
}
