package com.dijiaapp.eatserviceapp.network.api;

import com.dijiaapp.eatserviceapp.data.UserInfo;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wjy on 16/9/8.
 */
public interface UserService {
    @FormUrlEncoded
    @POST("login/login")
    Observable<UserInfo> login(@Field("userName") String name, @Field("password") String password);
}
