package com.example.koakptutorial.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

    @GET("weather")
    fun geoCoordinate
                (@Query("lat") latitude : String,
                 @Query("lon") longitude : String,
                 @Query("appid") appId : String
                ) : Call<OpenWeatherMapResponse>


}