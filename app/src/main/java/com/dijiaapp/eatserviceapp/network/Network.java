package com.dijiaapp.eatserviceapp.network;

import com.dijiaapp.eatserviceapp.network.api.FoodService;
import com.dijiaapp.eatserviceapp.network.api.OrderService;
import com.dijiaapp.eatserviceapp.network.api.SeatSevice;
import com.dijiaapp.eatserviceapp.network.api.UpdateService;
import com.dijiaapp.eatserviceapp.network.api.UserService;

import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wjy on 16/9/1.
 */
public class Network {
    private static SeatSevice seatSevice;
    private static UserService userService;
    private static FoodService foodService;
    private static OrderService orderService;
    private  static UpdateService updateService;

    public static OrderService getOrderService() {
        if (orderService == null) {
            Retrofit retrofit = getRetrofit();
            orderService = retrofit.create(OrderService.class);
        }
        return orderService;
    }
    public static UpdateService getUpdateService() {
        if (updateService == null) {
            Retrofit retrofit = getRetrofit();
            updateService = retrofit.create(UpdateService.class);
        }
        return updateService;
    }
    public static FoodService getFoodService() {
        if (foodService == null) {
            Retrofit retrofit = getRetrofit();
            foodService = retrofit.create(FoodService.class);
        }
        return foodService;
    }

    public static SeatSevice getSeatService() {
        if (seatSevice == null) {
            Retrofit retrofit = getRetrofit();
            seatSevice = retrofit.create(SeatSevice.class);

        }
        return seatSevice;
    }

    public static UserService getUserService() {
        if (userService == null) {
            Retrofit retrofit = getRetrofit();
            userService = retrofit.create(UserService.class);
        }
        return userService;
    }

    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
//                .baseUrl("http://211.149.235.17:8080/dcb/")//测试库
                .baseUrl("http://120.55.163.4:8080/dcb/")//正式库
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();

    }

}
