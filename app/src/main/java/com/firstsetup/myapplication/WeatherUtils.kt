package com.firstsetup.myapplication

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

object WeatherUtils {
    fun checkRainInCity(context: Context, city: String, onRainDetected: () -> Unit) {
        val apiKey = WeatherConfig.API_KEY
        val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=fr"

        val request = JsonObjectRequest(
            Request.Method.GET, weatherUrl, null,
            { response ->
                try {
                    val weatherArray = response.getJSONArray("weather")
                    val weatherMain = weatherArray.getJSONObject(0).getString("main")

                    if (weatherMain.equals("Rain", true)
                        || weatherMain.equals("Drizzle", true)
                        || weatherMain.equals("Thunderstorm", true)) {
                        onRainDetected()
                    }
                } catch (e: Exception) {
                    Log.e("WEATHER", "Parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("WEATHER", "Erreur météo: ${error.message}")
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}
