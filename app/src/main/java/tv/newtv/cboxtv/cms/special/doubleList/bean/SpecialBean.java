package tv.newtv.cboxtv.cms.special.doubleList.bean;

import java.util.List;
import java.util.Objects;

public class SpecialBean {

    /**
     * data : {"subject":"","competition":"","language":"普通话","CSUUIDs":"","subTitle":"","vImage":"http://img.cloud.ottcn.com:8080/n3images/2018/07/19/a92773fb7f194bbda8f7bd874a8dbaac_1531963477144.jpg","vipNumber":"","UUID":"84198","contentType":"PS","lSubScript":null,"area":"中国大陆","lSuperscript":null,"presenter":"","videoType":"纪录片","director":"无","realExclusive":null,"tags":"历史 纪实","actors":"无","grade":null,"premiereChannel":null,"producer":"贺亚莉","topic":"国家记忆","guest":"","programs":[{"lSuperscript":null,"rSubScript":null,"actionUri":"","title":"第一集 极地使命","seriesSubUUID":"84198","MAMID":"wuxi","actionType":"","subTitle":"","rSupersctipt":null,"movieLevel":"1","vImage":"","grade":null,"periods":"1","contentUUID":"d98611078cc242d690c1d571abbcae79","hImage":"http://img.cloud.ottcn.com:8080/n3images/2018/07/19/87154_0_20180719-00-54-15-994_14040162-5b76-4745-9b97-d71de6045278_d98611078cc242d690c1d571abbcae79_H2642500000aac128.jpg","contentType":"CP","lSubScript":null,"drm":null}],"videoClass":"历史","vipFlag":"0","cpCode":"NEWTV","singer":"","stepSize":"","description":"新中国登山事业艰难起步，中国登山队如何面对质疑？苏联为何违背协议，放弃联合登山计划？贺龙主张单独登峰，一向慎重的周恩来给出了怎样的答复？\n","title":"中国首登珠峰","prize":"","playOrder":"1","MAMID":"wuxi","duration":"0","is4k":"0","leadingRole":"无","movieLevel":"1","seriesSum":null,"enName":"","audiences":"","definition":"HD","vipProductId":"","issueDate":"","screenwriter":"","rSubScript":null,"reporter":"","classPeriod":"0","airtime":"2018","rSupersctipt":null,"sortType":"","contentUUID":"84198","premiereTime":"","hImage":"http://img.cloud.ottcn.com:8080/n3images/2018/07/19/a92773fb7f194bbda8f7bd874a8dbaac_1531963477144.jpg"}
     * errorMessage : 成功
     * errorCode : 0
     */

    private DataBean data;
    private String errorMessage;
    private String errorCode;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialBean that = (SpecialBean) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(errorCode, that.errorCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(data, errorMessage, errorCode);
    }

    @Override
    public String toString() {
        return "SpecialBean{" +
                "data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }

    public static class DataBean {
        /**
         * subject :
         * competition :
         * language : 普通话
         * CSUUIDs :
         * subTitle :
         * vImage : http://img.cloud.ottcn.com:8080/n3images/2018/07/19/a92773fb7f194bbda8f7bd874a8dbaac_1531963477144.jpg
         * vipNumber :
         * UUID : 84198
         * contentType : PS
         * lSubScript : null
         * area : 中国大陆
         * lSuperscript : null
         * presenter :
         * videoType : 纪录片
         * director : 无
         * realExclusive : null
         * tags : 历史 纪实
         * actors : 无
         * grade : null
         * premiereChannel : null
         * producer : 贺亚莉
         * topic : 国家记忆
         * guest :
         * programs : [{"lSuperscript":null,"rSubScript":null,"actionUri":"","title":"第一集 极地使命","seriesSubUUID":"84198","MAMID":"wuxi","actionType":"","subTitle":"","rSupersctipt":null,"movieLevel":"1","vImage":"","grade":null,"periods":"1","contentUUID":"d98611078cc242d690c1d571abbcae79","hImage":"http://img.cloud.ottcn.com:8080/n3images/2018/07/19/87154_0_20180719-00-54-15-994_14040162-5b76-4745-9b97-d71de6045278_d98611078cc242d690c1d571abbcae79_H2642500000aac128.jpg","contentType":"CP","lSubScript":null,"drm":null}]
         * videoClass : 历史
         * vipFlag : 0
         * cpCode : NEWTV
         * singer :
         * stepSize :
         * description : 新中国登山事业艰难起步，中国登山队如何面对质疑？苏联为何违背协议，放弃联合登山计划？贺龙主张单独登峰，一向慎重的周恩来给出了怎样的答复？

         * title : 中国首登珠峰
         * prize :
         * playOrder : 1
         * MAMID : wuxi
         * duration : 0
         * is4k : 0
         * leadingRole : 无
         * movieLevel : 1
         * seriesSum : null
         * enName :
         * audiences :
         * definition : HD
         * vipProductId :
         * issueDate :
         * screenwriter :
         * rSubScript : null
         * reporter :
         * classPeriod : 0
         * airtime : 2018
         * rSupersctipt : null
         * sortType :
         * contentUUID : 84198
         * premiereTime :
         * hImage : http://img.cloud.ottcn.com:8080/n3images/2018/07/19/a92773fb7f194bbda8f7bd874a8dbaac_1531963477144.jpg
         */

        private String subject;
        private String competition;
        private String language;
        private String CSUUIDs;
        private String subTitle;
        private String vImage;
        private String vipNumber;
        private String UUID;
        private String contentType;
        private Object lSubScript;
        private String area;
        private Object lSuperscript;
        private String presenter;
        private String videoType;
        private String director;
        private Object realExclusive;
        private String tags;
        private String actors;
        private Object grade;
        private Object premiereChannel;
        private String producer;
        private String topic;
        private String guest;
        private String videoClass;
        private String vipFlag;
        private String cpCode;
        private String singer;
        private String stepSize;
        private String description;
        private String title;
        private String prize;
        private String playOrder;
        private String MAMID;
        private String duration;
        private String is4k;
        private String leadingRole;
        private String movieLevel;
        private Object seriesSum;
        private String enName;
        private String audiences;
        private String definition;
        private String vipProductId;
        private String issueDate;
        private String screenwriter;
        private Object rSubScript;
        private String reporter;
        private String classPeriod;
        private String airtime;
        private Object rSupersctipt;
        private String sortType;
        private String contentUUID;
        private String premiereTime;
        private String hImage;
        private List<ProgramsBean> programs;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getCompetition() {
            return competition;
        }

        public void setCompetition(String competition) {
            this.competition = competition;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCSUUIDs() {
            return CSUUIDs;
        }

        public void setCSUUIDs(String CSUUIDs) {
            this.CSUUIDs = CSUUIDs;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getVImage() {
            return vImage;
        }

        public void setVImage(String vImage) {
            this.vImage = vImage;
        }

        public String getVipNumber() {
            return vipNumber;
        }

        public void setVipNumber(String vipNumber) {
            this.vipNumber = vipNumber;
        }

        public String getUUID() {
            return UUID;
        }

        public void setUUID(String UUID) {
            this.UUID = UUID;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Object getLSubScript() {
            return lSubScript;
        }

        public void setLSubScript(Object lSubScript) {
            this.lSubScript = lSubScript;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public Object getLSuperscript() {
            return lSuperscript;
        }

        public void setLSuperscript(Object lSuperscript) {
            this.lSuperscript = lSuperscript;
        }

        public String getPresenter() {
            return presenter;
        }

        public void setPresenter(String presenter) {
            this.presenter = presenter;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public Object getRealExclusive() {
            return realExclusive;
        }

        public void setRealExclusive(Object realExclusive) {
            this.realExclusive = realExclusive;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getActors() {
            return actors;
        }

        public void setActors(String actors) {
            this.actors = actors;
        }

        public Object getGrade() {
            return grade;
        }

        public void setGrade(Object grade) {
            this.grade = grade;
        }

        public Object getPremiereChannel() {
            return premiereChannel;
        }

        public void setPremiereChannel(Object premiereChannel) {
            this.premiereChannel = premiereChannel;
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

        public String getVideoClass() {
            return videoClass;
        }

        public void setVideoClass(String videoClass) {
            this.videoClass = videoClass;
        }

        public String getVipFlag() {
            return vipFlag;
        }

        public void setVipFlag(String vipFlag) {
            this.vipFlag = vipFlag;
        }

        public String getCpCode() {
            return cpCode;
        }

        public void setCpCode(String cpCode) {
            this.cpCode = cpCode;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getStepSize() {
            return stepSize;
        }

        public void setStepSize(String stepSize) {
            this.stepSize = stepSize;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }

        public String getPlayOrder() {
            return playOrder;
        }

        public void setPlayOrder(String playOrder) {
            this.playOrder = playOrder;
        }

        public String getMAMID() {
            return MAMID;
        }

        public void setMAMID(String MAMID) {
            this.MAMID = MAMID;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getIs4k() {
            return is4k;
        }

        public void setIs4k(String is4k) {
            this.is4k = is4k;
        }

        public String getLeadingRole() {
            return leadingRole;
        }

        public void setLeadingRole(String leadingRole) {
            this.leadingRole = leadingRole;
        }

        public String getMovieLevel() {
            return movieLevel;
        }

        public void setMovieLevel(String movieLevel) {
            this.movieLevel = movieLevel;
        }

        public Object getSeriesSum() {
            return seriesSum;
        }

        public void setSeriesSum(Object seriesSum) {
            this.seriesSum = seriesSum;
        }

        public String getEnName() {
            return enName;
        }

        public void setEnName(String enName) {
            this.enName = enName;
        }

        public String getAudiences() {
            return audiences;
        }

        public void setAudiences(String audiences) {
            this.audiences = audiences;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getVipProductId() {
            return vipProductId;
        }

        public void setVipProductId(String vipProductId) {
            this.vipProductId = vipProductId;
        }

        public String getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(String issueDate) {
            this.issueDate = issueDate;
        }

        public String getScreenwriter() {
            return screenwriter;
        }

        public void setScreenwriter(String screenwriter) {
            this.screenwriter = screenwriter;
        }

        public Object getRSubScript() {
            return rSubScript;
        }

        public void setRSubScript(Object rSubScript) {
            this.rSubScript = rSubScript;
        }

        public String getReporter() {
            return reporter;
        }

        public void setReporter(String reporter) {
            this.reporter = reporter;
        }

        public String getClassPeriod() {
            return classPeriod;
        }

        public void setClassPeriod(String classPeriod) {
            this.classPeriod = classPeriod;
        }

        public String getAirtime() {
            return airtime;
        }

        public void setAirtime(String airtime) {
            this.airtime = airtime;
        }

        public Object getRSupersctipt() {
            return rSupersctipt;
        }

        public void setRSupersctipt(Object rSupersctipt) {
            this.rSupersctipt = rSupersctipt;
        }

        public String getSortType() {
            return sortType;
        }

        public void setSortType(String sortType) {
            this.sortType = sortType;
        }

        public String getContentUUID() {
            return contentUUID;
        }

        public void setContentUUID(String contentUUID) {
            this.contentUUID = contentUUID;
        }

        public String getPremiereTime() {
            return premiereTime;
        }

        public void setPremiereTime(String premiereTime) {
            this.premiereTime = premiereTime;
        }

        public String getHImage() {
            return hImage;
        }

        public void setHImage(String hImage) {
            this.hImage = hImage;
        }

        public List<ProgramsBean> getPrograms() {
            return programs;
        }

        public void setPrograms(List<ProgramsBean> programs) {
            this.programs = programs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataBean dataBean = (DataBean) o;
            return Objects.equals(subject, dataBean.subject) &&
                    Objects.equals(competition, dataBean.competition) &&
                    Objects.equals(language, dataBean.language) &&
                    Objects.equals(CSUUIDs, dataBean.CSUUIDs) &&
                    Objects.equals(subTitle, dataBean.subTitle) &&
                    Objects.equals(vImage, dataBean.vImage) &&
                    Objects.equals(vipNumber, dataBean.vipNumber) &&
                    Objects.equals(UUID, dataBean.UUID) &&
                    Objects.equals(contentType, dataBean.contentType) &&
                    Objects.equals(lSubScript, dataBean.lSubScript) &&
                    Objects.equals(area, dataBean.area) &&
                    Objects.equals(lSuperscript, dataBean.lSuperscript) &&
                    Objects.equals(presenter, dataBean.presenter) &&
                    Objects.equals(videoType, dataBean.videoType) &&
                    Objects.equals(director, dataBean.director) &&
                    Objects.equals(realExclusive, dataBean.realExclusive) &&
                    Objects.equals(tags, dataBean.tags) &&
                    Objects.equals(actors, dataBean.actors) &&
                    Objects.equals(grade, dataBean.grade) &&
                    Objects.equals(premiereChannel, dataBean.premiereChannel) &&
                    Objects.equals(producer, dataBean.producer) &&
                    Objects.equals(topic, dataBean.topic) &&
                    Objects.equals(guest, dataBean.guest) &&
                    Objects.equals(videoClass, dataBean.videoClass) &&
                    Objects.equals(vipFlag, dataBean.vipFlag) &&
                    Objects.equals(cpCode, dataBean.cpCode) &&
                    Objects.equals(singer, dataBean.singer) &&
                    Objects.equals(stepSize, dataBean.stepSize) &&
                    Objects.equals(description, dataBean.description) &&
                    Objects.equals(title, dataBean.title) &&
                    Objects.equals(prize, dataBean.prize) &&
                    Objects.equals(playOrder, dataBean.playOrder) &&
                    Objects.equals(MAMID, dataBean.MAMID) &&
                    Objects.equals(duration, dataBean.duration) &&
                    Objects.equals(is4k, dataBean.is4k) &&
                    Objects.equals(leadingRole, dataBean.leadingRole) &&
                    Objects.equals(movieLevel, dataBean.movieLevel) &&
                    Objects.equals(seriesSum, dataBean.seriesSum) &&
                    Objects.equals(enName, dataBean.enName) &&
                    Objects.equals(audiences, dataBean.audiences) &&
                    Objects.equals(definition, dataBean.definition) &&
                    Objects.equals(vipProductId, dataBean.vipProductId) &&
                    Objects.equals(issueDate, dataBean.issueDate) &&
                    Objects.equals(screenwriter, dataBean.screenwriter) &&
                    Objects.equals(rSubScript, dataBean.rSubScript) &&
                    Objects.equals(reporter, dataBean.reporter) &&
                    Objects.equals(classPeriod, dataBean.classPeriod) &&
                    Objects.equals(airtime, dataBean.airtime) &&
                    Objects.equals(rSupersctipt, dataBean.rSupersctipt) &&
                    Objects.equals(sortType, dataBean.sortType) &&
                    Objects.equals(contentUUID, dataBean.contentUUID) &&
                    Objects.equals(premiereTime, dataBean.premiereTime) &&
                    Objects.equals(hImage, dataBean.hImage) &&
                    Objects.equals(programs, dataBean.programs);
        }

        @Override
        public int hashCode() {

            return Objects.hash(subject, competition, language, CSUUIDs, subTitle, vImage, vipNumber, UUID, contentType, lSubScript, area, lSuperscript, presenter, videoType, director, realExclusive, tags, actors, grade, premiereChannel, producer, topic, guest, videoClass, vipFlag, cpCode, singer, stepSize, description, title, prize, playOrder, MAMID, duration, is4k, leadingRole, movieLevel, seriesSum, enName, audiences, definition, vipProductId, issueDate, screenwriter, rSubScript, reporter, classPeriod, airtime, rSupersctipt, sortType, contentUUID, premiereTime, hImage, programs);
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "subject='" + subject + '\'' +
                    ", competition='" + competition + '\'' +
                    ", language='" + language + '\'' +
                    ", CSUUIDs='" + CSUUIDs + '\'' +
                    ", subTitle='" + subTitle + '\'' +
                    ", vImage='" + vImage + '\'' +
                    ", vipNumber='" + vipNumber + '\'' +
                    ", UUID='" + UUID + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", lSubScript=" + lSubScript +
                    ", area='" + area + '\'' +
                    ", lSuperscript=" + lSuperscript +
                    ", presenter='" + presenter + '\'' +
                    ", videoType='" + videoType + '\'' +
                    ", director='" + director + '\'' +
                    ", realExclusive=" + realExclusive +
                    ", tags='" + tags + '\'' +
                    ", actors='" + actors + '\'' +
                    ", grade=" + grade +
                    ", premiereChannel=" + premiereChannel +
                    ", producer='" + producer + '\'' +
                    ", topic='" + topic + '\'' +
                    ", guest='" + guest + '\'' +
                    ", videoClass='" + videoClass + '\'' +
                    ", vipFlag='" + vipFlag + '\'' +
                    ", cpCode='" + cpCode + '\'' +
                    ", singer='" + singer + '\'' +
                    ", stepSize='" + stepSize + '\'' +
                    ", description='" + description + '\'' +
                    ", title='" + title + '\'' +
                    ", prize='" + prize + '\'' +
                    ", playOrder='" + playOrder + '\'' +
                    ", MAMID='" + MAMID + '\'' +
                    ", duration='" + duration + '\'' +
                    ", is4k='" + is4k + '\'' +
                    ", leadingRole='" + leadingRole + '\'' +
                    ", movieLevel='" + movieLevel + '\'' +
                    ", seriesSum=" + seriesSum +
                    ", enName='" + enName + '\'' +
                    ", audiences='" + audiences + '\'' +
                    ", definition='" + definition + '\'' +
                    ", vipProductId='" + vipProductId + '\'' +
                    ", issueDate='" + issueDate + '\'' +
                    ", screenwriter='" + screenwriter + '\'' +
                    ", rSubScript=" + rSubScript +
                    ", reporter='" + reporter + '\'' +
                    ", classPeriod='" + classPeriod + '\'' +
                    ", airtime='" + airtime + '\'' +
                    ", rSupersctipt=" + rSupersctipt +
                    ", sortType='" + sortType + '\'' +
                    ", contentUUID='" + contentUUID + '\'' +
                    ", premiereTime='" + premiereTime + '\'' +
                    ", hImage='" + hImage + '\'' +
                    ", programs=" + programs +
                    '}';
        }

        public static class ProgramsBean {
            /**
             * lSuperscript : null
             * rSubScript : null
             * actionUri :
             * title : 第一集 极地使命
             * seriesSubUUID : 84198
             * MAMID : wuxi
             * actionType :
             * subTitle :
             * rSupersctipt : null
             * movieLevel : 1
             * vImage :
             * grade : null
             * periods : 1
             * contentUUID : d98611078cc242d690c1d571abbcae79
             * hImage : http://img.cloud.ottcn.com:8080/n3images/2018/07/19/87154_0_20180719-00-54-15-994_14040162-5b76-4745-9b97-d71de6045278_d98611078cc242d690c1d571abbcae79_H2642500000aac128.jpg
             * contentType : CP
             * lSubScript : null
             * drm : null
             */

            private Object lSuperscript;
            private Object rSubScript;
            private String actionUri;
            private String title;
            private String seriesSubUUID;
            private String MAMID;
            private String actionType;
            private String subTitle;
            private Object rSupersctipt;
            private String movieLevel;
            private String vImage;
            private Object grade;
            private String periods;
            private String contentUUID;
            private String hImage;
            private String contentType;
            private Object lSubScript;
            private Object drm;
            private boolean isPlay = false;

            public Object getlSuperscript() {
                return lSuperscript;
            }

            public void setlSuperscript(Object lSuperscript) {
                this.lSuperscript = lSuperscript;
            }

            public Object getrSubScript() {
                return rSubScript;
            }

            public void setrSubScript(Object rSubScript) {
                this.rSubScript = rSubScript;
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

            public String getSeriesSubUUID() {
                return seriesSubUUID;
            }

            public void setSeriesSubUUID(String seriesSubUUID) {
                this.seriesSubUUID = seriesSubUUID;
            }

            public String getMAMID() {
                return MAMID;
            }

            public void setMAMID(String MAMID) {
                this.MAMID = MAMID;
            }

            public String getActionType() {
                return actionType;
            }

            public void setActionType(String actionType) {
                this.actionType = actionType;
            }

            public String getSubTitle() {
                return subTitle;
            }

            public void setSubTitle(String subTitle) {
                this.subTitle = subTitle;
            }

            public Object getrSupersctipt() {
                return rSupersctipt;
            }

            public void setrSupersctipt(Object rSupersctipt) {
                this.rSupersctipt = rSupersctipt;
            }

            public String getMovieLevel() {
                return movieLevel;
            }

            public void setMovieLevel(String movieLevel) {
                this.movieLevel = movieLevel;
            }

            public String getvImage() {
                return vImage;
            }

            public void setvImage(String vImage) {
                this.vImage = vImage;
            }

            public Object getGrade() {
                return grade;
            }

            public void setGrade(Object grade) {
                this.grade = grade;
            }

            public String getPeriods() {
                return periods;
            }

            public void setPeriods(String periods) {
                this.periods = periods;
            }

            public String getContentUUID() {
                return contentUUID;
            }

            public void setContentUUID(String contentUUID) {
                this.contentUUID = contentUUID;
            }

            public String gethImage() {
                return hImage;
            }

            public void sethImage(String hImage) {
                this.hImage = hImage;
            }

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            public Object getlSubScript() {
                return lSubScript;
            }

            public void setlSubScript(Object lSubScript) {
                this.lSubScript = lSubScript;
            }

            public Object getDrm() {
                return drm;
            }

            public void setDrm(Object drm) {
                this.drm = drm;
            }

            public boolean isPlay() {
                return isPlay;
            }

            public void setPlay(boolean play) {
                isPlay = play;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ProgramsBean that = (ProgramsBean) o;
                return isPlay == that.isPlay &&
                        Objects.equals(lSuperscript, that.lSuperscript) &&
                        Objects.equals(rSubScript, that.rSubScript) &&
                        Objects.equals(actionUri, that.actionUri) &&
                        Objects.equals(title, that.title) &&
                        Objects.equals(seriesSubUUID, that.seriesSubUUID) &&
                        Objects.equals(MAMID, that.MAMID) &&
                        Objects.equals(actionType, that.actionType) &&
                        Objects.equals(subTitle, that.subTitle) &&
                        Objects.equals(rSupersctipt, that.rSupersctipt) &&
                        Objects.equals(movieLevel, that.movieLevel) &&
                        Objects.equals(vImage, that.vImage) &&
                        Objects.equals(grade, that.grade) &&
                        Objects.equals(periods, that.periods) &&
                        Objects.equals(contentUUID, that.contentUUID) &&
                        Objects.equals(hImage, that.hImage) &&
                        Objects.equals(contentType, that.contentType) &&
                        Objects.equals(lSubScript, that.lSubScript) &&
                        Objects.equals(drm, that.drm);
            }

            @Override
            public int hashCode() {

                return Objects.hash(lSuperscript, rSubScript, actionUri, title, seriesSubUUID, MAMID, actionType, subTitle, rSupersctipt, movieLevel, vImage, grade, periods, contentUUID, hImage, contentType, lSubScript, drm, isPlay);
            }

            @Override
            public String toString() {
                return "ProgramsBean{" +
                        "lSuperscript=" + lSuperscript +
                        ", rSubScript=" + rSubScript +
                        ", actionUri='" + actionUri + '\'' +
                        ", title='" + title + '\'' +
                        ", seriesSubUUID='" + seriesSubUUID + '\'' +
                        ", MAMID='" + MAMID + '\'' +
                        ", actionType='" + actionType + '\'' +
                        ", subTitle='" + subTitle + '\'' +
                        ", rSupersctipt=" + rSupersctipt +
                        ", movieLevel='" + movieLevel + '\'' +
                        ", vImage='" + vImage + '\'' +
                        ", grade=" + grade +
                        ", periods='" + periods + '\'' +
                        ", contentUUID='" + contentUUID + '\'' +
                        ", hImage='" + hImage + '\'' +
                        ", contentType='" + contentType + '\'' +
                        ", lSubScript=" + lSubScript +
                        ", drm=" + drm +
                        ", isPlay=" + isPlay +
                        '}';
            }
        }
    }
}
