package com.dijiaapp.eatserviceapp.data;

/**
 * Created by Daniel on 2016/12/27.
 */

public class MemoryCode {

    /**
     * dishesName : 澳门烤肉
     * memoryCode : 449
     * id : 10000264
     * dishesUnit : 份
     */

    private String dishesName;
    private String memoryCode;
    private int id;
    private String dishesUnit;

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
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
}
