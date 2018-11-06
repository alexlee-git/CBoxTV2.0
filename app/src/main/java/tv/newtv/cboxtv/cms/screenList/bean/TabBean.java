package tv.newtv.cboxtv.cms.screenList.bean;

import java.util.List;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class TabBean {

    /**
     * data : [{"id":"65","title":"CCTV","child":[{"id":"68","title":"CCTV-1","child":[]},{"id":"70","title":"CCTV-2","child":[]},{"id":"101","title":"CCTV-3","child":[]},{"id":"102","title":"CCTV-4","child":[]},{"id":"103","title":"CCTV-5","child":[]},{"id":"104","title":"CCTV-6","child":[]},{"id":"105","title":"CCTV-7","child":[]},{"id":"106","title":"CCTV-8","child":[]},{"id":"107","title":"CCTV-9","child":[]}]},{"id":"78","title":"卫视","child":[{"id":"80","title":"安徽卫视","child":[]},{"id":"81","title":"天津卫视","child":[]}]},{"id":"79","title":"影音","child":[{"id":"90","title":"精选","child":[]},{"id":"91","title":"小央视频","child":[]}]}]
     * errorMessage : 成功
     * errorCode : 0
     * updateTime : 1538038620234
     */

    private String errorMessage;
    private int errorCode;
    private long updateTime;
    private List<DataBean> data;

    @Override
    public String toString() {
        return "TabBean{" +
                "errorMessage='" + errorMessage + '\'' +
                ", errorCode=" + errorCode +
                ", updateTime=" + updateTime +
                ", data=" + data +
                '}';
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", child=" + child +
                    '}';
        }

        /**
         * id : 65
         * title : CCTV
         * child : [{"id":"68","title":"CCTV-1","child":[]},{"id":"70","title":"CCTV-2","child":[]},{"id":"101","title":"CCTV-3","child":[]},{"id":"102","title":"CCTV-4","child":[]},{"id":"103","title":"CCTV-5","child":[]},{"id":"104","title":"CCTV-6","child":[]},{"id":"105","title":"CCTV-7","child":[]},{"id":"106","title":"CCTV-8","child":[]},{"id":"107","title":"CCTV-9","child":[]}]
         */

        private String id;
        private String title;
        private List<ChildBean> child;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<ChildBean> getChild() {
            return child;
        }

        public void setChild(List<ChildBean> child) {
            this.child = child;
        }

        public static class ChildBean {
            /**
             * id : 68
             * title : CCTV-1
             * child : []
             */

            private String id;
            private String title;
            private List<?> child;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<?> getChild() {
                return child;
            }

            public void setChild(List<?> child) {
                this.child = child;
            }

            @Override
            public String toString() {
                return "ChildBean{" +
                        "id='" + id + '\'' +
                        ", title='" + title + '\'' +
                        ", child=" + child +
                        '}';
            }
        }
    }
}
