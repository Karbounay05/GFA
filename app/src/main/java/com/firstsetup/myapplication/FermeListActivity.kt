package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.json.JSONException
import org.json.JSONObject

class FermeListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FermeAdapter
    private val fermeList = mutableListOf<Ferme>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ferme)

        recyclerView = findViewById(R.id.recyclerFermes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FermeAdapter(fermeList)
        recyclerView.adapter = adapter

      chargerFermes()
    }

    private fun chargerFermes() {
        val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)
        Log.d("TOKEN_DEBUG", "Token = $token")

        if (token == null) {
            Toast.makeText(this, "Token manquant ❌", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/mes-fermes"

        val request = object : StringRequest(Request.Method.GET, url,
            { response ->
                fermeList.clear()
                try {
                    val json = JSONObject(response)
                    val fermes = json.getJSONArray("fermes")
                    for (i in 0 until fermes.length()) {
                        val ferme = fermes.getJSONObject(i)
                        fermeList.add(
                            Ferme(
                                ferme.getInt("id"),
                                ferme.getString("nom"),
                                ferme.getString("localisation"),
                                ferme.getDouble("taille"),
                                ferme.getString("type_sol")
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("FERME", "Parsing error", e)
                }
            },
            { error ->
                Log.e("FERME", "Erreur réseau: ${error.message}")
                Toast.makeText(this, "Erreur réseau ou token refusé", Toast.LENGTH_SHORT).show()
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


    data class Ferme(val id: Int, val nom: String, val localisation: String, val taille: Double, val typeSol: String)

    class FermeAdapter(private val fermes: List<Ferme>) : RecyclerView.Adapter<FermeAdapter.FermeViewHolder>() {

        class FermeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nomText: TextView = view.findViewById(R.id.nomFerme)
            val localisationText: TextView = view.findViewById(R.id.localisationFerme)
            val tailleText: TextView = view.findViewById(R.id.tailleFerme)
            val solText: TextView = view.findViewById(R.id.solFerme)
            val btnModifier: Button = view.findViewById(R.id.btnModifier)
            val btnSupprimer: Button = view.findViewById(R.id.btnSupprimer)
            val cardView: View = view.findViewById(R.id.cardFerme) // le CardView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FermeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_ferme, parent, false)
            return FermeViewHolder(view)
        }

        override fun onBindViewHolder(holder: FermeViewHolder, position: Int) {
            val ferme = fermes[position]
            holder.nomText.text = "🌾 ${ferme.nom}"
            holder.localisationText.text = "📍 ${ferme.localisation}"
            holder.tailleText.text = "📏 ${ferme.taille} ha"
            holder.solText.text = "🌱 ${ferme.typeSol}"

            // ✅ Le clic sur la carte entière ouvre les détails
            holder.cardView.setOnClickListener {
                val intent = Intent(holder.itemView.context, FermeDetailActivity::class.java)
                intent.putExtra("ferme_id", ferme.id)
                holder.itemView.context.startActivity(intent)
            }

            holder.btnModifier.setOnClickListener {
                val intent = Intent(holder.itemView.context, ModifierFermeActivity::class.java)
                intent.putExtra("ferme_id", ferme.id)
                holder.itemView.context.startActivity(intent)
            }

            holder.btnSupprimer.setOnClickListener {
                val intent = Intent(holder.itemView.context, SupprimerFermeActivity::class.java)
                intent.putExtra("ferme_id", ferme.id)
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = fermes.size
    }
}