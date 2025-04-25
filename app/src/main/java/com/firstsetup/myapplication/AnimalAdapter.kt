package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnimalAdapter(
    private val animaux: List<FermeDetailActivity.Animal>,
    private val onDelete: (Int) -> Unit // 🔥 Ajout du callback pour supprimer
) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    inner class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val espece: TextView = view.findViewById(R.id.especeAnimal)
        val nombre: TextView = view.findViewById(R.id.nombreAnimal)
        val statut: TextView = view.findViewById(R.id.statutAnimal)
        val btnModifier: Button = view.findViewById(R.id.btnModifierAnimal)
        val btnSupprimer: Button = view.findViewById(R.id.btnSupprimerAnimal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animaux[position]
        holder.espece.text = animal.espece
        holder.nombre.text = "Nombre: ${animal.nombre}"
        holder.statut.text = "Statut: ${animal.statut_sanitaire}"

        holder.btnModifier.setOnClickListener {
            // Tu pourras ajouter la navigation vers ModifierAnimalActivity ici
        }

        holder.btnSupprimer.setOnClickListener {
            onDelete(animal.id) // 👈 Ceci appelle la fonction passée depuis FermeDetailActivity
        }
    }

    override fun getItemCount() = animaux.size
}
