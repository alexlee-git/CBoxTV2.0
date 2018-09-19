package tv.newtv.cboxtv.cms.ad.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TCP on 2018/4/13.
 */

public class AdBean {
    public int status;
    public Adspaces adspaces;

    public class Adspaces{
        public List<AdspacesItem> before;
        public List<AdspacesItem> open;
        public List<AdspacesItem> quit;
        public List<AdspacesItem> desk;
        public List<AdspacesItem> buygoods;
    }

    public class AdspacesItem{
        public String ext;
        public String pos;
        public List<Material> materials;
        public int mid;
        public int aid;
    }

    public class Material{
        @SerializedName("file_path")
        public String filePath;
        @SerializedName("event_content")
        public String eventContent;
        @SerializedName("event_type")
        public String eventType;
        @SerializedName("file_name")
        public String fileName;
        @SerializedName("name")
        public String name;
        public int id;
        public String type;
        @SerializedName("file_size")
        public long fileSize;
        @SerializedName("play_time")
        public int playTime;
        @SerializedName("font_content")
        public String fontContent;
        @SerializedName("font_color")
        public String fontColor;
        @SerializedName("font_size")
        public String fontSize;
        @SerializedName("font_style")
        public String fontStyle;
    }


    @Override
    public String toString() {
        return "AdBean{" +
                "status=" + status +
                ", adspaces=" + adspaces +
                '}';
    }
}
