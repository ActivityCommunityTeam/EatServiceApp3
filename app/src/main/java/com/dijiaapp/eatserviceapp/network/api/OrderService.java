package com.dijiaapp.eatserviceapp.network.api;

import com.dijiaapp.eatserviceapp.data.Order;
import com.dijiaapp.eatserviceapp.data.OrderInfo;
import com.dijiaapp.eatserviceapp.data.ResultInfo;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wjy on 16/9/30.
 */

public interface OrderService {
    /**
     * 提交订单
     *
     * @param hotelId
     * @param userId
     * @param orderTotal
     * @param number
     * @param seatName
     * @param dishes
     * @return
     */
    @POST("order/save")
    @FormUrlEncoded
    Observable<ResultInfo> saveOrder(@Field("hotelId") long hotelId, @Field("userId") long userId, @Field("orderTotal") double orderTotal, @Field("dinnerNum") int number,
                                     @Field("seatName") String seatName, @Field("dishes") String dishes);

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @GET("order/getOrderDetail/{orderId}")
    Observable<Order> orderDetail(@Path("orderId") long orderId);

    /**
     * 订单列表
     *
     * @param hotelId
     * @return
     */
    @GET("order/getOrderItem/{hotelId}")
    Observable<List<OrderInfo>> listOrder(@Path("hotelId") long hotelId);

    @FormUrlEncoded
    @POST("order/addDishes")
    Observable<ResultInfo> addDishes(@Field("orderId") long orderId, @Field("orderTotal") Double orderTotal, @Field("dishes") String dishes);
}
