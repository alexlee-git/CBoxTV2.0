package com.newtv.cms.bean;

import java.util.List;

/**
 * Created by wangkun on 2018/2/24.
 */

public class ChkRequest {

    private String id;
    private String source;
    private String appKey;
    private String albumId;
    private String channelId;
    private List<Product> productDTOList;

    private String pid;


//    public int getVipFlag() {
//        return vipFlag;
//    }
//
//    public void setVipFlag(int vipFlag) {
//        this.vipFlag = vipFlag;
//    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Product> getProductDTOList() {
        return productDTOList;
    }

    public void setProductDTOList(List<Product> productDTOList) {
        this.productDTOList = productDTOList;
    }

    public static class User {
        private long userId;
        private String userToken;

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }
    }

    public static class Product {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Override
    public String toString() {
        return "id:"+id+",source:"+source+",appKey:"+appKey+",albumId:"+albumId+",channelId:"+channelId+",pId:"+pid;
    }
}
