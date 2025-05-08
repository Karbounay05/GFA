package com.firstsetup.myapplication.model

data class Ferme(
    val id: Int,
    val nom: String,
    val localisation: String,
    val taille: Double,
    val typeSol: String
) {
    override fun toString(): String {
        return nom // ✅ Seul le nom sera affiché dans le Spinner
    }
}
