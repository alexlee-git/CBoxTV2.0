package tv.newtv.cboxtv.uc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lin on 2018/3/1.
 */

public class UserBean implements Parcelable {
    private String uuid;
    private String customer_id;
    private String username;
    private String email;
    private String mobile;
    private String nickname;
    private String avatar;
    private String sex;
    private String description;
    private String credit;

    public UserBean() {

    }

    protected UserBean(Parcel in) {
        uuid = in.readString();
        customer_id = in.readString();
        username = in.readString();
        email = in.readString();
        mobile = in.readString();
        nickname = in.readString();
        avatar = in.readString();
        sex = in.readString();
        description = in.readString();
        credit = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(customer_id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(nickname);
        dest.writeString(avatar);
        dest.writeString(sex);
        dest.writeString(description);
        dest.writeString(credit);
    }
}
