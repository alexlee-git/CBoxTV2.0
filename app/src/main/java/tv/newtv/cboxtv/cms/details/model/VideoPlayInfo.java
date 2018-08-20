package tv.newtv.cboxtv.cms.details.model;

/**
 * Created by gaoleichao on 2018/4/18.
 */

public class VideoPlayInfo {
    public int index;
    public int position;
    public String uuid;

    public VideoPlayInfo(int index, int position, String uuid) {
        this.index = index;
        this.position = position;
        this.uuid = uuid;
    }
}
