package tv.newtv.cboxtv.cms.listPage.model;

import java.util.List;

/**
 * Created by caolonghe on 2018/3/9 0009.
 */

public class ScreenInfo {
    /**
     * id : search
     * state : 0
     * total : 6
     * resultList : [{"id":"39516","name":"那些年明星表演的尴尬瞬间","contentType":"PG","UUID":"b0518scf0cd","type":"娱乐","classType":"1","publishDate":"2018-03-03 00:00:00","hpicurl":"http://puui.qpic.cn/vpic/0/b0518scf0cd.png/0","desc":"1"},{"id":"43575","name":"《寻梦环游记》歌神带着男孩到处炫耀，男孩不知道他不是太爷爷","contentType":"PG","UUID":"l0544st1v6l","type":"电影","classType":"预告片|动画|喜剧|家庭|音乐|冒险","publishDate":"2018-03-03 00:00:00","picurl":"http://192.168.75.204/cnlive/public/180212105929589_554.png","hpicurl":"http://192.168.75.204/cnlive/public/180212105933755_894.png","desc":"111"},{"id":"44718","name":"牛仔裤变吊带裙，最出乎意料改造诞生","contentType":"CP","UUID":"q00226n67ay","type":"综艺","classType":"正片|脱口秀","publishDate":"2018-03-03 00:00:00","hpicurl":"http://puui.qpic.cn/vpic/0/q00226n67ay.png/0","desc":"11"},{"id":"44727","name":"《海贼王》路飞聚集同伴的道路，都是那么的催泪","contentType":"PG","UUID":"c0544fvo72j","type":"动漫","classType":"特辑","publishDate":"2018-03-03 00:00:00","hpicurl":"http://puui.qpic.cn/vpic/0/c0544fvo72j.png/0","desc":"1"},{"id":"25469","name":"test","contentType":"PS","UUID":"uuid","type":"test","classType":"test","publishDate":"2018-03-02 00:00:00","picurl":"http://192.168.75.204/cnlive/public/180209110956680_092.png","hpicurl":"http://192.168.75.204/cnlive/public/180209110954388_901.png","desc":"testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"},{"id":"45177","name":"cozi节目集标题","contentType":"PS","UUID":"uuid","type":"cozi1","classType":"cozi2","publishDate":"2018-03-02 00:00:00","picurl":"http://192.168.75.204/cnlive/public/180227103832863_280.jpg","hpicurl":"http://192.168.75.204/cnlive/public/180227103850886_120.jpg","desc":"cozi1"}]
     * programType : {}
     */

    private String id;
    private String state;
    private String total;
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
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

    public static class ProgramTypeBean {
    }

    public static class ResultListBean {
        /**
         * id : 39516
         * name : 那些年明星表演的尴尬瞬间
         * contentType : PG
         * UUID : b0518scf0cd
         * type : 娱乐
         * classType : 1
         * publishDate : 2018-03-03 00:00:00
         * hpicurl : http://puui.qpic.cn/vpic/0/b0518scf0cd.png/0
         * desc : 1
         * picurl : http://192.168.75.204/cnlive/public/180212105929589_554.png
         */

        private String id;
        private String name;
        private String contentType;
        private String UUID;
        private String type;
        private String classType;
        private String publishDate;
        private String hpicurl;
        private String desc;
        private String picurl;

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

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
