package com.firstsetup.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView

class Acceuil : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPager: ViewPager2

    // Ajouts météo
    private lateinit var weatherText: TextView
    private lateinit var lottieWeather: LottieAnimationView
    private lateinit var spinnerCities: Spinner
    private var selectedCity: String = "Marrakech"

    companion object {
        private const val API_KEY = "e8fb962960b460360ed7c6080e38db24"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acceuil_activity)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        viewPager = findViewById(R.id.viewPager)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val slides = listOf(
            listOf("Gérer la ferme", "Suivre la parcelle", "Diagnostiquer la plante"),
            listOf("Calculer le rendement", "Calculer la superficie", "Assistant AI")
        )

        val adapter = ServicesAdapter(slides)
        viewPager.adapter = adapter

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {}
                R.id.nav_settings -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Initialisation météo
        weatherText = findViewById(R.id.weatherText)
        lottieWeather = findViewById(R.id.lottieWeather)
        spinnerCities = findViewById(R.id.spinnerCities)

        val villes = listOf(
            "Rabat", "Casablanca", "Marrakech", "Fès", "Tanger", "Agadir", "Oujda", "Laâyoune",
            "Tétouan", "Meknès", "Nador", "El Jadida", "Khouribga", "Béni Mellal", "Kénitra",
            "Safi", "Mohammédia", "Errachidia", "Al Hoceïma", "Taroudant", "Guelmim", "Taza",
            "Settat", "Essaouira", "Larache", "Berkane", "Taourirt", "Ouarzazate", "Ksar El Kebir",
            "Tan-Tan", "Dakhla", "Midelt", "Zagora", "Azilal", "Ifrane", "Azemmour", "Sidi Bennour",
            "Tiflet", "Benslimane", "Jerada", "Sidi Kacem", "Sidi Ifni", "Sefrou"
        )

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, villes)
        spinnerCities.adapter = adapterSpinner

        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCity = villes[position]
                fetchWeather()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Autoriser merge paths (pour Android < 5.0)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun hideAccueilButtons() {}
    fun showAccueilButtons() {}

    private fun getWeatherUrl(): String {
        return "https://api.openweathermap.org/data/2.5/weather?q=$selectedCity&appid=$API_KEY&units=metric&lang=fr"
    }

    private fun fetchWeather() {
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, getWeatherUrl(), null,
            { response ->
                try {
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

                    val text = "Ville : $selectedCity\n" +
                            "Temp: $temp°C\n" +
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
