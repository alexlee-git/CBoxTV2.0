package tv.newtv.cboxtv.cms.listPage.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by caolonghe on 2018/3/3.
 */

public class NavListPageInfoResult {
    @SerializedName("errorMessage")
    private String errMsg;

    @SerializedName("errorCode")
    private String errCode;
    private String defaultFocus;

    @SerializedName("blockTitle")
    private String blockTitle ;
    private String navLocation;
    @SerializedName("data")
    private List<NavInfo> data;

    public String getBlockTitle() {
        return blockTitle;
    }

    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

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

    public List<NavInfo> getData() {
        return data;
    }

    public void setData(List<NavInfo> data) {
        this.data = data;
    }

    public class NavInfo implements Comparable<NavInfo>{
        @SerializedName("contentUUID")
        private String contentID;
        private String sortNum;

        @SerializedName("img")
        private String icon;
        private String actionType;

        @SerializedName("actionUri")
        private String actionURI;
        private String title;
        @SerializedName("image1")
        private String icon1;

        public String getIcon1() {
            return icon1;
        }

        public void setIcon1(String icon1) {
            this.icon1 = icon1;
        }

        public String getContentID() {
            if (contentID == null) {
                contentID = "";
            }
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
            if (actionURI == null) {
                actionURI = "";
            }
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

        @Override
        public int compareTo(NavInfo o) {
            return this.sortNum.compareTo(o.sortNum) ;

        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof NavInfo) {
                NavInfo objNavInfo = (NavInfo)obj;
                if (objNavInfo.contentID.equals(this.contentID)
                        && objNavInfo.sortNum.equals(this.sortNum)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.contentID.hashCode() * 37;
        }
    }

}
