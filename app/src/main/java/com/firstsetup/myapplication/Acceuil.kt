package com.firstsetup.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest

class Acceuil : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acceuil_activity)
        NotificationHelper.createNotificationChannel(this)
        checkNotifications()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
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
            listOf("Gérer la ferme", "Suivre la parcelle", "aide"),
            listOf("Calculer le rendement", "Calculer la superficie", "Assistant AI")
        )

        val adapter = ServicesAdapter(slides)
        viewPager.adapter = adapter

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profil -> {
                    startActivity(Intent(this, CultivateurProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
                    sharedPref.edit().clear().apply()
                    Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Charger le fragment météo
        supportFragmentManager.beginTransaction()
            .replace(R.id.weatherFragmentContainer, WeatherCardFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    fun verifierFerme() {
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show()
            Log.e("FERME", "❌ Token manquant dans SharedPreferences")
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/mes-fermes"

        val request = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                val fermes = response.getJSONArray("fermes")
                if (fermes.length() > 0) {
                    startActivity(Intent(this, FermeListActivity::class.java))
                } else {
                    startActivity(Intent(this, GererFermeActivity::class.java))
                }
            },
            { error ->
                Toast.makeText(this, "Erreur lors de la vérification de la ferme", Toast.LENGTH_SHORT).show()
                Log.e("FERME", "Erreur: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun checkNotifications() {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null) ?: return

        // 1️⃣ Vérif 7 jours d'inactivité
        val lastUpdate = prefs.getLong("last_update", 0)
        val now = System.currentTimeMillis()
        val sevenDays = 7 * 24 * 60 * 60 * 1000L
        if (now - lastUpdate > sevenDays) {
            NotificationHelper.sendNotification(
                this,
                "Aucun suivi récent 🤔",
                "Vous n’avez pas mis à jour vos cultures ou animaux depuis 7 jours.",
                2001
            )
        }

        // 2️⃣ Vraie vérification météo pluie via WeatherUtils
        WeatherUtils.checkRainInCity(this, "Marrakech") {
            NotificationHelper.sendNotification(
                this,
                "🌧️ Alerte météo",
                "Il va pleuvoir aujourd’hui à Marrakech. Pensez à protéger vos cultures !",
                2002
            )
            NotificationHelper.sendNotification(
                this,
                "🎉 Bienvenue à toi !",
                "Content de te revoir dans GFA 👩‍🌾",
                1000
            )

        }
    }


    fun hideAccueilButtons() {
        // à implémenter si nécessaire
    }

    fun showAccueilButtons() {
        // à implémenter si nécessaire
    }
}
