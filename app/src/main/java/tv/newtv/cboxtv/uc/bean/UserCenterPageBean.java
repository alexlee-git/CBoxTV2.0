package tv.newtv.cboxtv.uc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public class UserCenterPageBean {

    public String title;
    public List<Bean> data;

    public UserCenterPageBean(String title) {
        this.title = title;
    }

    public static class Bean {
        public String _contentuuid;
        public String _contenttype;
        public String _actiontype;
        public String _imageurl;
        public String _title_name;
        public String superscript;

        public String is_program;

        @SerializedName("_play_progress")
        public String progress; // 播放百分比

        @SerializedName("_play_index")
        public String playIndex;//观看集数
        @SerializedName("_episode_num")
        public String episode_num;//已更新集数
        @SerializedName("grade")
        public String grade;//评分

        @SerializedName("_update_time")
        public long updateTime;

        @SerializedName("_play_position")
        public String playPosition;

        @SerializedName("_user_id")
        public String user_id; // 用户id

        @SerializedName("_total_count")
        public String totalCnt; // 内容总集数

        @SerializedName("_play_id")
        public String playId;

        @SerializedName("_video_type")
        public String videoType;

        @SerializedName("_update_superscript")
        public String isUpdate;

        @SerializedName("_content_duration")
        public String duration; // 影片时长

        @SerializedName("_program_child_name")
        public String programChildName;

        @SerializedName("_content_id")
        public String contentId;

        @SerializedName("_recent_msg")
        public String recentMsg;

        public String get_contentuuid() {
            return _contentuuid;
        }

        public void set_contentuuid(String _contentuuid) {
            this._contentuuid = _contentuuid;
        }

        public String get_contenttype() {
            return _contenttype;
        }

        public void set_contenttype(String _contenttype) {
            this._contenttype = _contenttype;
        }

        public String get_actiontype() {
            return _actiontype;
        }

        public void set_actiontype(String _actiontype) {
            this._actiontype = _actiontype;
        }

        public String get_imageurl() {
            return _imageurl;
        }

        public void set_imageurl(String _imageurl) {
            this._imageurl = _imageurl;
        }

        public String get_title_name() {
            return _title_name;
        }

        public void set_title_name(String _title_name) {
            this._title_name = _title_name;
        }

        public String getSuperscript() {
            return superscript;
        }

        public void setSuperscript(String superscript) {
            this.superscript = superscript;
        }

        public String getIs_program() {
            return is_program;
        }

        public void setIs_program(String is_program) {
            this.is_program = is_program;
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

        public String getEpisode_num() {
            return episode_num;
        }

        public void setEpisode_num(String episode_num) {
            this.episode_num = episode_num;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getPlayPosition() {
            return playPosition;
        }

        public void setPlayPosition(String playPosition) {
            this.playPosition = playPosition;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTotalCnt() {
            return totalCnt;
        }

        public void setTotalCnt(String totalCnt) {
            this.totalCnt = totalCnt;
        }

        public String getPlayId() {
            return playId;
        }

        public void setPlayId(String playId) {
            this.playId = playId;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public String getIsUpdate() {
            return isUpdate;
        }

        public void setIsUpdate(String isUpdate) {
            this.isUpdate = isUpdate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getProgramChildName() {
            return programChildName;
        }

        public void setProgramChildName(String programChildName) {
            this.programChildName = programChildName;
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

<<<<<<< Updated upstream
        public String getRecentMsg() {
            return recentMsg;
        }

        public void setRecentMsg(String recentMsg) {
            this.recentMsg = recentMsg;
=======
        @Override
        public String toString() {
            return "Bean{" +
                    "_contentuuid='" + _contentuuid + '\'' +
                    ", _contenttype='" + _contenttype + '\'' +
                    ", _actiontype='" + _actiontype + '\'' +
                    ", _imageurl='" + _imageurl + '\'' +
                    ", _title_name='" + _title_name + '\'' +
                    ", superscript='" + superscript + '\'' +
                    ", is_program='" + is_program + '\'' +
                    ", progress='" + progress + '\'' +
                    ", playIndex='" + playIndex + '\'' +
                    ", episode_num='" + episode_num + '\'' +
                    ", grade='" + grade + '\'' +
                    ", updateTime=" + updateTime +
                    ", playPosition='" + playPosition + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", totalCnt='" + totalCnt + '\'' +
                    ", playId='" + playId + '\'' +
                    ", videoType='" + videoType + '\'' +
                    ", isUpdate='" + isUpdate + '\'' +
                    ", duration='" + duration + '\'' +
                    ", programChildName='" + programChildName + '\'' +
                    ", contentId='" + contentId + '\'' +
                    '}';
>>>>>>> Stashed changes
        }
    }
}
