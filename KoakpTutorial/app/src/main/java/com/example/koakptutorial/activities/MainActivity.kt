package com.example.koakptutorial.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.koakptutorial.R
import com.example.koakptutorial.data.OpenWeatherMapApi
import com.example.koakptutorial.data.OpenWeatherMapResponse
import com.example.koakptutorial.utils.Util
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private val pbLoading by lazy {
        findViewById<ProgressBar>(R.id.pbLoading)
    }
    private val tvTemperature by lazy {
        findViewById<TextView>(R.id.tvTemperature)
    }
    private val tvCity by lazy {
        findViewById<TextView>(R.id.tvCity)
    }
    private val ivWeatherStatus by lazy {
        findViewById<ImageView>(R.id.ivWeatherStatus)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.i("MainActivity.onCreate", "Permission Granted")
                    getLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.i("MainActivity.onCreate", "Permission Shown")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.i("MainActivity.onCreate", "Permission Denied")
                }

            }
            ).check()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        val locationManager =
            this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.i("MainActivity.onCreate", location?.latitude.toString())
        Toast.makeText(this,location?.latitude.toString(),Toast.LENGTH_SHORT).show()

        executeNetwork(
            latitude = location?.latitude.toString(),
            longitude = location?.longitude.toString()
        )
    }

    private fun showLoading() {
        pbLoading.visibility = View.VISIBLE
        tvTemperature.visibility = View.GONE
        tvCity.visibility = View.GONE
        ivWeatherStatus.visibility = View.GONE

    }

    private fun showData(
        temperature: String,
        city: String,
        weatherIcon: String
    ) {
        tvTemperature.text = temperature
        tvCity.text = city

        pbLoading.visibility = View.GONE
        tvTemperature.visibility = View.VISIBLE
        tvCity.visibility = View.VISIBLE
        ivWeatherStatus.visibility = View.VISIBLE

    }


    private fun executeNetwork(latitude: String, longitude: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)
        openWeatherMapApi.geoCoordinate(
            latitude = latitude,
            longitude = longitude,
            appId = Util.API_KEY
        )
            .enqueue(object : Callback<OpenWeatherMapResponse> {
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { openWeatherResponse ->
                            Log.i("response ", openWeatherResponse.toString())
                            showData(
                                temperature = openWeatherResponse.main.temp,
                                city = openWeatherResponse.name,
                                weatherIcon = openWeatherResponse.weatherList.getOrNull(0)?.icon
                                    ?: ""
                            )
                        }

                    }
                }

            })
    }

}