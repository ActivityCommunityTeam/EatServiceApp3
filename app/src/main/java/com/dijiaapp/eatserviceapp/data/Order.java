package com.dijiaapp.eatserviceapp.data;

import java.util.List;

/**
 * Created by wjy on 16/9/30.
 */

public class Order {
    private long hotelId;
    private long userId;
    private double ordreTotal;
    private String statusId;
    private int dinnerNum;
    private String seatName;
    private String remark;
    private List<OrderDishes> dishes;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getOrdreTotal() {
        return ordreTotal;
    }

    public void setOrdreTotal(double ordreTotal) {
        this.ordreTotal = ordreTotal;
    }

    public int getDinnerNum() {
        return dinnerNum;
    }

    public void setDinnerNum(int dinnerNum) {
        this.dinnerNum = dinnerNum;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderDishes> getDishes() {
        return dishes;
    }

    public void setDishes(List<OrderDishes> dishes) {
        this.dishes = dishes;
    }

    @Override
    public String toString() {
        return "Order{" +
                "hotelId=" + hotelId +
                ", userId=" + userId +
                ", ordreTotal=" + ordreTotal +
                ", dinnerNum=" + dinnerNum +
                ", seatName='" + seatName + '\'' +
                ", remark='" + remark + '\'' +
                ", dishes=" + dishes +
                '}';
    }
}
