package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
                R.id.nav_home -> {
                    // déjà sur l'accueil, ne rien faire
                }
                R.id.nav_profil -> {
                    startActivity(Intent(this, CultivateurProfileActivity::class.java))
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
        val userId = sharedPref.getInt("cultivateur_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show()
            Log.e("FERME", "❌ cultivateur_id introuvable dans SharedPreferences")
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$userId"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val hasFerme = response.length() > 0
                if (hasFerme) {
                    startActivity(Intent(this, FermeListActivity::class.java))
                } else {
                    startActivity(Intent(this, GererFermeActivity::class.java))
                }
            },
            { error ->
                Toast.makeText(this, "Erreur lors de la vérification de la ferme", Toast.LENGTH_SHORT).show()
                Log.e("FERME", "Erreur: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    fun hideAccueilButtons() {
        // à implémenter si nécessaire
    }

    fun showAccueilButtons() {
        // à implémenter si nécessaire
    }
}
