package tv.newtv.cboxtv.cms.search.bean;

import java.util.List;

/**
 * 类描述：搜索结果实体类
 * 创建人：wqs
 * 创建时间： 2018/3/10 0010 14:07
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchResultInfos {

    /**
     * id : search
     * state : 0
     * total : 4
     * resultList : [{"id":"45077","name":"待宰的羔羊","contentType":"PS","UUID":"uuid","type":"一级分类","classType":"二级分类","publishDate":"2018-03-02 00:00:00","picurl":"http://192.168.75.204/cnlive/public/180213142959437_804.png","hpicurl":"http://192.168.75.204/cnlive/public/180213142957314_431.png","desc":"内容简介","TRD":"4","TLD":"2","TRT":"3","TLT":"1"},{"id":"45251","name":"劲舞团-劲舞团VS真人街舞","contentType":"CP","UUID":"27iEykHq67w","type":"游戏","classType":"网络游戏|电竞视频|搞笑视频|腾讯游戏","publishDate":"2018-03-03 00:00:00","hpicurl":"http://puui.qpic.cn/vpic/0/27iEykHq67w.png/0"},{"id":"45255","name":"时政-新闻客户端（2018年3月）","contentType":"PS","UUID":"73d84ffceb734c07ab5ded407a0db0b1","type":"资讯","classType":"时政 国内","publishDate":"2018-03-02 00:00:00","director":"无","leadingrole":"无","desc":"港区代表委员首次集体乘高铁抵京。"},{"id":"45177","name":"cozi节目集标题","contentType":"PS","UUID":"uuid","type":"cozi1","classType":"cozi2","publishDate":"2018-03-02 00:00:00","picurl":"http://192.168.75.204/cnlive/public/180227103832863_280.jpg","hpicurl":"http://192.168.75.204/cnlive/public/180227103850886_120.jpg","desc":"cozi1"}]
     * programType : {"汽车":0,"少儿":0,"一级分类":1,"动漫":0,"母婴":0,"全部":4,"cozi1":1,"电影":0,"333":0,"游戏":1,"纪录片":0,"电视剧":0,"旅游":0,"test":0,"时尚":0,"MV":0,"1":0,"综艺":0,"生活":0,"娱乐":0,"体育":0,"教育":0,"y":0,"资讯":1,"舞蹈":0,"财经":0}
     */

    private String id;
    private String state;
    private Integer total;
    private ProgramTypeBean programType;
    private List<ResultListBean> resultList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public ProgramTypeBean getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramTypeBean programType) {
        this.programType = programType;
    }

    public List<ResultListBean> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultListBean> resultList) {
        this.resultList = resultList;
    }

    public class ProgramTypeBean {
        private String name;
        private Integer count;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class ResultListBean {
        /**
         * id : 45077
         * name : 待宰的羔羊
         * contentType : PS
         * UUID : uuid
         * type : 一级分类
         * classType : 二级分类
         * publishDate : 2018-03-02 00:00:00
         * picurl : http://192.168.75.204/cnlive/public/180213142959437_804.png
         * hpicurl : http://192.168.75.204/cnlive/public/180213142957314_431.png
         * desc : 内容简介
         * TRD : 4
         * TLD : 2
         * TRT : 3
         * TLT : 1
         * director : 无
         * leadingrole : 无
         */

        private String id;
        private String name;
        private String subTitle;
        private String contentType;
        private String UUID;
        private String type;
        private String classType;
        private String publishDate;
        private String picurl;
        private String hpicurl;
        private String desc;
        private String TRD;
        private String TLD;
        private String TRT;
        private String TLT;
        private String director;
        private String leadingrole;
        private String actionUri;

        public String getActionUri() {
            return actionUri;
        }

        public void setActionUri(String actionUri) {
            this.actionUri = actionUri;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getUUID() {
            return UUID;
        }

        public void setUUID(String UUID) {
            this.UUID = UUID;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(String publishDate) {
            this.publishDate = publishDate;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }

        public String getHpicurl() {
            return hpicurl;
        }

        public void setHpicurl(String hpicurl) {
            this.hpicurl = hpicurl;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTRD() {
            return TRD;
        }

        public void setTRD(String TRD) {
            this.TRD = TRD;
        }

        public String getTLD() {
            return TLD;
        }

        public void setTLD(String TLD) {
            this.TLD = TLD;
        }

        public String getTRT() {
            return TRT;
        }

        public void setTRT(String TRT) {
            this.TRT = TRT;
        }

        public String getTLT() {
            return TLT;
        }

        public void setTLT(String TLT) {
            this.TLT = TLT;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getLeadingrole() {
            return leadingrole;
        }

        public void setLeadingrole(String leadingrole) {
            this.leadingrole = leadingrole;
        }

        @Override
        public String toString() {
            return "ResultListBean{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", UUID='" + UUID + '\'' +
                    ", type='" + type + '\'' +
                    ", classType='" + classType + '\'' +
                    ", publishDate='" + publishDate + '\'' +
                    ", picurl='" + picurl + '\'' +
                    ", hpicurl='" + hpicurl + '\'' +
                    ", desc='" + desc + '\'' +
                    ", TRD='" + TRD + '\'' +
                    ", TLD='" + TLD + '\'' +
                    ", TRT='" + TRT + '\'' +
                    ", TLT='" + TLT + '\'' +
                    ", director='" + director + '\'' +
                    ", leadingrole='" + leadingrole + '\'' +
                    '}';
        }
    }
}
