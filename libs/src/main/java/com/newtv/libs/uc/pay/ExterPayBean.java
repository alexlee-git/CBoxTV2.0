package com.newtv.libs.uc.pay;

import java.io.Serializable;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2
 * 创建事件:     下午 3:49
 * 创建人:       caolonghe
 * 创建日期:     2018/10/19 0019
 */
public class ExterPayBean implements Serializable {


    private String vipProductId;
    private String action;
    private String contentUUID;
    private String MAMID;
    private String Title;
    private String vipFlag;
    private String contentType;

    public ExterPayBean() {
    }

    public ExterPayBean(String vipProductId, String action, String contentUUID, String MAMID, String title, String vipFlag, String contentType) {
        this.vipProductId = vipProductId;
        this.action = action;
        this.contentUUID = contentUUID;
        this.MAMID = MAMID;
        Title = title;
        this.vipFlag = vipFlag;
        this.contentType = contentType;
    }

    public String getVipProductId() {
        return vipProductId;
    }

    public void setVipProductId(String vipProductId) {
        this.vipProductId = vipProductId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
    }

    public String getMAMID() {
        return MAMID;
    }

    public void setMAMID(String MAMID) {
        this.MAMID = MAMID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(String vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "ExterPayBean{" +
                "vipProductId='" + vipProductId + '\'' +
                ", action='" + action + '\'' +
                ", contentUUID='" + contentUUID + '\'' +
                ", MAMID='" + MAMID + '\'' +
                ", Title='" + Title + '\'' +
                ", vipFlag='" + vipFlag + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
