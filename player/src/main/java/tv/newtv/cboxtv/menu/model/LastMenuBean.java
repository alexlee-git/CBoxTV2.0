package tv.newtv.cboxtv.menu.model;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TCP on 2018/4/17.
 */

public class LastMenuBean {

    /**
     * errorMessage : 成功
     * errorCode : 0
     * data : {"contentUUID":"guid值","MAMID":"newtv","contentType":"COPS","title":"[预]美国队长","subTitle":"美国队长","duration":"3600","videoType":"电影","videoClass":"科幻|动作","tags":"标签",
     * "vipFlag":"0","vipProductId":"ckkkkkkassak","movieLevel":"0","definition":"SD","vipNumber":"2","SortType":"1","playOrder":"1","premiereTime":"2019-01-02 00:03:09","premiereChannel":"首播频道",
     * "prize":"奖项","issueDate":"发行时间","leadingRole":"主角","audiences":"受众","presenter":"主持人","producer":"制片人","topic":"栏目","guest":"嘉宾","reporter":"记者","competition":"赛事",
     * "subject":"科目","classPeriod":"课时","singer":"歌手","screenwriter":"编剧","enName":"英文名称","area":"美国","director":"乔庄斯顿","actors":"克里斯埃文斯|海莉阿特维尔","language":"英语",
     * "airtime":"2011","hImage":"http://192.168.75.202:8081/newtv-2.0-epg/pic/A1.jpg","vImage":"http://192.168.75.202:8081/newtv-2.0-epvg/pic/B1.jpg",
     * "description":"故事讲述二次大战期间，主角史提芬罗杰斯想参军替美国打倒纳粹德军，可是他未能通过体能检查，于是他参加了军方一个秘密实验计划\u2014重生计划(Rebirth)，参加者会被改造成为超级战士，主角史提芬被改造後成为了唯一成功的实验品，自此之后， 他身穿红、白、蓝三色战斗服为国效力，他就成为了美国队长。",
     * "UUID":"","seriesSum":"3","grade":"7.5","lSuperScript":"","rSuperSctipt":"10000","lSubScript":"","rSubScript":"","cpCode":"CMS","stepSize":"",
     * "programs":[{"contentUUID":"guid值","contentType":"COPS","hImage":"http://192.168.75.202:8081/newtv-2.0-epg/pic/A1.jpg","vImage":"http://192.168.75.202:8081/newtv-2.0-epg/pic/B1.jpg",
     * "title":"冰川时代","subTitle":"冰川时代","actionType":"OPEN_VIDEO","actionUri":"","grade":"7.5","lSuperScript":"","rSuperSctipt":"10000","lSubScript":"","rSubScript":"","periods":"1","premiereTime":""}]}
     */

    private String errorMessage;
    private String errorCode;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * contentUUID : guid值
         * MAMID : newtv
         * contentType : COPS
         * title : [预]美国队长
         * subTitle : 美国队长
         * duration : 3600
         * videoType : 电影
         * videoClass : 科幻|动作
         * tags : 标签
         * vipFlag : 0
         * vipProductId : ckkkkkkassak
         * movieLevel : 0
         * definition : SD
         * vipNumber : 2
         * SortType : 1
         * playOrder : 1
         * premiereTime : 2019-01-02 00:03:09
         * premiereChannel : 首播频道
         * prize : 奖项
         * issueDate : 发行时间
         * leadingRole : 主角
         * audiences : 受众
         * presenter : 主持人
         * producer : 制片人
         * topic : 栏目
         * guest : 嘉宾
         * reporter : 记者
         * competition : 赛事
         * subject : 科目
         * classPeriod : 课时
         * singer : 歌手
         * screenwriter : 编剧
         * enName : 英文名称
         * area : 美国
         * director : 乔庄斯顿
         * actors : 克里斯埃文斯|海莉阿特维尔
         * language : 英语
         * airtime : 2011
         * hImage : http://192.168.75.202:8081/newtv-2.0-epg/pic/A1.jpg
         * vImage : http://192.168.75.202:8081/newtv-2.0-epvg/pic/B1.jpg
         * description : 故事讲述二次大战期间，主角史提芬罗杰斯想参军替美国打倒纳粹德军，可是他未能通过体能检查，于是他参加了军方一个秘密实验计划—重生计划(Rebirth)，参加者会被改造成为超级战士，主角史提芬被改造後成为了唯一成功的实验品，自此之后， 他身穿红、白、蓝三色战斗服为国效力，他就成为了美国队长。
         * UUID :
         * seriesSum : 3
         * grade : 7.5
         * lSuperScript :
         * rSuperSctipt : 10000
         * lSubScript :
         * rSubScript :
         * cpCode : CMS
         * stepSize :
         * programs : [{"contentUUID":"guid值","contentType":"COPS","hImage":"http://192.168.75.202:8081/newtv-2.0-epg/pic/A1.jpg","vImage":"http://192.168.75.202:8081/newtv-2.0-epg/pic/B1.jpg","title":"冰川时代","subTitle":"冰川时代","actionType":"OPEN_VIDEO","actionUri":"","grade":"7.5","lSuperScript":"","rSuperSctipt":"10000","lSubScript":"","rSubScript":"","periods":"1","premiereTime":""}]
         */

        private String contentUUID;
        private String MAMID;
        private String contentType;
        private String title;
        private String subTitle;
        private String duration;
        private String videoType;
        private String videoClass;
        private String tags;
        private String vipFlag;
        private String vipProductId;
        private String movieLevel;
        private String definition;
        private String vipNumber;
        private String SortType;
        private String playOrder;
        private String premiereTime;
        private String premiereChannel;
        private String prize;
        private String issueDate;
        private String leadingRole;
        private String audiences;
        private String presenter;
        private String producer;
        private String topic;
        private String guest;
        private String reporter;
        private String competition;
        private String subject;
        private String classPeriod;
        private String singer;
        private String screenwriter;
        private String enName;
        private String area;
        private String director;
        private String actors;
        private String language;
        private String airtime;
        private String hImage;
        private String vImage;
        private String description;
        private String UUID;
        private String seriesSum;
        private String grade;
        private String lSuperScript;
        private String rSuperSctipt;
        private String lSubScript;
        private String rSubScript;
        private String cpCode;
        private String stepSize;
        private List<Program> programs;

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
            return SortType;
        }

        public void setSortType(String SortType) {
            this.SortType = SortType;
        }

        public String getPlayOrder() {
            return playOrder;
        }

        public void setPlayOrder(String playOrder) {
            this.playOrder = playOrder;
        }

        public String getPremiereTime() {
            return premiereTime;
        }

        public void setPremiereTime(String premiereTime) {
            this.premiereTime = premiereTime;
        }

        public String getPremiereChannel() {
            return premiereChannel;
        }

        public void setPremiereChannel(String premiereChannel) {
            this.premiereChannel = premiereChannel;
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

        public String getHImage() {
            return hImage;
        }

        public void setHImage(String hImage) {
            this.hImage = hImage;
        }

        public String getVImage() {
            return vImage;
        }

        public void setVImage(String vImage) {
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

        public String getLSuperScript() {
            return lSuperScript;
        }

        public void setLSuperScript(String lSuperScript) {
            this.lSuperScript = lSuperScript;
        }

        public String getRSuperSctipt() {
            return rSuperSctipt;
        }

        public void setRSuperSctipt(String rSuperSctipt) {
            this.rSuperSctipt = rSuperSctipt;
        }

        public String getLSubScript() {
            return lSubScript;
        }

        public void setLSubScript(String lSubScript) {
            this.lSubScript = lSubScript;
        }

        public String getRSubScript() {
            return rSubScript;
        }

        public void setRSubScript(String rSubScript) {
            this.rSubScript = rSubScript;
        }

        public String getCpCode() {
            return cpCode;
        }

        public void setCpCode(String cpCode) {
            this.cpCode = cpCode;
        }

        public String getStepSize() {
            return stepSize;
        }

        public void setStepSize(String stepSize) {
            this.stepSize = stepSize;
        }

        public List<Program> getPrograms() {
            return programs;
        }

        public void setPrograms(List<Program> programs) {
            this.programs = programs;
        }

        public Content convertProgramSeriesInfo() {

            Content result = new Content();
            result.setContentUUID(contentUUID);
            result.setMAMID(MAMID);
            result.setContentType(contentType);
            result.setTitle(title);
            result.setSubTitle(subTitle);
            result.setDuration(duration);
            result.setVideoType(videoType);
            result.setVideoClass(videoClass);
//            result.setChannelId(channelId);
            result.setTags(tags);
            result.setVipFlag(vipFlag);
            result.setVipProductId(vipProductId);
            result.setMovieLevel(movieLevel);
            result.setDefinition(definition);
//            result.setVipNumber(vipNumber);
            result.setSortType(SortType);
            result.setPlayOrder(playOrder);
//            result.setPermiereTime(premiereTime);
//            result.setPermiereChannel(premiereChannel);
//            result.setPrize(prize);
//            result.setIssueDate(issueDate);
//            result.setLeadingRole(leadingRole);
//            result.setAudiences(audiences);
            result.setPresenter(presenter);
//            result.setProducer(producer);
//            result.setTopic(topic);
//            result.setGuest(guest);
//            result.setReporter(reporter);
//            result.setCompetition(competition);
//            result.setSubject(subject);
//            result.setClassPeriod(classPeriod);
//            result.setSinger(singer);
//            result.setScreenwriter(screenwriter);
            result.setEnName(enName);
            result.setArea(area);
            result.setDirector(director);
            result.setActors(actors);
            result.setLanguage(language);
            result.setAirtime(airtime);
            result.setHImage(hImage);
            result.setVImage(vImage);
            result.setDescription(description);
            result.setContentUUID(UUID);
            result.setSeriesSum(seriesSum);
            result.setGrade(grade);
//            result.setlSuperScript(lSuperScript);
//            result.setrSuperScript(rSuperSctipt);
//            result.setlSubScript(lSubScript);
//            result.setrSubScript(rSubScript);
//            result.setCpCode(cpCode);
//            result.setStepSize(stepSize);
            List<SubContent> data = new ArrayList<>();
            int size = programs.size();
            for (int i = 0; i < size; i++) {
                data.add(programs.get(i).convertProgramsInfo());
            }
            result.setData(data);
            return result;
        }
    }
}
