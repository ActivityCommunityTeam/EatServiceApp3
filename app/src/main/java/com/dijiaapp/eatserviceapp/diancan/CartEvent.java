package com.dijiaapp.eatserviceapp.diancan;

import com.dijiaapp.eatserviceapp.data.DishesListBean;

/**
 * Created by wjy on 16/9/28.
 *
 */
public class CartEvent {
    private int flag;
    private int disesBeanId;
    public CartEvent(int i, int disesBeanId) {
        flag = i;
        this.disesBeanId = disesBeanId;
    }

    public int getFlag() {
        return flag;
    }

    public int getDisesBeanId() {
        return disesBeanId;
    }
}
