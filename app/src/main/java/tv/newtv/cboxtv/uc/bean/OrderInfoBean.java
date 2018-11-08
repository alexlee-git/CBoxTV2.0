package tv.newtv.cboxtv.uc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by caolonghe on 2018/4/27 0027.
 * 项目名称：CBoxTV2.0
 * 包名：tv.newtv.cboxtv.uc.bean
 * 文件描述：订单Bean
 * 作者：lxq
 * 更改时间：2018/9/11
 */

public class OrderInfoBean {

    /**
     *{"orders":[{"id":1,"code":"b5d5e3f9we6sd","userId":1,"productId":1,"productName":"测试产品","amount":1,"payChannelId":1,"status":"PAY_SUCCESS","payChannelName":"支付宝","duration":10,"originalPrice":1,"discount":0,"createTime":"2018-08-23 17:23:02","payTime":"2018-08-23 17:23:02","tranExpireTime":"2018-08-23 17:23:02","expireTime":"2018-08-23 17:23:02"}],"total":1}     * total : 1
     */

    private int total;

    @SerializedName("orders")
    private List<OrdersBean> orders;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OrdersBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersBean> orders) {
        this.orders = orders;
    }

    public static class OrdersBean implements Comparable<OrdersBean>{
        /**
         * "id":1,
         "code":"b5d5e3f9we6sd",
         "userId":1,
         "productId":1,
         "productName":"测试产品",
         "amount":1,
         "payChannelId":1,
         "status":"PAY_SUCCESS",
         "payChannelName":"支付宝",
         "duration":10,
         "originalPrice":1,
         "discount":0,
         "createTime":"2018-08-23 17:23:02",
         "payTime":"2018-08-23 17:23:02",
         "tranExpireTime":"2018-08-23 17:23:02",
         "expireTime":"2018-08-23 17:23:02"
         */
        @SerializedName("id")
        private int id;//订单id
        @SerializedName("code")
        private String code;//订单号
        @SerializedName("userId")
        private int userId;
        @SerializedName("productId")
        private int productId;
        @SerializedName("productName")
        private String productName;
        @SerializedName("amount")
        private int amount;
        @SerializedName("payChannelId")
        private int payChannelId;
        @SerializedName("status")
        private String status;
        @SerializedName("payChannelName")
        private String payChannelName;
        @SerializedName("originalPrice")
        private int originalPrice;
        @SerializedName("discount")
        private int discount;
        @SerializedName("createTime")
        private String createTime;//创建时间
        @SerializedName("payTime")
        private String payTime;//订购时间
        @SerializedName("expireTime")
        private String expireTime;//过期时间
        @SerializedName("tranExpireTime")
        private String tranExpireTime;//订单失效时间
        @SerializedName("productType")
        private int productType;
        @SerializedName("mediaId")
        private String mediaId;
        @SerializedName("contentType")
        private String contentType;
        @SerializedName("duration")
        private int duration;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getPayChannelId() {
            return payChannelId;
        }

        public void setPayChannelId(int payChannelId) {
            this.payChannelId = payChannelId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPayChannelName() {
            return payChannelName;
        }

        public void setPayChannelName(String payChannelName) {
            this.payChannelName = payChannelName;
        }

        public int getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(int originalPrice) {
            this.originalPrice = originalPrice;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String createTime) {
            this.payTime = createTime;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public String getTranExpireTime() {
            return tranExpireTime;
        }

        public void setTranExpireTime(String tranExpireTime) {
            this.tranExpireTime = tranExpireTime;
        }

        public int getProductType() {
            return productType;
        }

        public void setProductType(int productType) {
            this.productType = productType;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
    public int compareTo(OrdersBean o) {
        int i = this.getId() - o.getId();//先按照年龄排序
        return i;
    }

        @Override
        public String toString() {
            return "OrdersBean{" +
                    "id=" + id +
                    ", code='" + code + '\'' +
                    ", userId=" + userId +
                    ", productId=" + productId +
                    ", productName='" + productName + '\'' +
                    ", amount=" + amount +
                    ", payChannelId=" + payChannelId +
                    ", status='" + status + '\'' +
                    ", payChannelName='" + payChannelName + '\'' +
                    ", originalPrice=" + originalPrice +
                    ", discount=" + discount +
                    ", createTime='" + createTime + '\'' +
                    ", payTime='" + payTime + '\'' +
                    ", expireTime='" + expireTime + '\'' +
                    ", tranExpireTime='" + tranExpireTime + '\'' +
                    ", productType='" + productType + '\'' +
                    ", mediaId='" + mediaId + '\'' +
                    ", contentType='" + contentType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderInfoBean{" +
                "total=" + total +
                ", orders=" + orders +
                '}';
    }
}
