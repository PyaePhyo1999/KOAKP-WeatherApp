package com.example.koakptutorial.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

    @GET("weather")
    fun geoCoordinate
                (@Query("lat") latitude : String,
                 @Query("lon") longitude : String

                ) : Call<OpenWeatherMapResponse>

    @GET("weather")
    fun getByCityName
                (@Query("q") cityName : String

    ) : Call<OpenWeatherMapResponse>


}