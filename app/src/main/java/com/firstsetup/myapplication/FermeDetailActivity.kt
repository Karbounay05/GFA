package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class FermeDetailActivity : AppCompatActivity() {

    private lateinit var recyclerViewAnimaux: RecyclerView
    private lateinit var recyclerViewCultures: RecyclerView
    private lateinit var btnAjouterAnimal: Button
    private lateinit var btnAjouterCulture: Button
    private lateinit var titreFermes: TextView

    private var fermeId: Int = -1

    private val animaux = mutableListOf<Animal>()
    private val cultures = mutableListOf<Culture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ferme_detail_activity)

        Toast.makeText(this, "🚀 Bienvenue sur FermeDetailActivity", Toast.LENGTH_SHORT).show()

        titreFermes = findViewById(R.id.titreFermes)
        recyclerViewAnimaux = findViewById(R.id.recyclerViewAnimaux)
        recyclerViewCultures = findViewById(R.id.recyclerViewCultures)
        btnAjouterAnimal = findViewById(R.id.btnAjouterAnimal)
        btnAjouterCulture = findViewById(R.id.btnAjouterCulture)

        fermeId = intent.getIntExtra("ferme_id", -1)
        val fermeNom = intent.getStringExtra("ferme_nom") ?: ""
        titreFermes.text = "Détails de la ferme: $fermeNom"

        recyclerViewAnimaux.layoutManager = LinearLayoutManager(this)
        recyclerViewCultures.layoutManager = LinearLayoutManager(this)

        btnAjouterAnimal.setOnClickListener {
            val intent = Intent(this, AjouterAnimalActivity::class.java)
            intent.putExtra("ferme_id", fermeId)
            startActivity(intent)
        }

        btnAjouterCulture.setOnClickListener {
            val intent = Intent(this, AjouterCultureActivity::class.java)
            intent.putExtra("ferme_id", fermeId)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        chargerAnimaux()
        chargerCultures()
    }

    private fun chargerAnimaux() {
        val url = "https://fluorescent-boiled-butter.glitch.me/animaux/$fermeId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                animaux.clear()
                val array = response.getJSONArray("animaux")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    animaux.add(
                        Animal(
                            obj.getInt("id"),
                            obj.getString("espece"),
                            obj.getInt("nombre"),
                            obj.getString("date_entree"),
                            obj.getString("statut_sanitaire")
                        )
                    )
                }
                recyclerViewAnimaux.adapter = AnimalAdapter(animaux) { id ->
                    supprimerAnimal(id)
                }

                btnAjouterAnimal.visibility = if (animaux.isEmpty()) View.VISIBLE else View.GONE
            },
            { error ->
                Log.e("FermeDetailActivity", "Erreur chargement animaux", error)
                Toast.makeText(this, "Erreur chargement animaux: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
    private fun supprimerAnimal(cultureId: Int) {
        val url = "https://fluorescent-boiled-butter.glitch.me/culture/$cultureId"

        val request = StringRequest(Request.Method.DELETE, url,
            {
                Toast.makeText(this, "Animal supprimé ✅", Toast.LENGTH_SHORT).show()
                chargerAnimaux()
            },
            {
                Toast.makeText(this, "Erreur suppression culture : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }


    private fun chargerCultures() {
        val url = "https://fluorescent-boiled-butter.glitch.me/culture/$fermeId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                cultures.clear()
                val array = response.getJSONArray("cultures")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    cultures.add(
                        Culture(
                            obj.getInt("id"),
                            obj.getString("type"),
                            obj.getDouble("surface"),
                            obj.getString("saison"),
                            obj.getString("date_plantation")
                        )
                    )
                }
                recyclerViewCultures.adapter = CultureAdapter(cultures) { id ->
                    supprimerCulture(id)
                }
                btnAjouterCulture.visibility = if (cultures.isEmpty()) View.VISIBLE else View.GONE
            },
            { error ->
                Log.e("FermeDetailActivity", "Erreur chargement cultures", error)
                Toast.makeText(this, "Erreur chargement cultures: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
    private fun supprimerCulture(cultureId: Int) {
        val url = "https://fluorescent-boiled-butter.glitch.me/culture/$cultureId"

        val request = StringRequest(Request.Method.DELETE, url,
            {
                Toast.makeText(this, "culture supprimé ✅", Toast.LENGTH_SHORT).show()
                chargerCultures()
            },
            {
                Toast.makeText(this, "Erreur suppression culture : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
    data class Animal(val id: Int, val espece: String, val nombre: Int, val date_entree: String, val statut_sanitaire: String)
    data class Culture(val id: Int, val type: String, val surface: Double, val saison: String, val date_plantation: String)
}
