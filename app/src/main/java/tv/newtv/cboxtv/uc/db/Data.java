package tv.newtv.cboxtv.uc.db;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db.v1
 * 创建事件:         09:12
 * 创建人:           weihaichao
 * 创建日期:          2018/2/28
 */

public class Data {
    private String _resourceid;
    private String _resource_poster;
    private String _resource_name;

    public void setResourceid(String _resourceid) {
        this._resourceid = _resourceid;
    }

    public String getResourceid() {
        return _resourceid;
    }

    public void setResourceName(String _resource_name) {
        this._resource_name = _resource_name;
    }

    public String getResourceName() {
        return _resource_name;
    }

    public void setResourcePoster(String _resource_poster) {
        this._resource_poster = _resource_poster;
    }

    public String getResourcePoster() {
        return _resource_poster;
    }
}
