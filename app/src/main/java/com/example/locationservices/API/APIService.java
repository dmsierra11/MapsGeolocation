package com.example.locationservices.API;

import com.example.locationservices.API.classes.RouteList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by VCHI on 10/5/16.
 */
public interface APIService {

    @GET("json")
    Call<RouteList> getRoutes(@Query("origin") String origin, @Query("destination") String APPID,
                                   @Query("sensor") boolean sensor);
}
