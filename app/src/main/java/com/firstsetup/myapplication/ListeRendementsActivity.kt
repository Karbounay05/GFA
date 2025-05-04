package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ListeRendementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cultivateurId = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_rendements)

        recyclerView = findViewById(R.id.recyclerViewRendements)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRendements()
    }

    private fun fetchRendements() {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/$cultivateurId"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(url,
            { response ->
                val list = mutableListOf<Rendement>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    list.add(
                        Rendement(
                            categorie = obj.getString("categorie"),
                            superficie = obj.getDouble("superficie"),
                            production = obj.getDouble("production"),
                            pertes = obj.getDouble("pertes"),
                            mois = obj.getString("mois"),
                            annee = obj.getString("annee")
                        )
                    )
                }
                recyclerView.adapter = RendementAdapter(list)
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }
}
