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
import com.android.volley.toolbox.Volley
import org.json.JSONException

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

        val cultivateurId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("cultivateur_id", -1)

        if (cultivateurId != -1) {
            chargerFermes(cultivateurId)
        } else {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_LONG).show()
        }
    }

    private fun chargerFermes(userId: Int) {
        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$userId"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                fermeList.clear()
                for (i in 0 until response.length()) {
                    val ferme = response.getJSONObject(i)
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
            },
            { error ->
                Log.e("FERME", "Erreur: ${error.message}")
                Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        )


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