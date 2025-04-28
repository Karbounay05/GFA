package com.firstsetup.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FermeAdapter(
    private val fermes: List<FermeListActivity.Ferme>
) : RecyclerView.Adapter<FermeAdapter.FermeViewHolder>() {

    private var lastPosition = -1

    inner class FermeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomText: TextView = itemView.findViewById(R.id.nomFerme)
        val localisationText: TextView = itemView.findViewById(R.id.localisationFerme)
        val tailleText: TextView = itemView.findViewById(R.id.tailleFerme)
        val solText: TextView = itemView.findViewById(R.id.solFerme)
        val btnModifier: Button = itemView.findViewById(R.id.btnModifier)
        val btnSupprimer: Button = itemView.findViewById(R.id.btnSupprimer)
        val cardView: View = itemView.findViewById(R.id.cardFerme)
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

        // ✅ Animation si position non encore animée
        val currentPos = holder.adapterPosition
        if (currentPos != RecyclerView.NO_POSITION && currentPos > lastPosition) {
            val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.move_up2)
            holder.cardView.startAnimation(animation)
            lastPosition = currentPos
        }

        // ✅ Clic sur la carte → Détails
        holder.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FermeDetailActivity::class.java)
            intent.putExtra("ferme_id", ferme.id)
            intent.putExtra("ferme_nom", ferme.nom)
            holder.itemView.context.startActivity(intent)
        }

        // ✅ Modifier
        holder.btnModifier.setOnClickListener {
            val intent = Intent(holder.itemView.context, ModifierFermeActivity::class.java)
            intent.putExtra("ferme_id", ferme.id)
            holder.itemView.context.startActivity(intent)
        }

        // ✅ Supprimer
        holder.btnSupprimer.setOnClickListener {
            val intent = Intent(holder.itemView.context, SupprimerFermeActivity::class.java)
            intent.putExtra("ferme_id", ferme.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = fermes.size
}