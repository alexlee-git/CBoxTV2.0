package tv.newtv.cboxtv.cms.screenList.bean;

import java.util.List;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class LabelDataBean {


    /**
     * data : [{"title":"喋血长江","vImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/01/3af7422db6ba4434be21e553f42d8e7c_1538346767010.jpg","grade":0,"contentId":"11346698","hImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/01/3af7422db6ba4434be21e553f42d8e7c_1538346767010.jpg","contentType":"PS","contentUUID":"169798","movieLevel":1},{"title":"战天狼","vImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/25/7b00acda84394642aaf952bc4a87a20d_1537853545393.jpg","grade":0,"contentId":"11350867","hImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/25/7b00acda84394642aaf952bc4a87a20d_1537853545393.jpg","contentType":"PS","contentUUID":"164467","movieLevel":1},{"title":"棒棒的幸福生活","vImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/28/0df5a8791e6849ae9631f022e8a6c17e_1538084084767.jpg","grade":0,"contentId":"11350884","hImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/28/0df5a8791e6849ae9631f022e8a6c17e_1538084084767.jpg","contentType":"PS","contentUUID":"167289","movieLevel":1},{"title":"家庭秘密","vImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/18/963f2a2a0fa148d182720bd4bb6ae617_1537240554191.jpg","grade":0,"contentId":"11351018","hImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/09/18/963f2a2a0fa148d182720bd4bb6ae617_1537240554191.jpg","contentType":"PS","contentUUID":"157436","movieLevel":1},{"title":"彭湃","vImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/08/533507141b35400eb6d441e31ab4412a_1538933870372.jpg","grade":0,"contentId":"11362234","hImage":"http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/08/533507141b35400eb6d441e31ab4412a_1538933870372.jpg","contentType":"PS","contentUUID":"174466","movieLevel":1}]
     * errorMessage : 成功
     * errorCode : 0
     * total : 5
     */

    private String errorMessage;
    private String errorCode;
    private int total;
    private List<DataBean> data;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 喋血长江
         * vImage : http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/01/3af7422db6ba4434be21e553f42d8e7c_1538346767010.jpg
         * grade : 0
         * contentId : 11346698
         * hImage : http://img.cloud.ottcn.com:8080/n3imageshttp://srcvd.cloud.ottcn.com/image/newtv/2018/10/01/3af7422db6ba4434be21e553f42d8e7c_1538346767010.jpg
         * contentType : PS
         * contentUUID : 169798
         * movieLevel : 1
         */

        private String title;
        private String vImage;
        private int grade;
        private String contentId;
        private String hImage;
        private String contentType;
        private String contentUUID;
        private int movieLevel;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVImage() {
            return vImage;
        }

        public void setVImage(String vImage) {
            this.vImage = vImage;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

        public String getHImage() {
            return hImage;
        }

        public void setHImage(String hImage) {
            this.hImage = hImage;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentUUID() {
            return contentUUID;
        }

        public void setContentUUID(String contentUUID) {
            this.contentUUID = contentUUID;
        }

        public int getMovieLevel() {
            return movieLevel;
        }

        public void setMovieLevel(int movieLevel) {
            this.movieLevel = movieLevel;
        }
    }
}
