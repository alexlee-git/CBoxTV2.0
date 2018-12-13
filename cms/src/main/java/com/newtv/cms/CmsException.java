package com.newtv.cms;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         13:11
 * 创建人:           weihaichao
 * 创建日期:          2018/12/12
 */
public class CmsException extends Exception {

    private String mCode;
    private String mMessage;

    public CmsException(String code,String message){
        super(message);
        mCode = code;
        mMessage = message;
    }

    public String getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
