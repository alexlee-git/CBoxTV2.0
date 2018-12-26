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

        //轮播收藏使用
        public String is_finish;//是否结束
        public String real_exclusive;//运营标识
        public String issue_date;
        public String last_publish_date;
        public String sub_title;
        public String v_image;
        public String h_image;
        public String vip_flag;//付费标识
        public String alternate_number;

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

        public String getRecentMsg() {
            return recentMsg;
        }

        public void setRecentMsg(String recentMsg) {
            this.recentMsg = recentMsg;
        }

        public String getIs_finish() {
            return is_finish;
        }

        public void setIs_finish(String is_finish) {
            this.is_finish = is_finish;
        }

        public String getReal_exclusive() {
            return real_exclusive;
        }

        public void setReal_exclusive(String real_exclusive) {
            this.real_exclusive = real_exclusive;
        }

        public String getIssue_date() {
            return issue_date;
        }

        public void setIssue_date(String issue_date) {
            this.issue_date = issue_date;
        }

        public String getLast_publish_date() {
            return last_publish_date;
        }

        public void setLast_publish_date(String last_publish_date) {
            this.last_publish_date = last_publish_date;
        }

        public String getSub_title() {
            return sub_title;
        }

        public void setSub_title(String sub_title) {
            this.sub_title = sub_title;
        }

        public String getV_image() {
            return v_image;
        }

        public void setV_image(String v_image) {
            this.v_image = v_image;
        }

        public String getH_image() {
            return h_image;
        }

        public void setH_image(String h_image) {
            this.h_image = h_image;
        }

        public String getVip_flag() {
            return vip_flag;
        }

        public void setVip_flag(String vip_flag) {
            this.vip_flag = vip_flag;
        }

        public String getAlternate_number() {
            return alternate_number;
        }

        public void setAlternate_number(String alternate_number) {
            this.alternate_number = alternate_number;
        }

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
                    ", recentMsg='" + recentMsg + '\'' +
                    '}';
        }


    }

    //2018.12.21 wqs 添加记录扩展字段使用
    public static class ExtendBean {

        /**
         * versionCode :
         */

        private String versionCode;//应用版本号
        public String is_finish;//是否结束
        public String real_exclusive;//运营标识
        public String issue_date;
        public String last_publish_date;
        public String sub_title;
        public String v_image;
        public String h_image;
        public String vip_flag;//付费标识
        public String alternate_number;

        public String getIs_finish() {
            return is_finish;
        }

        public void setIs_finish(String is_finish) {
            this.is_finish = is_finish;
        }

        public String getReal_exclusive() {
            return real_exclusive;
        }

        public void setReal_exclusive(String real_exclusive) {
            this.real_exclusive = real_exclusive;
        }

        public String getIssue_date() {
            return issue_date;
        }

        public void setIssue_date(String issue_date) {
            this.issue_date = issue_date;
        }

        public String getLast_publish_date() {
            return last_publish_date;
        }

        public void setLast_publish_date(String last_publish_date) {
            this.last_publish_date = last_publish_date;
        }

        public String getSub_title() {
            return sub_title;
        }

        public void setSub_title(String sub_title) {
            this.sub_title = sub_title;
        }

        public String getV_image() {
            return v_image;
        }

        public void setV_image(String v_image) {
            this.v_image = v_image;
        }

        public String getH_image() {
            return h_image;
        }

        public void setH_image(String h_image) {
            this.h_image = h_image;
        }

        public String getVip_flag() {
            return vip_flag;
        }

        public void setVip_flag(String vip_flag) {
            this.vip_flag = vip_flag;
        }

        public String getAlternate_number() {
            return alternate_number;
        }

        public void setAlternate_number(String alternate_number) {
            this.alternate_number = alternate_number;
        }


        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }
    }

}
