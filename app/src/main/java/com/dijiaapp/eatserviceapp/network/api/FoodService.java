package com.dijiaapp.eatserviceapp.network.api;

import com.dijiaapp.eatserviceapp.data.FoodType;
import com.dijiaapp.eatserviceapp.data.MemoryCode;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wjy on 16/9/26.
 */

public interface FoodService {

    @GET("dishes/getDishesList/{id}")
    Observable<List<FoodType>> listFoods(@Path("id") long id);

    @GET("dishes/getMemoryCode/{hotelId} ")
    Observable<List<MemoryCode>> getMemoryCode(@Path("hotelId") long hotelId);


}
