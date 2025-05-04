package com.firstsetup.myapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.firstsetup.myapplication.weather.WeatherCardFragment
import com.google.android.material.navigation.NavigationView

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

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val slides = listOf(
            listOf("Gérer la ferme", "Suivre la parcelle", "Diagnostiquer la plante"),
            listOf("Calculer le rendement", "Calculer la superficie", "Assistant AI")
        )
        viewPager.adapter = ServicesAdapter(slides)

        // Inject weather fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.weatherFragmentContainer, WeatherCardFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }
}
