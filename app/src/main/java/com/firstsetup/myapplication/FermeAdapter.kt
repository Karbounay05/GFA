package com.firstsetup.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FermeAdapter(private val fermes: List<Ferme>) : RecyclerView.Adapter<FermeAdapter.FermeViewHolder>() {

    inner class FermeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomText: TextView = itemView.findViewById(R.id.nomFerme)
        val localisationText: TextView = itemView.findViewById(R.id.localisationFerme)
        val card: View = itemView.findViewById(R.id.cardFerme)
        val btnDetails: Button = itemView.findViewById(R.id.btnDetails)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FermeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_ferme, parent, false)
        return FermeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FermeViewHolder, position: Int) {
        val ferme = fermes[position]
        holder.nomText.text = "🌾 ${ferme.nom}"
        holder.localisationText.text = "📍 ${ferme.localisation}"

        holder.btnDetails.setOnClickListener {
            val intent = Intent(holder.itemView.context, FermeDetailActivity::class.java)
            intent.putExtra("ferme_id", ferme.id)
            intent.putExtra("ferme_nom", ferme.nom)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = fermes.size
}
