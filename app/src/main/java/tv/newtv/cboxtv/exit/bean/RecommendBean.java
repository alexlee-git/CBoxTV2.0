package tv.newtv.cboxtv.exit.bean;

import java.util.List;

public class RecommendBean {


    /**
     * data : [{"programs":[{"defaultFocus":0,"contentId":null,"contentType":null,"img":"http://img.cloud.ottcn.com/n3images/2018/10/22/e0954e345409402881487a7ca30ffcd5_1540177107688.png","title":"弱者","subTitle":null,"l_id":"11488303","l_uuid":"59946","l_contentType":"PS","l_actionType":"OPEN_DETAILS","l_actionUri":null,"l_focusId":null,"l_focusParam":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"0","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_1","isAd":0,"sortNum":"1","seriesSubUUID":null,"apk":null,"apkPageType":null,"apkPageParam":null,"specialParam":"0","recommendedType":"1","recentMsg":""},{"defaultFocus":0,"contentId":null,"contentType":null,"img":"http://img.cloud.ottcn.com/n3images/2018/10/22/c1860698f5464ff8a4f11d03544d5427_1540170652459.jpg","title":"浓情酒乡","subTitle":null,"l_id":"11488301","l_uuid":"59967","l_contentType":"PS","l_actionType":"OPEN_DETAILS","l_actionUri":null,"l_focusId":null,"l_focusParam":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"288","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_2","isAd":0,"sortNum":"2","seriesSubUUID":null,"apk":null,"apkPageType":null,"apkPageParam":null,"specialParam":"0","recommendedType":"1","recentMsg":""}],"blockId":10759,"blockTitle":"新建区块","blockImg":"","haveBlockTitle":"1","rowNum":"","colNum":"","layoutCode":"layout_008","blockType":"1"}]
     * isNav : 0
     * subTitle :
     * pageTitle : 退出页
     * background :
     * errorMessage : 成功
     * errorCode : 0
     * description : null
     * isAd : 0
     * templateZT :
     */

    private int isNav;
    private String subTitle;
    private String pageTitle;
    private String background;
    private String errorMessage;
    private String errorCode;
    private Object description;
    private String isAd;
    private String templateZT;
    private List<DataBean> data;

    public int getIsNav() {
        return isNav;
    }

    public void setIsNav(int isNav) {
        this.isNav = isNav;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getIsAd() {
        return isAd;
    }

    public void setIsAd(String isAd) {
        this.isAd = isAd;
    }

    public String getTemplateZT() {
        return templateZT;
    }

    public void setTemplateZT(String templateZT) {
        this.templateZT = templateZT;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * programs : [{"defaultFocus":0,"contentId":null,"contentType":null,"img":"http://img.cloud.ottcn.com/n3images/2018/10/22/e0954e345409402881487a7ca30ffcd5_1540177107688.png","title":"弱者","subTitle":null,"l_id":"11488303","l_uuid":"59946","l_contentType":"PS","l_actionType":"OPEN_DETAILS","l_actionUri":null,"l_focusId":null,"l_focusParam":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"0","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_1","isAd":0,"sortNum":"1","seriesSubUUID":null,"apk":null,"apkPageType":null,"apkPageParam":null,"specialParam":"0","recommendedType":"1","recentMsg":""},{"defaultFocus":0,"contentId":null,"contentType":null,"img":"http://img.cloud.ottcn.com/n3images/2018/10/22/c1860698f5464ff8a4f11d03544d5427_1540170652459.jpg","title":"浓情酒乡","subTitle":null,"l_id":"11488301","l_uuid":"59967","l_contentType":"PS","l_actionType":"OPEN_DETAILS","l_actionUri":null,"l_focusId":null,"l_focusParam":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"288","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_2","isAd":0,"sortNum":"2","seriesSubUUID":null,"apk":null,"apkPageType":null,"apkPageParam":null,"specialParam":"0","recommendedType":"1","recentMsg":""}]
         * blockId : 10759
         * blockTitle : 新建区块
         * blockImg :
         * haveBlockTitle : 1
         * rowNum :
         * colNum :
         * layoutCode : layout_008
         * blockType : 1
         */

        private int blockId;
        private String blockTitle;
        private String blockImg;
        private String haveBlockTitle;
        private String rowNum;
        private String colNum;
        private String layoutCode;
        private String blockType;
        private List<ProgramsBean> programs;

        public int getBlockId() {
            return blockId;
        }

        public void setBlockId(int blockId) {
            this.blockId = blockId;
        }

        public String getBlockTitle() {
            return blockTitle;
        }

        public void setBlockTitle(String blockTitle) {
            this.blockTitle = blockTitle;
        }

        public String getBlockImg() {
            return blockImg;
        }

        public void setBlockImg(String blockImg) {
            this.blockImg = blockImg;
        }

        public String getHaveBlockTitle() {
            return haveBlockTitle;
        }

        public void setHaveBlockTitle(String haveBlockTitle) {
            this.haveBlockTitle = haveBlockTitle;
        }

        public String getRowNum() {
            return rowNum;
        }

        public void setRowNum(String rowNum) {
            this.rowNum = rowNum;
        }

        public String getColNum() {
            return colNum;
        }

        public void setColNum(String colNum) {
            this.colNum = colNum;
        }

        public String getLayoutCode() {
            return layoutCode;
        }

        public void setLayoutCode(String layoutCode) {
            this.layoutCode = layoutCode;
        }

        public String getBlockType() {
            return blockType;
        }

        public void setBlockType(String blockType) {
            this.blockType = blockType;
        }

        public List<ProgramsBean> getPrograms() {
            return programs;
        }

        public void setPrograms(List<ProgramsBean> programs) {
            this.programs = programs;
        }

        public static class ProgramsBean {
            /**
             * defaultFocus : 0
             * contentId : null
             * contentType : null
             * img : http://img.cloud.ottcn.com/n3images/2018/10/22/e0954e345409402881487a7ca30ffcd5_1540177107688.png
             * title : 弱者
             * subTitle : null
             * l_id : 11488303
             * l_uuid : 59946
             * l_contentType : PS
             * l_actionType : OPEN_DETAILS
             * l_actionUri : null
             * l_focusId : null
             * l_focusParam : null
             * grade :
             * lSuperScript :
             * rSuperScript :
             * lSubScript :
             * rSubScript :
             * columnPoint : 0
             * rowPoint : 0
             * columnLength : 240
             * rowHeight : 360
             * cellType :
             * cellCode : cell_008_1
             * isAd : 0
             * sortNum : 1
             * seriesSubUUID : null
             * apk : null
             * apkPageType : null
             * apkPageParam : null
             * specialParam : 0
             * recommendedType : 1
             * recentMsg :
             */

            private int defaultFocus;
            private Object contentId;
            private Object contentType;
            private String img;
            private String title;
            private Object subTitle;
            private String l_id;
            private String l_uuid;
            private String l_contentType;
            private String l_actionType;
            private Object l_actionUri;
            private Object l_focusId;
            private Object l_focusParam;
            private String grade;
            private String lSuperScript;
            private String rSuperScript;
            private String lSubScript;
            private String rSubScript;
            private String columnPoint;
            private String rowPoint;
            private String columnLength;
            private String rowHeight;
            private String cellType;
            private String cellCode;
            private int isAd;
            private String sortNum;
            private Object seriesSubUUID;
            private Object apk;
            private Object apkPageType;
            private Object apkPageParam;
            private String specialParam;
            private String recommendedType;
            private String recentMsg;

            public int getDefaultFocus() {
                return defaultFocus;
            }

            public void setDefaultFocus(int defaultFocus) {
                this.defaultFocus = defaultFocus;
            }

            public Object getContentId() {
                return contentId;
            }

            public void setContentId(Object contentId) {
                this.contentId = contentId;
            }

            public Object getContentType() {
                return contentType;
            }

            public void setContentType(Object contentType) {
                this.contentType = contentType;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Object getSubTitle() {
                return subTitle;
            }

            public void setSubTitle(Object subTitle) {
                this.subTitle = subTitle;
            }

            public String getL_id() {
                return l_id;
            }

            public void setL_id(String l_id) {
                this.l_id = l_id;
            }

            public String getL_uuid() {
                return l_uuid;
            }

            public void setL_uuid(String l_uuid) {
                this.l_uuid = l_uuid;
            }

            public String getL_contentType() {
                return l_contentType;
            }

            public void setL_contentType(String l_contentType) {
                this.l_contentType = l_contentType;
            }

            public String getL_actionType() {
                return l_actionType;
            }

            public void setL_actionType(String l_actionType) {
                this.l_actionType = l_actionType;
            }

            public Object getL_actionUri() {
                return l_actionUri;
            }

            public void setL_actionUri(Object l_actionUri) {
                this.l_actionUri = l_actionUri;
            }

            public Object getL_focusId() {
                return l_focusId;
            }

            public void setL_focusId(Object l_focusId) {
                this.l_focusId = l_focusId;
            }

            public Object getL_focusParam() {
                return l_focusParam;
            }

            public void setL_focusParam(Object l_focusParam) {
                this.l_focusParam = l_focusParam;
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

            public String getRSuperScript() {
                return rSuperScript;
            }

            public void setRSuperScript(String rSuperScript) {
                this.rSuperScript = rSuperScript;
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

            public String getColumnPoint() {
                return columnPoint;
            }

            public void setColumnPoint(String columnPoint) {
                this.columnPoint = columnPoint;
            }

            public String getRowPoint() {
                return rowPoint;
            }

            public void setRowPoint(String rowPoint) {
                this.rowPoint = rowPoint;
            }

            public String getColumnLength() {
                return columnLength;
            }

            public void setColumnLength(String columnLength) {
                this.columnLength = columnLength;
            }

            public String getRowHeight() {
                return rowHeight;
            }

            public void setRowHeight(String rowHeight) {
                this.rowHeight = rowHeight;
            }

            public String getCellType() {
                return cellType;
            }

            public void setCellType(String cellType) {
                this.cellType = cellType;
            }

            public String getCellCode() {
                return cellCode;
            }

            public void setCellCode(String cellCode) {
                this.cellCode = cellCode;
            }

            public int getIsAd() {
                return isAd;
            }

            public void setIsAd(int isAd) {
                this.isAd = isAd;
            }

            public String getSortNum() {
                return sortNum;
            }

            public void setSortNum(String sortNum) {
                this.sortNum = sortNum;
            }

            public Object getSeriesSubUUID() {
                return seriesSubUUID;
            }

            public void setSeriesSubUUID(Object seriesSubUUID) {
                this.seriesSubUUID = seriesSubUUID;
            }

            public Object getApk() {
                return apk;
            }

            public void setApk(Object apk) {
                this.apk = apk;
            }

            public Object getApkPageType() {
                return apkPageType;
            }

            public void setApkPageType(Object apkPageType) {
                this.apkPageType = apkPageType;
            }

            public Object getApkPageParam() {
                return apkPageParam;
            }

            public void setApkPageParam(Object apkPageParam) {
                this.apkPageParam = apkPageParam;
            }

            public String getSpecialParam() {
                return specialParam;
            }

            public void setSpecialParam(String specialParam) {
                this.specialParam = specialParam;
            }

            public String getRecommendedType() {
                return recommendedType;
            }

            public void setRecommendedType(String recommendedType) {
                this.recommendedType = recommendedType;
            }

            public String getRecentMsg() {
                return recentMsg;
            }

            public void setRecentMsg(String recentMsg) {
                this.recentMsg = recentMsg;
            }
        }
    }
}
