package com.dijiaapp.eatserviceapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wjy on 16/9/30.
 */

public class OrderInfo implements Parcelable {
    private long userId;
    private String waiterName;
    private String orderHeaderNo;
    private long orderId;
    private int dinnerNum;
    private String statusId;
    private String seatName; //实际上是seatId;

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public String getOrderHeaderNo() {
        return orderHeaderNo;
    }

    public void setOrderHeaderNo(String orderHeaderNo) {
        this.orderHeaderNo = orderHeaderNo;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getDinnerNum() {
        return dinnerNum;
    }

    public void setDinnerNum(int dinnerNum) {
        this.dinnerNum = dinnerNum;
    }
    @Override
    public String toString() {
        return "OrderInfo{" +
                "userId=" + userId +
                ", waiterName='" + waiterName + '\'' +
                ", orderHeaderNo='" + orderHeaderNo + '\'' +
                ", orderId=" + orderId +
                ", dinnerNum=" + dinnerNum +
                ", statusId='" + statusId + '\'' +
                ", seatId=" + seatName +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.waiterName);
        dest.writeString(this.orderHeaderNo);
        dest.writeLong(this.orderId);
        dest.writeInt(this.dinnerNum);
        dest.writeString(this.statusId);
        dest.writeString(this.seatName);
    }

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        this.userId = in.readLong();
        this.waiterName = in.readString();
        this.orderHeaderNo = in.readString();
        this.orderId = in.readLong();
        this.dinnerNum = in.readInt();
        this.statusId = in.readString();
        this.seatName = in.readString();
    }

    public static final Parcelable.Creator<OrderInfo> CREATOR = new Parcelable.Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };
}
