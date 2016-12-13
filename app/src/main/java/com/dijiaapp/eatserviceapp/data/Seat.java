package com.dijiaapp.eatserviceapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wjy on 16/8/15.
 */
public class Seat implements Parcelable {
    /**
     * seatType : 01
     * containNum : 6
     * seatId : 1
     * seatName : 座位01
     * useStatus : 01 (01:可以点餐，02：正在点餐)
     */
    private String seatType;
    private int containNum;
    private int seatId;
    private String seatName;
    private String useStatus;

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getContainNum() {
        return containNum;
    }

    public void setContainNum(int containNum) {
        this.containNum = containNum;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatType='" + seatType + '\'' +
                ", containNum=" + containNum +
                ", seatId=" + seatId +
                ", seatName='" + seatName + '\'' +
                ", useStatus='" + useStatus + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.seatType);
        dest.writeInt(this.containNum);
        dest.writeInt(this.seatId);
        dest.writeString(this.seatName);
        dest.writeString(this.useStatus);
    }

    public Seat() {
    }

    protected Seat(Parcel in) {
        this.seatType = in.readString();
        this.containNum = in.readInt();
        this.seatId = in.readInt();
        this.seatName = in.readString();
        this.useStatus = in.readString();
    }

    public static final Parcelable.Creator<Seat> CREATOR = new Parcelable.Creator<Seat>() {
        @Override
        public Seat createFromParcel(Parcel source) {
            return new Seat(source);
        }

        @Override
        public Seat[] newArray(int size) {
            return new Seat[size];
        }
    };
}
