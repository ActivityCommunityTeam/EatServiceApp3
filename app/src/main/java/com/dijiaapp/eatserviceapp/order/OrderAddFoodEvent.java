package com.dijiaapp.eatserviceapp.order;

import com.dijiaapp.eatserviceapp.data.OrderInfo;

/**
 * Created by wjy on 2016/11/10.
 */
public class OrderAddFoodEvent {
    private OrderInfo orderInfo;
    public OrderAddFoodEvent(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }
}
