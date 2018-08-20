package tv.newtv.cboxtv.cms.details.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/2/26 0026.
 */

public class PayChannel {

    @SerializedName("payChannels")
    private List<PayChannelsBean> payChannels;

    public List<PayChannelsBean> getPayChannels() {
        return payChannels;
    }

    public void setPayChannels(List<PayChannelsBean> payChannels) {
        this.payChannels = payChannels;
    }

    public static class PayChannelsBean {
        /**
         * id : 1
         * name : 支付宝
         * status : true
         * memo : 支付宝支付123
         */

        private int id;
        private String name;
        private boolean status;
        private String memo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        @Override
        public String toString() {
            return "PayChannelsBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    ", memo='" + memo + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PayChannel{" +
                "payChannels=" + payChannels +
                '}';
    }
}
