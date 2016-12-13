package com.dijiaapp.eatserviceapp.order;

import com.dijiaapp.eatserviceapp.data.OrderInfo;

/**
 * Created by wjy on 2016/11/8.
 */
public class OrderOverEvent {
    OrderInfo orderInfo ;
    public OrderOverEvent(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }
}
