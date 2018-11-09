package tv.newtv.cboxtv.uc.v2.Pay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by caolonghe on 2018/3/20 0020.
 */

public class ProductPricesInfo implements Serializable {


    /**
     * response : {"id":183,"name":"少儿频道","prdType":1,"status":4,"corner":null,"horzPoster":"http://bzo.cloud.ottcn.com/tiger/imgs/1521012599945.png","perpPoster":"dog.jpg","memo":"这是备注","prdSource":null,"extCode":null,"extData":null,"appKey":"appkey","prices":[{"id":80,"prdId":183,"name":"资费名称","price":1,"vipPrice":1,"point":1,"vipPoint":1,"duration":1,"memo":"1","priceDiscount":0,"vipPriceDiscount":0,"pointDiscount":0,"vipPointDiscount":0,"realDuration":1,"icon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521011980522.png","focusIcon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521012111125.png","prdSource":null,"extCode":null,"activity":{"id":48,"prcId":80,"name":"春节打2折","actType":"DISCOUNT","startTime":1519862400000,"endTime":1617148800000,"detailType":"PERCENTAGE","percentage":20,"giveHour":null,"givePrcId":null,"price":null,"vipPrice":null,"point":null,"vipPoint":null,"memo":"打折2","icon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010606552.png","focusIcon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010608838.png","product":null}}]}
     * statusCode : 1
     * message : success
     */
    @SerializedName("response")
    private ResponseBean response;
    @SerializedName("statusCode")
    private String statusCode;
    @SerializedName("message")
    private String message;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
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

    public static class ResponseBean implements Serializable {
        /**
         * id : 183
         * name : 少儿频道
         * prdType : 1
         * status : 4
         * corner : null
         * horzPoster : http://bzo.cloud.ottcn.com/tiger/imgs/1521012599945.png
         * perpPoster : dog.jpg
         * memo : 这是备注
         * prdSource : null
         * extCode : null
         * extData : null
         * appKey : appkey
         * prices : [{"id":80,"prdId":183,"name":"资费名称","price":1,"vipPrice":1,"point":1,"vipPoint":1,"duration":1,"memo":"1","priceDiscount":0,"vipPriceDiscount":0,"pointDiscount":0,"vipPointDiscount":0,"realDuration":1,"icon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521011980522.png","focusIcon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521012111125.png","prdSource":null,"extCode":null,"activity":{"id":48,"prcId":80,"name":"春节打2折","actType":"DISCOUNT","startTime":1519862400000,"endTime":1617148800000,"detailType":"PERCENTAGE","percentage":20,"giveHour":null,"givePrcId":null,"price":null,"vipPrice":null,"point":null,"vipPoint":null,"memo":"打折2","icon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010606552.png","focusIcon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010608838.png","product":null}}]
         */

        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("prdType")
        private int prdType;
        private int status;
        private Object corner;
        private String horzPoster;
        private String perpPoster;
        private String memo;
        private Object prdSource;
        private Object extCode;
        private Object extData;
        private String appKey;
        @SerializedName("prices")
        private List<PricesBean> prices;

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

        public int getPrdType() {
            return prdType;
        }

        public void setPrdType(int prdType) {
            this.prdType = prdType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getCorner() {
            return corner;
        }

        public void setCorner(Object corner) {
            this.corner = corner;
        }

        public String getHorzPoster() {
            return horzPoster;
        }

        public void setHorzPoster(String horzPoster) {
            this.horzPoster = horzPoster;
        }

        public String getPerpPoster() {
            return perpPoster;
        }

        public void setPerpPoster(String perpPoster) {
            this.perpPoster = perpPoster;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public Object getPrdSource() {
            return prdSource;
        }

        public void setPrdSource(Object prdSource) {
            this.prdSource = prdSource;
        }

        public Object getExtCode() {
            return extCode;
        }

        public void setExtCode(Object extCode) {
            this.extCode = extCode;
        }

        public Object getExtData() {
            return extData;
        }

        public void setExtData(Object extData) {
            this.extData = extData;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public List<PricesBean> getPrices() {
            return prices;
        }

        public void setPrices(List<PricesBean> prices) {
            this.prices = prices;
        }

        @Override
        public String toString() {
            return "ResponseBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", prdType=" + prdType +
                    ", status=" + status +
                    ", corner=" + corner +
                    ", horzPoster='" + horzPoster + '\'' +
                    ", perpPoster='" + perpPoster + '\'' +
                    ", memo='" + memo + '\'' +
                    ", prdSource=" + prdSource +
                    ", extCode=" + extCode +
                    ", extData=" + extData +
                    ", appKey='" + appKey + '\'' +
                    ", prices=" + prices +
                    '}';
        }

        public static class PricesBean implements Serializable {
            /**
             * id : 80
             * prdId : 183
             * name : 资费名称
             * price : 1
             * vipPrice : 1
             * point : 1
             * vipPoint : 1
             * duration : 1
             * memo : 1
             * priceDiscount : 0
             * vipPriceDiscount : 0
             * pointDiscount : 0
             * vipPointDiscount : 0
             * realDuration : 1
             * icon : http://bzo.cloud.ottcn.com/tiger/imgs/1521011980522.png
             * focusIcon : http://bzo.cloud.ottcn.com/tiger/imgs/1521012111125.png
             * prdSource : null
             * extCode : null
             * activity : {"id":48,"prcId":80,"name":"春节打2折","actType":"DISCOUNT","startTime":1519862400000,"endTime":1617148800000,"detailType":"PERCENTAGE","percentage":20,"giveHour":null,"givePrcId":null,"price":null,"vipPrice":null,"point":null,"vipPoint":null,"memo":"打折2","icon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010606552.png","focusIcon":"http://bzo.cloud.ottcn.com/tiger/imgs/1521010608838.png","product":null}
             */

            @SerializedName("id")
            private int id;
            @SerializedName("prdId")
            private int prdId;
            @SerializedName("name")
            private String name;
            @SerializedName("price")
            private int price;
            @SerializedName("vipPrice")
            private int vipPrice;
            private int point;
            private int vipPoint;
             @SerializedName("duration")
            private int duration;
            private String memo;

            @SerializedName("priceDiscount")
            private int priceDiscount;
            @SerializedName("vipPriceDiscount")
            private int vipPriceDiscount;
            @SerializedName("pointDiscount")
            private int pointDiscount;
            @SerializedName("vipPointDiscount")
            private int vipPointDiscount;

            @SerializedName("realDuration")
            private long realDuration;
            private String icon;
            private String focusIcon;
            private Object prdSource;
            private Object extCode;
            private ActivityBean activity;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPrdId() {
                return prdId;
            }

            public void setPrdId(int prdId) {
                this.prdId = prdId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public int getVipPrice() {
                return vipPrice;
            }

            public void setVipPrice(int vipPrice) {
                this.vipPrice = vipPrice;
            }

            public int getPoint() {
                return point;
            }

            public void setPoint(int point) {
                this.point = point;
            }

            public int getVipPoint() {
                return vipPoint;
            }

            public void setVipPoint(int vipPoint) {
                this.vipPoint = vipPoint;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }

            public int getPriceDiscount() {
                return priceDiscount;
            }

            public void setPriceDiscount(int priceDiscount) {
                this.priceDiscount = priceDiscount;
            }

            public int getVipPriceDiscount() {
                return vipPriceDiscount;
            }

            public void setVipPriceDiscount(int vipPriceDiscount) {
                this.vipPriceDiscount = vipPriceDiscount;
            }

            public int getPointDiscount() {
                return pointDiscount;
            }

            public void setPointDiscount(int pointDiscount) {
                this.pointDiscount = pointDiscount;
            }

            public int getVipPointDiscount() {
                return vipPointDiscount;
            }

            public void setVipPointDiscount(int vipPointDiscount) {
                this.vipPointDiscount = vipPointDiscount;
            }

            public long getRealDuration() {
                return realDuration;
            }

            public void setRealDuration(long realDuration) {
                this.realDuration = realDuration;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getFocusIcon() {
                return focusIcon;
            }

            public void setFocusIcon(String focusIcon) {
                this.focusIcon = focusIcon;
            }

            public Object getPrdSource() {
                return prdSource;
            }

            public void setPrdSource(Object prdSource) {
                this.prdSource = prdSource;
            }

            public Object getExtCode() {
                return extCode;
            }

            public void setExtCode(Object extCode) {
                this.extCode = extCode;
            }

            public ActivityBean getActivity() {
                return activity;
            }

            public void setActivity(ActivityBean activity) {
                this.activity = activity;
            }

            @Override
            public String toString() {
                return "PricesBean{" +
                        "id=" + id +
                        ", prdId=" + prdId +
                        ", name='" + name + '\'' +
                        ", price=" + price +
                        ", vipPrice=" + vipPrice +
                        ", point=" + point +
                        ", vipPoint=" + vipPoint +
                        ", duration=" + duration +
                        ", memo='" + memo + '\'' +
                        ", priceDiscount=" + priceDiscount +
                        ", vipPriceDiscount=" + vipPriceDiscount +
                        ", pointDiscount=" + pointDiscount +
                        ", vipPointDiscount=" + vipPointDiscount +
                        ", realDuration=" + realDuration +
                        ", icon='" + icon + '\'' +
                        ", focusIcon='" + focusIcon + '\'' +
                        ", prdSource=" + prdSource +
                        ", extCode=" + extCode +
                        ", activity=" + activity +
                        '}';
            }

            public static class ActivityBean implements Serializable {
                /**
                 * id : 48
                 * prcId : 80
                 * name : 春节打2折
                 * actType : DISCOUNT
                 * startTime : 1519862400000
                 * endTime : 1617148800000
                 * detailType : PERCENTAGE
                 * percentage : 20
                 * giveHour : null
                 * givePrcId : null
                 * price : null
                 * vipPrice : null
                 * point : null
                 * vipPoint : null
                 * memo : 打折2
                 * icon : http://bzo.cloud.ottcn.com/tiger/imgs/1521010606552.png
                 * focusIcon : http://bzo.cloud.ottcn.com/tiger/imgs/1521010608838.png
                 * product : null
                 */

                private int id;
                private int prcId;
                private String name;
                private String actType;
                private long startTime;
                private long endTime;
                private String detailType;
                private int percentage;
                private Object giveHour;
                private Object givePrcId;
                private Object price;
                private Object vipPrice;
                private Object point;
                private Object vipPoint;
                private String memo;
                private String icon;
                private String focusIcon;
                private Object product;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getPrcId() {
                    return prcId;
                }

                public void setPrcId(int prcId) {
                    this.prcId = prcId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getActType() {
                    return actType;
                }

                public void setActType(String actType) {
                    this.actType = actType;
                }

                public long getStartTime() {
                    return startTime;
                }

                public void setStartTime(long startTime) {
                    this.startTime = startTime;
                }

                public long getEndTime() {
                    return endTime;
                }

                public void setEndTime(long endTime) {
                    this.endTime = endTime;
                }

                public String getDetailType() {
                    return detailType;
                }

                public void setDetailType(String detailType) {
                    this.detailType = detailType;
                }

                public int getPercentage() {
                    return percentage;
                }

                public void setPercentage(int percentage) {
                    this.percentage = percentage;
                }

                public Object getGiveHour() {
                    return giveHour;
                }

                public void setGiveHour(Object giveHour) {
                    this.giveHour = giveHour;
                }

                public Object getGivePrcId() {
                    return givePrcId;
                }

                public void setGivePrcId(Object givePrcId) {
                    this.givePrcId = givePrcId;
                }

                public Object getPrice() {
                    return price;
                }

                public void setPrice(Object price) {
                    this.price = price;
                }

                public Object getVipPrice() {
                    return vipPrice;
                }

                public void setVipPrice(Object vipPrice) {
                    this.vipPrice = vipPrice;
                }

                public Object getPoint() {
                    return point;
                }

                public void setPoint(Object point) {
                    this.point = point;
                }

                public Object getVipPoint() {
                    return vipPoint;
                }

                public void setVipPoint(Object vipPoint) {
                    this.vipPoint = vipPoint;
                }

                public String getMemo() {
                    return memo;
                }

                public void setMemo(String memo) {
                    this.memo = memo;
                }

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public String getFocusIcon() {
                    return focusIcon;
                }

                public void setFocusIcon(String focusIcon) {
                    this.focusIcon = focusIcon;
                }

                public Object getProduct() {
                    return product;
                }

                public void setProduct(Object product) {
                    this.product = product;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ProductPricesInfo{" +
                "response=" + response +
                ", statusCode='" + statusCode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
