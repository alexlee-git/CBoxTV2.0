package tv.newtv.cboxtv.cms.details.model;

import com.google.gson.annotations.SerializedName;

import tv.newtv.cboxtv.player.ProgramSeriesInfo;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class ProgramSeriesInfoResult {

    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("data")
    private ProgramSeriesInfo mProgramSeriesInfo;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ProgramSeriesInfo getmProgramSeriesInfo() {
        return mProgramSeriesInfo;
    }

    public void setmProgramSeriesInfo(ProgramSeriesInfo mProgramSeriesInfo) {
        this.mProgramSeriesInfo = mProgramSeriesInfo;
    }

    @Override
    public String toString() {
        return "ProgramSeriesInfoResult{" +
                "mProgramSeriesInfo=" + mProgramSeriesInfo +
                '}';
    }

    public class ProgramsInfo {

        private String contentId;
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

        public ProgramsInfo(String contentId, String title, String contentType, String hImage, String vImage, String subTitle, String actionType, String actionUri, String grade, String lSuperScript, String rSuperScript, String lSubScript, String rSubScript, String periods) {
            this.contentId = contentId;
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
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
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
    }
}
