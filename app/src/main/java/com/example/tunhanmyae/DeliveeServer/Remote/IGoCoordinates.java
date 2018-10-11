package com.example.tunhanmyae.DeliveeServer.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoCoordinates {
    @GET("maps/api/geocode/json")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("maps/api/geocode/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);

}
