package tv.newtv.cboxtv.uc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public class UserCenterPageBean {

    public String title;
    public List<Bean> data;

    public UserCenterPageBean(String title) {
        this.title = title;
    }

    public class Bean {
        public String id;
        public String _contentuuid;
        public String _contenttype;
        public String _title_name;
        public String _imageurl;
        public String _user_id;
        public String _actiontype;
        public long _update_time;
        @SerializedName("_play_index")
        public String playIndex;
        @SerializedName("_play_position")
        public String playPosition;
        @SerializedName("_play_id")
        public String playId;
    }
}
