package tv.newtv.cboxtv.uc.bean;

/**
 * 项目名称： CBoxTV2.0
 * 类描述：用户会员信息实体类
 * 创建人：wqs
 * 创建时间： 2018/9/13 0013 16:48
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MemberInfoBean {

    /**
     * id : 1
     * appKey : 543cc33ebea8e0dfe25fd24b3c67c5b2
     * userId : 1
     * productId : 1
     * expireTime : 2018-08-24 17:23:02
     */

    private int id;
    private String appKey;
    private int userId;
    private int productId;
    private String expireTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
