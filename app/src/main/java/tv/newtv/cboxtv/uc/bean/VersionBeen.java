package tv.newtv.cboxtv.uc.bean;

/**
 * Created by Administrator on 2018/4/19.
 */

public class VersionBeen

{

    /**
     * versionCode : 123456
     * versionName : 第二版
     * versionDescription : 微信的第二个版本
     * packageSize : 22222
     * packageAddr : http://www.suibianxiede.com
     * packageMD5 : 39deb1aa4294a0d200222ae10b1d9e83
     * upgradeType : 1
     * channelId : 1
     * channelCode : 105671
     * appKey : 894b4a5f3abdcaee3be27d7bf2d40064
     */

    private int versionCode;
    private String versionName;
    private String versionDescription;
    private int packageSize;
    private String packageAddr;
    private String packageMD5;
    private int upgradeType;
    private int channelId;
    private String channelCode;
    private String appKey;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public String getPackageAddr() {
        return packageAddr;
    }

    public void setPackageAddr(String packageAddr) {
        this.packageAddr = packageAddr;
    }

    public String getPackageMD5() {
        return packageMD5;
    }

    public void setPackageMD5(String packageMD5) {
        this.packageMD5 = packageMD5;
    }

    public int getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(int upgradeType) {
        this.upgradeType = upgradeType;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
