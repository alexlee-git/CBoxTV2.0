package tv.newtv.cboxtv.uc.v2.data;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/21 0021
 */
public class History {

    private String user_id;               //用户id
    private String channel_code;         //渠道编号
    private String app_key;               //app编号
    private String programset_id;        //节目ID
    private String programset_name;      //节目名称
    private String is_program;            //视频类型,是否为节目集(0-节目集，1-普通节目)
    private String poster;                //海报url
    private String program_progress;     //播放进度
    private String user_name;             //用户昵称
    private String program_dur;           //影片时长
    private String program_watch_dur;    //视频观看时长
    private String is_panda;              //是否来自熊猫渠道,是:true;不是false
    private String check_record;          //检查数据记录,是:true;不是false,(如果传递true,如果已有记录,则更新已有记录,如果没有记录,会新增记录)
    private String program_child_id;     //自视频ID
    private String score;                  //评分
    private String video_type;            //节目一级分类
    private String total_count;           //节目总集数
    private String superscript;           //角标id
    private String content_type;          //节目类型
    private String program_watch_date;   //观看日期

    public History(String user_id, String channel_code, String app_key, String programset_id, String programset_name, String is_program, String poster, String program_progress, String user_name, String program_dur, String program_watch_dur, String is_panda, String check_record, String program_child_id, String score, String video_type, String total_count, String superscript, String content_type, String program_watch_date) {
        this.user_id = user_id;
        this.channel_code = channel_code;
        this.app_key = app_key;
        this.programset_id = programset_id;
        this.programset_name = programset_name;
        this.is_program = is_program;
        this.poster = poster;
        this.program_progress = program_progress;
        this.user_name = user_name;
        this.program_dur = program_dur;
        this.program_watch_dur = program_watch_dur;
        this.is_panda = is_panda;
        this.check_record = check_record;
        this.program_child_id = program_child_id;
        this.score = score;
        this.video_type = video_type;
        this.total_count = total_count;
        this.superscript = superscript;
        this.content_type = content_type;
        this.program_watch_date = program_watch_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChannel_code() {
        return channel_code;
    }

    public void setChannel_code(String channel_code) {
        this.channel_code = channel_code;
    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getProgramset_id() {
        return programset_id;
    }

    public void setProgramset_id(String programset_id) {
        this.programset_id = programset_id;
    }

    public String getProgramset_name() {
        return programset_name;
    }

    public void setProgramset_name(String programset_name) {
        this.programset_name = programset_name;
    }

    public String getIs_program() {
        return is_program;
    }

    public void setIs_program(String is_program) {
        this.is_program = is_program;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getProgram_progress() {
        return program_progress;
    }

    public void setProgram_progress(String program_progress) {
        this.program_progress = program_progress;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProgram_dur() {
        return program_dur;
    }

    public void setProgram_dur(String program_dur) {
        this.program_dur = program_dur;
    }

    public String getProgram_watch_dur() {
        return program_watch_dur;
    }

    public void setProgram_watch_dur(String program_watch_dur) {
        this.program_watch_dur = program_watch_dur;
    }

    public String getIs_panda() {
        return is_panda;
    }

    public void setIs_panda(String is_panda) {
        this.is_panda = is_panda;
    }

    public String getCheck_record() {
        return check_record;
    }

    public void setCheck_record(String check_record) {
        this.check_record = check_record;
    }

    public String getProgram_child_id() {
        return program_child_id;
    }

    public void setProgram_child_id(String program_child_id) {
        this.program_child_id = program_child_id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public String getSuperscript() {
        return superscript;
    }

    public void setSuperscript(String superscript) {
        this.superscript = superscript;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getProgram_watch_date() {
        return program_watch_date;
    }

    public void setProgram_watch_date(String program_watch_date) {
        this.program_watch_date = program_watch_date;
    }

    @Override
    public String toString() {
        return "History{" +
                "user_id='" + user_id + '\'' +
                ", channel_code='" + channel_code + '\'' +
                ", app_key='" + app_key + '\'' +
                ", programset_id='" + programset_id + '\'' +
                ", programset_name='" + programset_name + '\'' +
                ", is_program='" + is_program + '\'' +
                ", poster='" + poster + '\'' +
                ", program_progress='" + program_progress + '\'' +
                ", user_name='" + user_name + '\'' +
                ", program_dur='" + program_dur + '\'' +
                ", program_watch_dur='" + program_watch_dur + '\'' +
                ", is_panda='" + is_panda + '\'' +
                ", check_record='" + check_record + '\'' +
                ", program_child_id='" + program_child_id + '\'' +
                ", score='" + score + '\'' +
                ", video_type='" + video_type + '\'' +
                ", total_count='" + total_count + '\'' +
                ", superscript='" + superscript + '\'' +
                ", content_type='" + content_type + '\'' +
                ", program_watch_date='" + program_watch_date + '\'' +
                '}';
    }
}
