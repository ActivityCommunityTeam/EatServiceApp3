package com.dijiaapp.eatserviceapp.data;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wjy on 16/9/23.
 */

public class FoodType extends RealmObject {

    /**
     * dishesTypeDesc : 美味家常
     * dishesList : [{"dishesName":"澳门烤肉","onSalePrice":null,"memoryCode":null,"id":10000264,"dishesUnit":"份","dishesPrice":48},{"dishesName":"豆角炒肉","onSalePrice":null,"memoryCode":null,"id":10000267,"dishesUnit":null,"dishesPrice":26},{"dishesName":"测试01","onSalePrice":33,"memoryCode":null,"id":10000278,"dishesUnit":"碗","dishesPrice":33},{"dishesName":"测试","onSalePrice":0,"memoryCode":"","id":10000279,"dishesUnit":"份","dishesPrice":0.01},{"dishesName":"cs005","onSalePrice":30,"memoryCode":"445","id":10000280,"dishesUnit":"罐","dishesPrice":60}]
     * showSort : 1
     * id : 10000559
     */

    private String dishesTypeDesc;
    private int showSort;
    @PrimaryKey
    private int id;
    private RealmList<DishesListBean> dishesList;

    public String getDishesTypeDesc() {
        return dishesTypeDesc;
    }

    public void setDishesTypeDesc(String dishesTypeDesc) {
        this.dishesTypeDesc = dishesTypeDesc;
    }

    public int getShowSort() {
        return showSort;
    }

    public void setShowSort(int showSort) {
        this.showSort = showSort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<DishesListBean> getDishesList() {
        return dishesList;
    }

    public void setDishesList(RealmList<DishesListBean> dishesList) {
        this.dishesList = dishesList;
    }

    @Override
    public String toString() {
        return "FoodType{" +
                "dishesTypeDesc='" + dishesTypeDesc + '\'' +
                ", showSort=" + showSort +
                ", id=" + id +
                ", dishesList=" + dishesList +
                '}';
    }
}
