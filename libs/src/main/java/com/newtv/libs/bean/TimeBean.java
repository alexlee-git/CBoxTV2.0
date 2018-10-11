package com.newtv.libs.bean;

/**
 * Created by TCP on 2018/5/23.
 */

public class TimeBean {
    private long response;
    private String statusCode;
    private String message;

    public long getResponse() {
        return response;
    }

    public void setResponse(long response) {
        this.response = response;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
