package tv.newtv.cboxtv.cms.screenList.bean;

import java.util.List;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class LabelBean {


    /**
     * data : [{"filterKey":"classTypes","filterValue":[{"title":"全部","key":""},{"title":"111","key":"222"},{"title":"222","key":"333"},{"title":"333","key":"444"}],"filterName":"二级分类"},{"filterKey":"years","filterValue":[{"title":"全部","key":""},{"title":"111","key":"222"},{"title":"222","key":"333"}],"filterName":"年代"},{"filterKey":"areas","filterValue":[{"title":"全部","key":""},{"title":"111","key":"222"},{"title":"222","key":"333"}],"filterName":"地区"}]
     * errorMessage : 成功
     * errorCode : 0
     */

    private String errorMessage;
    private String errorCode;
    private List<DataBean> data;

    @Override
    public String toString() {
        return "LabelBean{" +
                "errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", data=" + data +
                '}';
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
        @Override
        public String toString() {
            return "LabelDataBean{" +
                    "filterKey='" + filterKey + '\'' +
                    ", filterName='" + filterName + '\'' +
                    ", filterValue=" + filterValue +
                    '}';
        }

        /**
         * filterKey : classTypes
         * filterValue : [{"title":"全部","key":""},{"title":"111","key":"222"},{"title":"222","key":"333"},{"title":"333","key":"444"}]
         * filterName : 二级分类
         */


        private String filterKey;
        private String filterName;
        private List<FilterValueBean> filterValue;

        public String getFilterKey() {
            return filterKey;
        }

        public void setFilterKey(String filterKey) {
            this.filterKey = filterKey;
        }

        public String getFilterName() {
            return filterName;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public List<FilterValueBean> getFilterValue() {
            return filterValue;
        }

        public void setFilterValue(List<FilterValueBean> filterValue) {
            this.filterValue = filterValue;
        }

        public static class FilterValueBean {
            @Override
            public String toString() {
                return "FilterValueBean{" +
                        "title='" + title + '\'' +
                        ", key='" + key + '\'' +
                        '}';
            }

            /**
             * title : 全部
             * key :
             */


            private String title;
            private String key;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}
