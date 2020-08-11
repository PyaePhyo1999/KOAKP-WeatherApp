package com.example.koakptutorial.data

import com.example.koakptutorial.utils.Util
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AppInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
        val newUrl = url.newBuilder()
            .addQueryParameter("appid",Util.API_KEY)
            .addQueryParameter("units",Util.WEATHER_UNIT)
            .build()
        val request = Request.Builder().url(newUrl).build()

        return chain.proceed(request)
    }


}