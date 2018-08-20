package tv.newtv.cboxtv.cms.search.bean;

import java.util.List;

/**
 * 类描述：热搜数据的实体类
 * 创建人：wqs
 * 创建时间： 2018/3/16 0016 10:16
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchHotInfo {

    /**
     * data : [{"hotSearch":[{"title":"爱情公寓4"},{"title":"爱情公寓3"},{"title":"爱情公寓2"},{"title":"爱情公寓1"}],"blockId":8051,"blockTitle":"大家都爱搜列表","blockImg":"","haveBlockTitle":"0","rowNum":"","colNum":"","layoutCode":"","blockType":"recommendOnOrder"},{"programs":[{"contentUUID":"yubwtu7da9bpewf","contentType":"PS","img":"http://172.25.5.101/img/cnlive/180313154514480_950.png","title":"全景CBA","subTitle":"","actionType":"OPEN_DETAILS","actionUri":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"1440","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_6","isAd":0}],"blockId":8052,"blockTitle":"热门搜索line1","blockImg":"","haveBlockTitle":"0","rowNum":"0","colNum":"","layoutCode":"layout_008","blockType":"recommendOnCell"}]
     * isNav : 0
     * errorMessage : 成功
     * errorCode : 0
     */

    private String isNav;
    private String errorMessage;
    private String errorCode;
    private List<DataBean> data;

    public String getIsNav() {
        return isNav;
    }

    public void setIsNav(String isNav) {
        this.isNav = isNav;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * hotSearch : [{"title":"爱情公寓4"},{"title":"爱情公寓3"},{"title":"爱情公寓2"},{"title":"爱情公寓1"}]
         * blockId : 8051
         * blockTitle : 大家都爱搜列表
         * blockImg :
         * haveBlockTitle : 0
         * rowNum :
         * colNum :
         * layoutCode :
         * blockType : recommendOnOrder
         * programs : [{"contentUUID":"yubwtu7da9bpewf","contentType":"PS","img":"http://172.25.5.101/img/cnlive/180313154514480_950.png","title":"全景CBA","subTitle":"","actionType":"OPEN_DETAILS","actionUri":null,"grade":"","lSuperScript":"","rSuperScript":"","lSubScript":"","rSubScript":"","columnPoint":"1440","rowPoint":"0","columnLength":"240","rowHeight":"360","cellType":"","cellCode":"cell_008_6","isAd":0}]
         */

        private int blockId;
        private String blockTitle;
        private String blockImg;
        private String haveBlockTitle;
        private String rowNum;
        private String colNum;
        private String layoutCode;
        private String blockType;
        private List<HotSearchBean> hotSearch;
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

        public List<HotSearchBean> getHotSearch() {
            return hotSearch;
        }

        public void setHotSearch(List<HotSearchBean> hotSearch) {
            this.hotSearch = hotSearch;
        }

        public List<ProgramsBean> getPrograms() {
            return programs;
        }

        public void setPrograms(List<ProgramsBean> programs) {
            this.programs = programs;
        }

        public static class HotSearchBean {
            /**
             * title : 爱情公寓4
             */

            private String title;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class ProgramsBean {
            /**
             * contentUUID : yubwtu7da9bpewf
             * contentType : PS
             * img : http://172.25.5.101/img/cnlive/180313154514480_950.png
             * title : 全景CBA
             * subTitle :
             * actionType : OPEN_DETAILS
             * actionUri : null
             * grade :
             * lSuperScript :
             * rSuperScript :
             * lSubScript :
             * rSubScript :
             * columnPoint : 1440
             * rowPoint : 0
             * columnLength : 240
             * rowHeight : 360
             * cellType :
             * cellCode : cell_008_6
             * isAd : 0
             */

            private String contentUUID;
            private String contentType;
            private String img;
            private String title;
            private String subTitle;
            private String actionType;
            private Object actionUri;
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

            public String getContentUUID() {
                return contentUUID;
            }

            public void setContentUUID(String contentUUID) {
                this.contentUUID = contentUUID;
            }

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
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

            public Object getActionUri() {
                return actionUri;
            }

            public void setActionUri(Object actionUri) {
                this.actionUri = actionUri;
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
        }
    }
}
