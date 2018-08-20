package tv.newtv.cboxtv.uc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lin on 2018/2/27.
 */
public class FavoriteBean implements Parcelable {
    private String contentUUId;

    private String contentType;

    private String name;

    private String poster;

    public FavoriteBean(String contentUUId, String contentType, String name, String poster) {
        this.contentUUId = contentUUId;
        this.contentType = contentType;
        this.name = name;
        this.poster = poster;
    }

    public FavoriteBean() {

    }

    protected FavoriteBean(Parcel in) {
        contentUUId = in.readString();
        contentType = in.readString();
        name = in.readString();
        poster = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contentUUId);
        dest.writeString(contentType);
        dest.writeString(name);
        dest.writeString(poster);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavoriteBean> CREATOR = new Creator<FavoriteBean>() {
        @Override
        public FavoriteBean createFromParcel(Parcel in) {
            return new FavoriteBean(in);
        }

        @Override
        public FavoriteBean[] newArray(int size) {
            return new FavoriteBean[size];
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

    @Override
    public String toString() {
        return "FavoriteBean{" +
                "contentUUId='" + contentUUId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", name='" + name + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }
}
