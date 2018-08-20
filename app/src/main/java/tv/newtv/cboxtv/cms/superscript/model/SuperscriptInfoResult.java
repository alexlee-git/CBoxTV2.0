package tv.newtv.cboxtv.cms.superscript.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lixin on 2018/3/9.
 */

public class SuperscriptInfoResult<T> {
    @SerializedName("errorMessage")
    private String errMsg;

    @SerializedName("errorCode")
    private String errCode;

    private String updateTime;
    private List<T> data;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
