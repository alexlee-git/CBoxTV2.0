package tv.newtv.cboxtv.player;

import com.newtv.cms.bean.CdnUrl;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/2/5 0005.
 * 单节目
 */

public class ChkPlayResult implements Serializable {

    private static final long serialVersionUID = -2062188526961737431L;
    private String contentUUID;       //内容ID
    private String MAMID;
    private String title;
    private String contentType;     //内容类型
    private String contentUrl;     //视频播放地址
    private String subTitle;
    private String duration;
    private String videoType;      //电影
    private String videoClass;
    private String tags;
    private String vipFlag;
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
    private String cpCode;           //内容厂商编码
    private String videoSize;        //视频尺寸
    private String freeDuration;   //试播时长
    private String bitrateStream;   // 码流
    private String periods;          // 集号
    private String programSeriesUUIDs;          // 节目集id
    private String categoryIds;          // 栏目id  cctv_colum_cctv/CCTV-4/
    private List<CdnUrl> data;

    private boolean encryptFlag;
    private String decryptKey;

    public ChkPlayResult() {
    }

    public ChkPlayResult(String contentUUID, String MAMID, String title, String contentType,
                         String contentUrl, String subTitle, String duration, String
                                     videoType, String videoClass, String tags, String vipFlag,
                         String vipProductId, String movieLevel, String definition, String
                                     vipNumber, String sortType, String playOrder, String
                                     permiereTime, String permiereChannel, String prize, String
                                     issueDate, String leadingRole, String audiences, String
                                     presenter, String producer, String topic, String guest,
                         String reporter, String competition, String subject, String
                                     classPeriod, String singer, String screenwriter, String
                                     enName, String area, String director, String actors, String
                                     language, String airtime, String hImage, String vImage,
                         String description, String UUID, String seriesSum, String grade,
                         String lSuperScript, String rSuperScript, String lSubScript, String
                                     rSubScript, String cpCode, String videoSize, String
                                     freeDuration, String bitrateStream, String periods,
                         List<CdnUrl> data) {
        this.contentUUID = contentUUID;
        this.MAMID = MAMID;
        this.title = title;
        this.contentType = contentType;
        this.contentUrl = contentUrl;
        this.subTitle = subTitle;
        this.duration = duration;
        this.videoType = videoType;
        this.videoClass = videoClass;
        this.tags = tags;
        this.vipFlag = vipFlag;
        this.vipProductId = vipProductId;
        this.movieLevel = movieLevel;
        this.definition = definition;
        this.vipNumber = vipNumber;
        this.sortType = sortType;
        this.playOrder = playOrder;
        this.permiereTime = permiereTime;
        this.permiereChannel = permiereChannel;
        this.prize = prize;
        this.issueDate = issueDate;
        this.leadingRole = leadingRole;
        this.audiences = audiences;
        this.presenter = presenter;
        this.producer = producer;
        this.topic = topic;
        this.guest = guest;
        this.reporter = reporter;
        this.competition = competition;
        this.subject = subject;
        this.classPeriod = classPeriod;
        this.singer = singer;
        this.screenwriter = screenwriter;
        this.enName = enName;
        this.area = area;
        this.director = director;
        this.actors = actors;
        this.language = language;
        this.airtime = airtime;
        this.hImage = hImage;
        this.vImage = vImage;
        this.description = description;
        this.UUID = UUID;
        this.seriesSum = seriesSum;
        this.grade = grade;
        this.lSuperScript = lSuperScript;
        this.rSuperScript = rSuperScript;
        this.lSubScript = lSubScript;
        this.rSubScript = rSubScript;
        this.cpCode = cpCode;
        this.videoSize = videoSize;
        this.freeDuration = freeDuration;
        this.bitrateStream = bitrateStream;
        this.periods = periods;
        this.data = data;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getProgramSeriesUUIDs() {
        return programSeriesUUIDs;
    }

    public void setProgramSeriesUUIDs(String programSeriesUUIDs) {
        this.programSeriesUUIDs = programSeriesUUIDs;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
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

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
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

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getFreeDuration() {
        return freeDuration;
    }

    public void setFreeDuration(String freeDuration) {
        this.freeDuration = freeDuration;
    }

    public String getBitrateStream() {
        return bitrateStream;
    }

    public void setBitrateStream(String bitrateStream) {
        this.bitrateStream = bitrateStream;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public List<CdnUrl> getData() {
        return data;
    }

    public void setData(List<CdnUrl> data) {
        this.data = data;
    }


    public boolean getEncryptFlag() {
        return encryptFlag;
    }

    public void setEncryptFlag(boolean encryptFlag) {
        this.encryptFlag = encryptFlag;
    }

    public String getDecryptKey() {
        return decryptKey;
    }

    public void setDecryptKey(String decryptKey) {
        this.decryptKey = decryptKey;
    }

    @Override
    public String toString() {
        return "ProgramDetailInfo{" +
                "contentUUID='" + contentUUID + '\'' +
                ", MAMID='" + MAMID + '\'' +
                ", contentType='" + contentType + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", duration='" + duration + '\'' +
                ", videoType='" + videoType + '\'' +
                ", videoClass='" + videoClass + '\'' +
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
                ", videoSize='" + videoSize + '\'' +
                ", freeDuration='" + freeDuration + '\'' +
                ", bitrateStream='" + bitrateStream + '\'' +
                ", periods='" + periods + '\'' +
                ", programSeriesUUIDs='" + programSeriesUUIDs + '\'' +
                ", data=" + data +
                '}';
    }
}
