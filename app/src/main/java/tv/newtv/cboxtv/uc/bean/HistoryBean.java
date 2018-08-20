package tv.newtv.cboxtv.uc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lin on 2018/2/27.
 */

public class HistoryBean implements Parcelable {
    private String contentUUId;

    private String contentType;

    private String name;

    private String poster;

    private String currentTime;

    private String currentNum;

    private String totalNum;

    public HistoryBean(String contentUUId, String contentType, String name, String poster, String currentTime, String currentNum, String totalNum) {
        this.contentUUId = contentUUId;
        this.contentType = contentType;
        this.name = name;
        this.poster = poster;
        this.currentTime = currentTime;
        this.currentNum = currentNum;
        this.totalNum = totalNum;
    }

    public HistoryBean() {

    }

    protected HistoryBean(Parcel in) {
        contentUUId = in.readString();
        contentType = in.readString();
        name = in.readString();
        poster = in.readString();
        currentTime = in.readString();
        currentNum = in.readString();
        totalNum = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contentUUId);
        dest.writeString(contentType);
        dest.writeString(name);
        dest.writeString(poster);
        dest.writeString(currentTime);
        dest.writeString(currentNum);
        dest.writeString(totalNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryBean> CREATOR = new Creator<HistoryBean>() {
        @Override
        public HistoryBean createFromParcel(Parcel in) {
            return new HistoryBean(in);
        }

        @Override
        public HistoryBean[] newArray(int size) {
            return new HistoryBean[size];
        }
    };

    public String getContentUUId() {
        return contentUUId;
    }

    public void setContentUUId(String contentUUId) {
        this.contentUUId = contentUUId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(String currentNum) {
        this.currentNum = currentNum;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    @Override
    public String toString() {
        return "HistoryBean{" +
                "contentUUId='" + contentUUId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", name='" + name + '\'' +
                ", poster='" + poster + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", currentNum='" + currentNum + '\'' +
                ", totalNum='" + totalNum + '\'' +
                '}';
    }
}
