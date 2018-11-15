package tv.newtv.ottlauncher.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by caolonghe on 2018/11/2.
 */

public final class History implements Serializable {

    private final String contentUUId;

    private final String contentType;

    private final String contentName;

    private final String contentImgUrl;

    private final String packageName;

    private final String className;

    private final String extent1;

    private final String extent2;

    private final long updateTime;


    public History(@NonNull String contentUUId, String contentType, String contentName, String contentImgUrl, @NonNull String packageName, @NonNull String className, @NonNull String extent1, @NonNull String extent2, long updateTime) {
        this.contentUUId = contentUUId;
        this.contentType = contentType;
        this.contentName = contentName;
        this.contentImgUrl = contentImgUrl;
        this.packageName = packageName;
        this.className = className;
        this.extent1 = extent1;
        this.extent2 = extent2;
        this.updateTime = updateTime;
    }

    @NonNull
    public String getContentUUId() {
        return contentUUId;
    }

    @Nullable
    public String getContentType() {
        return contentType;
    }

    @Nullable
    public String getContentName() {
        return contentName;
    }

    @Nullable
    public String getContentImgUrl() {
        return contentImgUrl;
    }


    @NonNull
    public String getPackageName() {
        return packageName;
    }

    @NonNull
    public String getClassName() {
        return className;
    }

    @NonNull
    public String getExtent1() {
        return extent1;
    }

    @NonNull
    public String getExtent2() {
        return extent2;
    }

    @Nullable
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {
        return "History{" +
                "contentUUId='" + contentUUId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentName='" + contentName + '\'' +
                ", contentImgUrl='" + contentImgUrl + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", extent1='" + extent1 + '\'' +
                ", extent2='" + extent2 + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
