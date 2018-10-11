package tv.newtv.cboxtv.menu.model;

import java.util.List;

/**
 * Created by TCP on 2018/4/17.
 */

public class HeadMenuBean {
    private String errorMessage;
    private String errorCode;
    private String updateTime;
    private List<Node> data;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<Node> getData() {
        return data;
    }

    public void setData(List<Node> data) {
        this.data = data;
    }

}
