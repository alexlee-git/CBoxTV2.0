package tv.newtv.cboxtv.uc.v2.sub;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 项目名称:         熊猫ROM-launcher应用
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午6:20
 * 创建人:           lixin
 * 创建日期:         2018/9/11
 */

public class UserCenterUniversalBean {

    public String title;
    public List<Bean> data;

    public UserCenterUniversalBean(String title) {
        this.title = title;
    }

    public static class Bean {
        private String contentUuid;
        private String contentType;
        private String actionType;
        private String posterUrl;
        private String title;
        private String score;
        private String updateTime;
        private String progress;// 观看的进度百分比
        private String playIndex; // 当前是第几集
        private String updateSuperscript; // 是否要显示"有更新"角标, "1":显示 "0":不显示

        @SerializedName("_play_position")
        private String breakPoint; // 断点时间

        private String duration; // 影片时长

        public String getUpdateSuperscript() {
            return updateSuperscript;
        }

        public void setUpdateSuperscript(String updateSuperscript) {
            this.updateSuperscript = updateSuperscript;
        }

        private String userId;
        private String subProgramId; // 子节目id
        private String totalCnt; // 如果是节目集就是子节目数

        public String getContentUuid() {
            return contentUuid;
        }

        public void setContentUuid(String contentUuid) {
            this.contentUuid = contentUuid;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public void setPosterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        public String getPlayIndex() {
            return playIndex;
        }

        public void setPlayIndex(String playIndex) {
            this.playIndex = playIndex;
        }

        public String getBreakPoint() {
            return breakPoint;
        }

        public void setBreakPoint(String breakPoint) {
            this.breakPoint = breakPoint;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSubProgramId() {
            return subProgramId;
        }

        public void setSubProgramId(String subProgramId) {
            this.subProgramId = subProgramId;
        }

        public String getTotalCnt() {
            return totalCnt;
        }

        public void setTotalCnt(String totalCnt) {
            this.totalCnt = totalCnt;
        }
    }
}
