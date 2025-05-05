package com.firstsetup.myapplication

import Rendement
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class CorbeilleActivity : AppCompatActivity(), CorbeilleAdapter.ActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CorbeilleAdapter
    private val rendements = mutableListOf<Rendement>()
    private val cultivateurId = 5
    private val handler = android.os.Handler()
    private val refreshRunnable = object : Runnable {
        override fun run() {
            chargerRendementsSupprimes()
            handler.postDelayed(this, 3000)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corbeille)

        recyclerView = findViewById(R.id.recyclerViewCorbeille)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CorbeilleAdapter(rendements, this)
        recyclerView.adapter = adapter

        chargerRendementsSupprimes()
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun chargerRendementsSupprimes() {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/deleted/$cultivateurId"
        val queue = Volley.newRequestQueue(this)

        val req = JsonObjectRequest(
            url, null,
            { response ->
                rendements.clear()
                val array = response.getJSONArray("rendements")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    rendements.add(
                        Rendement(
                            id = obj.getInt("id"),
                            categorie = obj.getString("categorie"),
                            superficie = obj.getDouble("superficie"),
                            production = obj.getDouble("production"),
                            pertes = obj.getDouble("pertes"),
                            mois = obj.getString("mois"),
                            annee = obj.getString("annee"),
                            rendementParHa = obj.getDouble("rendement_par_ha"),
                            pertesParHa = obj.getDouble("pertes_par_ha")
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur chargement corbeille : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(req)
    }

    override fun onRestore(rendement: Rendement) {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/restore/${rendement.id}"
        val queue = Volley.newRequestQueue(this)

        val req = StringRequest(
            com.android.volley.Request.Method.PUT, url,
            {
                Toast.makeText(this, "✅ Restauré avec succès", Toast.LENGTH_SHORT).show()
                rendements.remove(rendement)
                adapter.notifyDataSetChanged()
                setResult(RESULT_OK) // pour ListeRendementsActivity
            },
            { error ->
                Toast.makeText(this, "❌ Erreur restauration : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(req)
    }

    override fun onDelete(rendement: Rendement) {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/hard/${rendement.id}"
        val queue = Volley.newRequestQueue(this)

        val req = StringRequest(
            com.android.volley.Request.Method.DELETE, url,
            {
                Toast.makeText(this, "🗑️ Supprimé définitivement", Toast.LENGTH_SHORT).show()
                rendements.remove(rendement)
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur API : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(req)
    }
}
