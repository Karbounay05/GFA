package com.firstsetup.myapplication

import Rendement
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CorbeilleAdapter(
    private val list: List<Rendement>,
    private val listener: ActionListener
) : RecyclerView.Adapter<CorbeilleAdapter.ViewHolder>() {

    interface ActionListener {
        fun onRestore(r: Rendement)
        fun onDelete(r: Rendement)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCategorie: TextView = itemView.findViewById(R.id.textCategorieCorbeille)
        val textDetails: TextView = itemView.findViewById(R.id.textDetailsCorbeille)
        val btnRestore: Button = itemView.findViewById(R.id.btnRestore)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_corbeille, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.textCategorie.text = r.categorie
        holder.textDetails.text = "${r.mois} ${r.annee} - ${r.rendementParHa} kg/ha"

        holder.btnRestore.setOnClickListener { listener.onRestore(r) }
        holder.btnDelete.setOnClickListener { listener.onDelete(r) }
    }
}
