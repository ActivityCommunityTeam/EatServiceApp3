package com.dijiaapp.eatserviceapp.network.api;

import com.dijiaapp.eatserviceapp.update.UpdateMsg;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by johe on 2016/12/29.
 */

public interface UpdateService {

    @GET("appversion/getAppVersion/{hotelId}")
    Observable<UpdateMsg> getAppVersion(@Path("hotelId") long hotelId );

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

}
