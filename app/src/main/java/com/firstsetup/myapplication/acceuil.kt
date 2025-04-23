package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView

class acceuil : AppCompatActivity() {

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

        // New format: List of 3 slides, each with 3 strings
        val slides = listOf(
            listOf("Gérer la ferme", "Suivre la parcelle", "Diagnostiquer la plant"),
            listOf("Calculer le rendement", "Calculer la superficier", "Assistant AI")
        )


        val adapter = ServicesAdapter(slides)
        viewPager.adapter = adapter

        // Navigation drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle Home
                }
                R.id.nav_settings -> {
                    // Handle Settings
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Logout
        val logoutButton: Button = findViewById(R.id.button_logout)
        logoutButton.setOnClickListener {
            getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit()
                .putInt("val", 2)
                .apply()

            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}