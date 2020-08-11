package com.example.koakptutorial.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.koakptutorial.R
import com.example.koakptutorial.RetrofitInstance
import com.example.koakptutorial.data.OpenWeatherMapApi
import com.example.koakptutorial.data.OpenWeatherMapResponse
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val pbLoading by lazy {
        findViewById<ProgressBar>(R.id.pbLoading)
    }
    private val tvTemperature by lazy {
        findViewById<TextView>(R.id.tvTemperature)
    }
    private val etCityName by lazy {
        findViewById<EditText>(R.id.etCityName)
    }
    private val ivWeatherStatus by lazy {
        findViewById<ImageView>(R.id.ivWeatherStatus)
    }
    private val btnSearch by lazy {
        findViewById<Button>(R.id.btnSearch)
    }
    private val tvError by lazy {
        findViewById<TextView>(R.id.tvError)
    }
    private val btnReset by lazy {
        findViewById<Button>(R.id.btnReset)
    }


    private val retrofit by lazy {
        RetrofitInstance.instance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Weather"


        btnSearch.setOnClickListener {
            val cityName = etCityName.text.toString()
            executeNetwork(cityName)
        }
        btnReset.setOnClickListener {
            getLocation()
        }

        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.i("MainActivity.onCreate", "Permission Granted")
                    val locationManager =
                        this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Log.i("MainActivity.onCreate", location?.latitude.toString())
                    Toast.makeText(
                        applicationContext,
                        location?.latitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    executeNetwork(
                        latitude = location?.latitude.toString(),
                        longitude = location?.longitude.toString()
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                   showError()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                  showError()
                }

            }
            ).check()

    }

    private fun showLoading() {
        pbLoading.visibility = View.VISIBLE
        tvTemperature.visibility = View.GONE
        etCityName.visibility = View.GONE
        ivWeatherStatus.visibility = View.GONE
        btnSearch.visibility = View.GONE

        tvError.visibility=View.GONE
        btnReset.visibility =View.GONE

    }

    private fun showData(
        temperature: String,
        city: String,
        weatherIcon: String
    ) {
        tvTemperature.text = "$temperature°C"
        etCityName.setText(city)
        Glide.with(this).load(weatherIcon).into(ivWeatherStatus)


        pbLoading.visibility = View.GONE
        tvError.visibility=View.GONE
        btnReset.visibility =View.GONE

        tvTemperature.visibility = View.VISIBLE
        etCityName.visibility = View.VISIBLE
        ivWeatherStatus.visibility = View.VISIBLE
        btnSearch.visibility = View.VISIBLE
    }

    private fun showError(){
        pbLoading.visibility = View.GONE
        tvTemperature.visibility = View.GONE
        etCityName.visibility = View.GONE
        ivWeatherStatus.visibility = View.GONE
        btnSearch.visibility = View.GONE

        tvError.visibility = View.VISIBLE
        btnReset.visibility = View.VISIBLE
    }
    private fun executeNetwork(cityName: String) {
        showLoading()
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)
        openWeatherMapApi.getByCityName(
            cityName = cityName
        )
            .enqueue(object : Callback<OpenWeatherMapResponse> {
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                   showError()
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { openWeatherResponse ->
                            Log.i("response ", openWeatherResponse.toString())
                            val iconUrl = openWeatherResponse.weatherList.getOrNull(0)?.icon
                                ?: ""
                            val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"

                            showData(
                                temperature = openWeatherResponse.main.temp,
                                city = openWeatherResponse.name,
                                weatherIcon = fullUrl
                            )
                        }

                    } else {
                        showError()
                    }
                }

            })
    }

    private fun executeNetwork(latitude: String, longitude: String) {
        showLoading()

        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)
        openWeatherMapApi.geoCoordinate(
            latitude = latitude,
            longitude = longitude
        )
            .enqueue(object : Callback<OpenWeatherMapResponse> {
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                    showError()
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { openWeatherResponse ->
                            Log.i("response ", openWeatherResponse.toString())
                            val iconUrl = openWeatherResponse.weatherList.getOrNull(0)?.icon
                                ?: ""
                            val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"

                            showData(
                                temperature = openWeatherResponse.main.temp,
                                city = openWeatherResponse.name,
                                weatherIcon = fullUrl
                            )
                        }

                    }else{
                        showError()
                    }
                }

            })
    }

}