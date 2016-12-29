package com.dijiaapp.eatserviceapp.update;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johe on 2016/12/29.
 */

public class DownloadProgress implements Parcelable {//序列化

    private int progress;//下载进度
    private long currentFileSize;//已下载大小
    private long totalFileSize;//总大小

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public DownloadProgress() {
    }

    protected DownloadProgress(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Parcelable.Creator<DownloadProgress> CREATOR = new Parcelable.Creator<DownloadProgress>() {
        @Override
        public DownloadProgress createFromParcel(Parcel source) {
            return new DownloadProgress(source);
        }

        @Override
        public DownloadProgress[] newArray(int size) {
            return new DownloadProgress[size];
        }
    };
}
