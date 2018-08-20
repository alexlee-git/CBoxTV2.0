package tv.newtv.cboxtv.cms.listPage.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by caolonghe on 2018/3/8 0008.
 */

public class MarkInfo {

    @SerializedName("retrievalKeywordList")
    private List<Mark> data ;

    public List<Mark> getData() {
        return data;
    }

    public void setData(List<Mark> data) {
        this.data = data;
    }

    public class Mark {
        private String key;
        private String type;
        private String classTypes;
        private String years;
        private String areas;


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getClassTypes() {
            return classTypes;
        }

        public void setClassTypes(String classTypes) {
            this.classTypes = classTypes;
        }

        public String getYears() {
            return years;
        }

        public void setYears(String years) {
            this.years = years;
        }

        public String getAreas() {
            return areas;
        }

        public void setAreas(String areas) {
            this.areas = areas;
        }
    }
}
