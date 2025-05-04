package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RendementAdapter(private val rendements: List<Rendement>) :
    RecyclerView.Adapter<RendementAdapter.RendementViewHolder>() {

    class RendementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCategorie: TextView = itemView.findViewById(R.id.textCategorie)
        val textDetails: TextView = itemView.findViewById(R.id.textDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RendementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rendement, parent, false)
        return RendementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RendementViewHolder, position: Int) {
        val r = rendements[position]
        holder.textCategorie.text = r.categorie
        holder.textDetails.text = "${r.superficie} ha • ${r.production} kg • ${r.mois} ${r.annee}"
    }

    override fun getItemCount() = rendements.size
}
