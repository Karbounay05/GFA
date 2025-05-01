package com.firstsetup.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weatherText: TextView
    private lateinit var lottieWeather: LottieAnimationView

    companion object {
        private const val API_KEY = "e8fb962960b460360ed7c6080e38db24"
        private const val CITY_NAME = "Rabat"
    }

    private fun getWeatherUrl(): String {
        return "https://api.openweathermap.org/data/2.5/weather?q=$CITY_NAME&appid=$API_KEY&units=metric&lang=fr"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acceuil_activity)
        weatherText = findViewById(R.id.weatherText)
        lottieWeather = findViewById(R.id.lottieWeather)

        fetchWeather()
    }

    private fun fetchWeather() {
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, getWeatherUrl(), null,
            { response ->
                try {
                    Log.d("WEATHER_RAW", response.toString())

                    val main = response.optJSONObject("main")
                    val temp = main?.optDouble("temp", 0.0)
                    val humidity = main?.optInt("humidity", 0)

                    val weatherArray = response.optJSONArray("weather")
                    val description = weatherArray?.optJSONObject(0)?.optString("description", "Pas de données")
                    val mainWeather = weatherArray?.optJSONObject(0)?.optString("main", "")

                    val wind = response.optJSONObject("wind")
                    val windSpeed = wind?.optDouble("speed", 0.0)

                    val sys = response.optJSONObject("sys")
                    val now = response.optLong("dt")
                    val sunrise = sys?.optLong("sunrise", 0) ?: 0
                    val sunset = sys?.optLong("sunset", 0) ?: 0
                    val isNight = now < sunrise || now > sunset

                    Log.d("TIME", "now=$now, sunrise=$sunrise, sunset=$sunset")
                    Log.d("ANIM_LOGIC", "main=$mainWeather, isNight=$isNight")

                    val text = "Temp: $temp°C\n" +
                            "Description: $description\n" +
                            "Vent: $windSpeed m/s\n" +
                            "Humidité: $humidity%"
                    weatherText.text = text

                    if (mainWeather != null) afficherAnimation(mainWeather, isNight)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erreur parsing météo", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode ?: -1
                Log.e("WEATHER", "Erreur réseau: ${error.message} (code: $statusCode)")
                Toast.makeText(this, "Erreur réseau : code $statusCode", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }

    private fun afficherAnimation(main: String, isNight: Boolean) {
        val anim = when {
            isNight && main.contains("Clear", true) -> R.raw.night
            main.contains("Cloud", true) -> R.raw.cloud
            main.contains("Rain", true) -> R.raw.rain
            main.contains("Storm", true) -> R.raw.storm
            else -> if (isNight) R.raw.night else R.raw.sun
        }
        lottieWeather.setAnimation(anim)
        lottieWeather.playAnimation()
    }
}
