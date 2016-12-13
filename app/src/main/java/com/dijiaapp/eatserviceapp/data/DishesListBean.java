package com.dijiaapp.eatserviceapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wjy on 16/9/23.
 */

public class DishesListBean extends RealmObject{

    /**
     * dishesName : 澳门烤肉
     * onSalePrice : null
     * memoryCode : null
     * id : 10000264
     * dishesUnit : 份
     * dishesPrice : 48
     */

    private String dishesName;
    private double onSalePrice;
    private String memoryCode;
    @PrimaryKey
    private int id;
    private String dishesUnit;
    private double dishesPrice;

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public double getOnSalePrice() {
        return onSalePrice;
    }

    public void setOnSalePrice(double onSalePrice) {
        this.onSalePrice = onSalePrice;
    }

    public String getMemoryCode() {
        return memoryCode;
    }

    public void setMemoryCode(String memoryCode) {
        this.memoryCode = memoryCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDishesUnit() {
        return dishesUnit;
    }

    public void setDishesUnit(String dishesUnit) {
        this.dishesUnit = dishesUnit;
    }

    public double getDishesPrice() {
        return dishesPrice;
    }

    public void setDishesPrice(double dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    @Override
    public String toString() {
        return "DishesListBean{" +
                "dishesName='" + dishesName + '\'' +
                ", onSalePrice=" + onSalePrice +
                ", memoryCode='" + memoryCode + '\'' +
                ", id=" + id +
                ", dishesUnit='" + dishesUnit + '\'' +
                ", dishesPrice=" + dishesPrice +
                '}';
    }
}
