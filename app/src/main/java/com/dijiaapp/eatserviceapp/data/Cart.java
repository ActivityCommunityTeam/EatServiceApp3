package com.dijiaapp.eatserviceapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static android.R.attr.id;

/**
 * Created by wjy on 16/9/28.
 * 购物车
 */

public class Cart extends RealmObject {
    private double money;
    private int amount;
    private DishesListBean dishesListBean;
    private int seatId;
    private long time;


    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public DishesListBean getDishesListBean() {
        return dishesListBean;
    }

    public void setDishesListBean(DishesListBean dishesListBean) {
        this.dishesListBean = dishesListBean;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public int getSeatId() {
        return seatId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "money=" + money +
                ", amount=" + amount +
                ", dishesListBean=" + dishesListBean +
                ", seatId=" + seatId +
                ", time=" + time +
                '}';
    }
}
