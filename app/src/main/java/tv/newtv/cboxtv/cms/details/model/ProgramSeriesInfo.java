package tv.newtv.cboxtv.cms.details.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/2/1 0001.
 * 节目集
 */

public class ProgramSeriesInfo implements Serializable {
    // add by lxf
    public static final String TYPE_LOOP_PER_DAY = "1";       // 直播循环类型： 每日直播
    public static final String TYPE_LOOP_PER_WEEK = "2";       // 直播循环类型： 每周直播     liveParam = “1|2|5” 表示周一|周二|周五直播
    public static final String TYPE_LOOP_ONCE = "3";       // 直播循环类型： 一次直播     liveParam = “2018-05-01”
    private String liveUrl = "";
    private String playUrl;
//    private String liveUrl = "http://s003.test.vod06.icntvcdn.com/live/sscntv63.m3u8";      // 直播地址
    //    private String     liveUrl             = "http://172.25.5.20/live/368066e26450484c80fca6c23fd2d77e/1d73344c100c44949112a43c0e65a2f8.m3u8";      // 直播地址
//    private String     liveUrl             = "http://live1.cloud.ottcn.com/live/368066e26450484c80fca6c23fd2d77e/1d73344c100c44949112a43c0e65a2f8-1.m3u8";      // 直播地址
//    public String     liveUrl             = "http://live1.cloud.ottcn.com/live/368066e26450484c80fca6c23fd2d77e/1d73344c100c44949112a43c0e65a2f8-1.m3u8?timeshift=-4140";      // 直播地址
    private String liveParam = "";      // 直播循环参数
    private String liveLoopType = "1";       // 直播循环类型
    private String playStartTime = "";      // 直播开始时间   19:00:00
    private String playEndTime = "";      // 直播结束时间   19:30:00
    /**
     * 是否时移 1为是，其他为否
     */
    private String isTimeShift;

    public String getIsTimeShift() {
        return isTimeShift;
    }

    public void setIsTimeShift(String isTimeShift) {
        this.isTimeShift = isTimeShift;
    }

    //1d73344c100c44949112a43c0e65a2f8-1/201804300513/timeShiftFlag_52_12.000_5626.ts
    //http://live1.cloud.ottcn.com/live/368066e26450484c80fca6c23fd2d77e/1d73344c100c44949112a43c0e65a2f8-1/201804300513/timeShiftFlag_52_12.000_5626.ts

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getLiveParam() {
        return liveParam;
    }

    public void setLiveParam(String liveParam) {
        this.liveParam = liveParam;
    }

    public String getLiveLoopType() {
        return liveLoopType;
    }

    public void setLiveLoopType(String liveLoopType) {
        this.liveLoopType = liveLoopType;
    }

    public String getPlayStartTime() {
        return playStartTime;
    }

    public void setPlayStartTime(String playStartTime) {
        this.playStartTime = playStartTime;
    }

    public String getPlayEndTime() {
        return playEndTime;
    }

    public void setPlayEndTime(String playEndTime) {
        this.playEndTime = playEndTime;
    }

    private static final long serialVersionUID = -5025335829144041525L;
    private String contentUUID;       //内容ID
    private String MAMID;
    private String contentType;     //内容类型
    private String title;
    private String subTitle;
    private String duration;
    private String videoType;      //电影
    private String videoClass;
    private String channelId;
    private String tags;
    private String vipFlag;//0 免费；1 VIP免费；2 单点付费
    private String vipProductId;
    private String movieLevel;     //1正片 2预告片 3花絮
    private String definition;     //SD标清  HD高清
    private String vipNumber;      //最新付费节目
    private String sortType;       //排序方式
    private String playOrder;      //播放顺序
    private String permiereTime;   //首播时间
    private String permiereChannel;//首播频道
    private String prize;          //奖项
    private String issueDate;      //发行时间
    private String leadingRole;    //主角
    private String audiences;      //受众
    private String presenter;      //主持人
    private String producer;       //制片人
    private String topic;          //栏目
    private String guest;          //嘉宾
    private String reporter;       //记者
    private String competition;    //赛事
    private String subject;        //科目
    private String classPeriod;    //课时
    private String singer;         //歌手
    private String screenwriter;   // 编剧
    private String enName;         //英文名称
    private String area;           //国家地区
    private String director;       //导演 "|"
    private String actors;         //主演 "|"
    private String language;       //语言
    private String airtime;        //年代
    private String hImage;         //横海报
    private String vImage;
    private String description;     //描述
    private String UUID;            //源系统ID，同cms1.0中的outsourceid，腾讯cid或vid
    private String seriesSum;       //总集数
    private String grade;           //评分
    private String lSuperScript;    //左上角标ID
    private String rSuperScript;    //右上角标ID
    private String lSubScript;      //左下角标ID
    private String rSubScript;      //右下角标ID
    private String cpCode;          //内容厂商编码
    private String stepSize;        //分组步长
    public String discription;        //s人物简介
    public String district;        //地区
    public String country;        //国家
    public String layoutTitle;//列表标题
    public int layoutId;//布局id
    public boolean isCollect;
    public boolean isAttention;
    @SerializedName("programs")
    private List<ProgramsInfo> data;

    private String programSeriesUUIDs;

    public String getProgramSeriesUUIDs() {
        return programSeriesUUIDs;
    }

    public void setProgramSeriesUUIDs(String programSeriesUUIDs) {
        this.programSeriesUUIDs = programSeriesUUIDs;
    }

    public ProgramSeriesInfo() {
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

    public List<ProgramsInfo> getData() {
        return data;
    }

    public void setData(List<ProgramsInfo> data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideoType() {
        return videoType;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoClass() {
        return videoClass;
    }

    public void setVideoClass(String videoClass) {
        this.videoClass = videoClass;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(String vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getVipProductId() {
        return vipProductId;
    }

    public void setVipProductId(String vipProductId) {
        this.vipProductId = vipProductId;
    }

    public String getMovieLevel() {
        return movieLevel;
    }

    public void setMovieLevel(String movieLevel) {
        this.movieLevel = movieLevel;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getVipNumber() {
        return vipNumber;
    }

    public void setVipNumber(String vipNumber) {
        this.vipNumber = vipNumber;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(String playOrder) {
        this.playOrder = playOrder;
    }

    public String getPermiereTime() {
        return permiereTime;
    }

    public void setPermiereTime(String permiereTime) {
        this.permiereTime = permiereTime;
    }

    public String getPermiereChannel() {
        return permiereChannel;
    }

    public void setPermiereChannel(String permiereChannel) {
        this.permiereChannel = permiereChannel;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getLeadingRole() {
        return leadingRole;
    }

    public void setLeadingRole(String leadingRole) {
        this.leadingRole = leadingRole;
    }

    public String getAudiences() {
        return audiences;
    }

    public void setAudiences(String audiences) {
        this.audiences = audiences;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(String classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(String screenwriter) {
        this.screenwriter = screenwriter;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAirtime() {
        return airtime;
    }

    public void setAirtime(String airtime) {
        this.airtime = airtime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getSeriesSum() {
        return seriesSum;
    }

    public void setSeriesSum(String seriesSum) {
        this.seriesSum = seriesSum;
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

    public String getStepSize() {
        return stepSize;
    }

    public void setStepSize(String stepSize) {
        this.stepSize = stepSize;
    }

    public void resolveVip(){
        if(getData() != null && getData().size() > 0){
            if(getVipFlag() != null && !getVipFlag().equals("0")){

                if(TextUtils.isEmpty(vipNumber)){
                    for(ProgramsInfo info : getData()){
                        info.setVipFlag(getVipFlag());
                    }
                } else {
                    int number = Integer.parseInt(vipNumber);
                    int min = Math.min(number,getData().size());
                    int temp = 0;
                    for(int i=0;i<min;i++){
                        if("0".equals(sortType)){
                            temp = getData().size() - i;
                        } else {
                            temp = i;
                        }
                        getData().get(temp).setVipFlag(vipFlag);
                    }
                }

            }
        }
    }

    public static class ProgramsInfo implements Serializable {

        private static final long serialVersionUID = 3461360387779387505L;
        private String contentUUID;
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
        private String des;         //集号
        private String liveUrl;
        private String playUrl;
        private String seriesSubUUID;
        private String playStartTime;
        private String playEndTime;
        //是否从栏目树观看历史进入的，如果是鉴权的AlbumId传seriesSubUUID
        private boolean isMenuGroupHistory;

        private String vipFlag;

        public ProgramsInfo() {
        }

        public ProgramsInfo(String contentUUID, String title, String contentType, String hImage, String vImage, String subTitle, String actionType, String actionUri, String grade, String lSuperScript, String rSuperScript, String lSubScript, String rSubScript, String periods, String des) {
            this.contentUUID = contentUUID;
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
            this.des = des;
        }

        public String getVipFlag() {
            return vipFlag;
        }

        public void setVipFlag(String vipFlag) {
            this.vipFlag = vipFlag;
        }

        public boolean isMenuGroupHistory() {
            return isMenuGroupHistory;
        }

        public void setMenuGroupHistory(boolean menuGroupHistory) {
            isMenuGroupHistory = menuGroupHistory;
        }

        public String getPlayStartTime() {
            return playStartTime;
        }

        public void setPlayStartTime(String playStartTime) {
            this.playStartTime = playStartTime;
        }

        public String getPlayEndTime() {
            return playEndTime;
        }

        public void setPlayEndTime(String playEndTime) {
            this.playEndTime = playEndTime;
        }

        public String getSeriesSubUUID() {
            return seriesSubUUID;
        }

        public void setSeriesSubUUID(String seriesSubUUID) {
            this.seriesSubUUID = seriesSubUUID;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getContentUUID() {
            return contentUUID;
        }

        public void setContentUUID(String contentUUID) {
            this.contentUUID = contentUUID;
        }

        public String getActionUri() {
            return actionUri;
        }

        public void setActionUri(String actionUri) {
            this.actionUri = actionUri;
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

        public String getLiveUrl() {
            return liveUrl;
        }

        public void setLiveUrl(String liveUrl) {
            this.liveUrl = liveUrl;
        }

        public void setPeriods(String periods) {
            this.periods = periods;
        }

        @Override
        public String toString() {
            return "ProgramsInfo{" +
                    "contentUUID='" + contentUUID + '\'' +
                    ", title='" + title + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", hImage='" + hImage + '\'' +
                    ", vImage='" + vImage + '\'' +
                    ", subTitle='" + subTitle + '\'' +
                    ", actionType='" + actionType + '\'' +
                    ", actionUri='" + actionUri + '\'' +
                    ", grade='" + grade + '\'' +
                    ", lSuperScript='" + lSuperScript + '\'' +
                    ", rSuperScript='" + rSuperScript + '\'' +
                    ", lSubScript='" + lSubScript + '\'' +
                    ", rSubScript='" + rSubScript + '\'' +
                    ", seriesSubUUID='" + seriesSubUUID + '\'' +
                    ", periods='" + periods + '\'' +
                    '}';
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "ProgramSeriesInfo{" +
                "liveUrl='" + liveUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", liveParam='" + liveParam + '\'' +
                ", liveLoopType='" + liveLoopType + '\'' +
                ", playStartTime='" + playStartTime + '\'' +
                ", playEndTime='" + playEndTime + '\'' +
                ", isTimeShift='" + isTimeShift + '\'' +
                ", contentUUID='" + contentUUID + '\'' +
                ", MAMID='" + MAMID + '\'' +
                ", contentType='" + contentType + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", duration='" + duration + '\'' +
                ", videoType='" + videoType + '\'' +
                ", videoClass='" + videoClass + '\'' +
                ", channelId='" + channelId + '\'' +
                ", tags='" + tags + '\'' +
                ", vipFlag='" + vipFlag + '\'' +
                ", vipProductId='" + vipProductId + '\'' +
                ", movieLevel='" + movieLevel + '\'' +
                ", definition='" + definition + '\'' +
                ", vipNumber='" + vipNumber + '\'' +
                ", sortType='" + sortType + '\'' +
                ", playOrder='" + playOrder + '\'' +
                ", permiereTime='" + permiereTime + '\'' +
                ", permiereChannel='" + permiereChannel + '\'' +
                ", prize='" + prize + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", leadingRole='" + leadingRole + '\'' +
                ", audiences='" + audiences + '\'' +
                ", presenter='" + presenter + '\'' +
                ", producer='" + producer + '\'' +
                ", topic='" + topic + '\'' +
                ", guest='" + guest + '\'' +
                ", reporter='" + reporter + '\'' +
                ", competition='" + competition + '\'' +
                ", subject='" + subject + '\'' +
                ", classPeriod='" + classPeriod + '\'' +
                ", singer='" + singer + '\'' +
                ", screenwriter='" + screenwriter + '\'' +
                ", enName='" + enName + '\'' +
                ", area='" + area + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", language='" + language + '\'' +
                ", airtime='" + airtime + '\'' +
                ", hImage='" + hImage + '\'' +
                ", vImage='" + vImage + '\'' +
                ", description='" + description + '\'' +
                ", UUID='" + UUID + '\'' +
                ", seriesSum='" + seriesSum + '\'' +
                ", grade='" + grade + '\'' +
                ", lSuperScript='" + lSuperScript + '\'' +
                ", rSuperScript='" + rSuperScript + '\'' +
                ", lSubScript='" + lSubScript + '\'' +
                ", rSubScript='" + rSubScript + '\'' +
                ", cpCode='" + cpCode + '\'' +
                ", stepSize='" + stepSize + '\'' +
                ", discription='" + discription + '\'' +
                ", district='" + district + '\'' +
                ", country='" + country + '\'' +
                ", layoutTitle='" + layoutTitle + '\'' +
                ", layoutId=" + layoutId +
                ", isCollect=" + isCollect +
                ", isAttention=" + isAttention +
                ", data=" + data +
                ", programSeriesUUIDs='" + programSeriesUUIDs + '\'' +
                '}';
    }
}
