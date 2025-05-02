package com.firstsetup.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var weatherText: TextView
    private lateinit var lottieWeather: LottieAnimationView

    companion object {
        private const val API_KEY = "132b349bd472e322f99891150708a288" // Ta vraie API key ici
        private const val CITY_NAME = "Rabat"
        private const val URL = "https://api.openweathermap.org/data/2.5/weather?q=$CITY_NAME&appid=$API_KEY&units=metric"
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

        val request = JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                try {
                    Log.d("WEATHER", "Réponse reçue: $response") // Pour debug

                    val main = response.optJSONObject("main")
                    val temp = main?.optDouble("temp", 0.0)
                    val humidity = main?.optInt("humidity", 0)

                    val weatherArray = response.optJSONArray("weather")
                    val description =
                        weatherArray?.optJSONObject(0)?.optString("description", "Pas de données")

                    val wind = response.optJSONObject("wind")
                    val windSpeed = wind?.optDouble("speed", 0.0)

                    val text = "Temp: $temp°C\n" +
                            "Description: $description\n" +
                            "Vent: $windSpeed m/s\n" +
                            "Humidité: $humidity%"
                    weatherText.text = text

                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    val isNight = hour < 6 || hour > 18

                    if (description != null) afficherAnimation(description, isNight)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Erreur lors du traitement des données météo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { error ->
                Log.e("WEATHER", "Erreur de connexion: ${error.message}")
                Toast.makeText(this, "Erreur de connexion à l'API météo", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }

    private fun afficherAnimation(description: String, isNight: Boolean) {
        val anim = when {
            isNight && description.contains("clear", true) -> R.raw.night
            description.contains("cloud", true) -> R.raw.cloud
            description.contains("rain", true) -> R.raw.rain
            description.contains("storm", true) -> R.raw.storm
            else -> if (isNight) R.raw.night else R.raw.sun
        }
        lottieWeather.setAnimation(anim)
        lottieWeather.playAnimation()
    }
}