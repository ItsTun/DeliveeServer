package com.example.tunhanmyae.DeliveeServer.Remote;

import com.example.tunhanmyae.DeliveeServer.model.MyResponse;
import com.example.tunhanmyae.DeliveeServer.model.Sender;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-type:application/json",
                    "Authorization-key=AAAA0iw_r7o:APA91bG38ijW_7inEj7YUpsaUl3abpGkqtqpvP3sWvYNHnXCtXyV-5jPODDF1AYFl7uULu5ZDMFFMNV3cGGF0HAPpGZvx7yGIMt8HQLeJFAiRH4_pb8_ajBThD4qeZu860eKsQ6ZTXzRBesKfhk5NS8mz8GN6wr0-A"


            }
    )
    @POST("fcm/send")
    retrofit2.Call<MyResponse> sendNotification(@Body Sender body);
}
