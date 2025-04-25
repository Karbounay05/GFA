package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CultureAdapter(
    private val cultures: List<FermeDetailActivity.Culture>,
    private val onDelete: (Int) -> Unit // ✅ ici dans le constructeur
) : RecyclerView.Adapter<CultureAdapter.CultureViewHolder>() {

    inner class CultureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(R.id.typeCulture)
        val surface: TextView = view.findViewById(R.id.surfaceCulture)
        val saison: TextView = view.findViewById(R.id.saisonCulture)
        val etat: TextView = view.findViewById(R.id.etatSanteCulture)
        val btnModifier: Button = view.findViewById(R.id.btnModifierCulture)
        val btnSupprimer: Button = view.findViewById(R.id.btnSupprimerCulture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CultureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture, parent, false)
        return CultureViewHolder(view)
    }

    override fun onBindViewHolder(holder: CultureViewHolder, position: Int) {
        val culture = cultures[position]
        holder.type.text = culture.type
        holder.surface.text = "Surface: ${culture.surface} ha"
        holder.saison.text = "Saison: ${culture.saison}"


        holder.btnModifier.setOnClickListener {
            // À implémenter : ModifierCultureActivity
        }

        holder.btnSupprimer.setOnClickListener {
            onDelete(culture.id) // ✅ appel correct de la fonction
        }
    }

    override fun getItemCount() = cultures.size
}
