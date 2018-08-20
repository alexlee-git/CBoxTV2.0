package tv.newtv.cboxtv.cms.mainPage.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lixin on 2018/1/11.
 */

public class NavInfoResult<T> {
    @SerializedName("errorMessage")
    private String errMsg;

    @SerializedName("errorCode")
    private String errCode;
    private String defaultFocus;

    private String navLocation;
    private T data;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getDefaultFocus() {
        return defaultFocus;
    }

    public void setDefaultFocus(String defaultFocus) {
        this.defaultFocus = defaultFocus;
    }

    public String getNavLocation() {
        return navLocation;
    }

    public void setNavLocation(String navLocation) {
        this.navLocation = navLocation;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public class NavInfo {
        @SerializedName("contentUUID")
        private String contentID;
        private String sortNum;

        @SerializedName("img")
        private String icon;
        @SerializedName("img_select")
        private String icon_select;
        @SerializedName("img_focus")
        private String icon_focus;
        private String actionType;
        private String focusId;

        @SerializedName("actionUri")
        private String actionURI;
        private String title;
        @SerializedName("image1")
        private String icon1;
        private String background;
        private int isAd; // 0：不是广告位 1；是广告位

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public int getIsAd() {
            return isAd;
        }

        public void setIsAd(int isAd) {
            this.isAd = isAd;
        }

        public String getIcon1() {
            return icon1;
        }

        public void setIcon1(String icon1) {
            this.icon1 = icon1;
        }

        public String getContentID() {
            return contentID;
        }

        public void setContentID(String contentID) {
            this.contentID = contentID;
        }

        public String getSortNum() {
            return sortNum;
        }

        public void setSortNum(String sortNum) {
            this.sortNum = sortNum;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getActionURI() {
            return actionURI;
        }

        public void setActionURI(String actionURI) {
            this.actionURI = actionURI;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon_select() {
            return icon_select;
        }

        public void setIcon_select(String icon_select) {
            this.icon_select = icon_select;
        }

        public String getIcon_focus() {
            return icon_focus;
        }

        public void setIcon_focus(String icon_focus) {
            this.icon_focus = icon_focus;
        }
    }

}
