package com.dijiaapp.eatserviceapp.data;

/**
 * Created by wjy on 16/9/30.
 */
public class OrderDishes {
    private long dishesId;
    private String dishesName;
    private int orderNum;
    private double dishesPrice;
    private double totalPrice;
    private String dishesUnit;

    public long getDishesId() {
        return dishesId;
    }

    public void setDishesId(long dishesId) {
        this.dishesId = dishesId;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public double getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(double dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDishesUnit() {
        return dishesUnit;
    }

    public void setDishesUnit(String dishesUnit) {
        this.dishesUnit = dishesUnit;
    }

    @Override
    public String toString() {
        return "OrderDishes{" +
                "dishesId=" + dishesId +
                ", dishesName='" + dishesName + '\'' +
                ", orderNum=" + orderNum +
                ", dishesPrice=" + dishesPrice +
                ", totalPrice=" + totalPrice +
                ", dishesUnit='" + dishesUnit + '\'' +
                '}';
    }
}
