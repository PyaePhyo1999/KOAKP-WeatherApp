package com.example.koakptutorial

import com.example.koakptutorial.data.AppInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private var retrofit : Retrofit? = null
    fun instance() : Retrofit{
        if (retrofit==null){
             val okHttpClientBuilder = OkHttpClient.Builder()
             val addInterceptor = AppInterceptor()
             okHttpClientBuilder.addInterceptor(addInterceptor)

            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build()
        }

        return retrofit!!
    }

}
