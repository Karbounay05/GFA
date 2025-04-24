package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class FermeActivity : AppCompatActivity() {

    private lateinit var fermeNom: TextView
    private lateinit var fermeLocalisation: TextView
    private lateinit var fermeSuperficie: TextView
    private lateinit var fermeTypeSol: TextView

    private lateinit var btnModifier: Button
    private lateinit var btnSupprimer: Button
    private lateinit var btnAjouterCulture: Button
    private lateinit var btnAjouterAnimaux: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ferme)

        // Initialisation des vues
        fermeNom = findViewById(R.id.fermeTitre)
        fermeLocalisation = findViewById(R.id.fermeLocalisation)
        fermeSuperficie = findViewById(R.id.fermeSuperficie)
        fermeTypeSol = findViewById(R.id.fermeSol)

        btnModifier = findViewById(R.id.btnModifierFerme)
       btnSupprimer = findViewById(R.id.btnSupprimerFerme)
        btnAjouterCulture = findViewById(R.id.btnAjouterCulture)
        btnAjouterAnimaux = findViewById(R.id.btnAjouterAnimal)

        // On récupère les infos envoyées par Intent
        val id = intent.getIntExtra("ferme_id", -1)
        val nom = intent.getStringExtra("nom")
        val localisation = intent.getStringExtra("localisation")
        val superficie = intent.getStringExtra("superficie")
        val typeSol = intent.getStringExtra("type_sol")

        fermeNom.text = "🌾 Ferme : $nom"
        fermeLocalisation.text = "📍 $localisation"
        fermeSuperficie.text = "📏 $superficie ha"
        fermeTypeSol.text = "🌱 Sol : $typeSol"

        // Événements des boutons
        btnModifier.setOnClickListener {
            val intent = Intent(this, ModifierFermeActivity::class.java)
            intent.putExtra("ferme_id", id)
            intent.putExtra("nom", nom)
            intent.putExtra("localisation", localisation)
            intent.putExtra("superficie", superficie)
            intent.putExtra("type_sol", typeSol)
            startActivity(intent)
        }

        btnSupprimer.setOnClickListener {
            val intent = Intent(this, SupprimerFermeActivity::class.java)
            intent.putExtra("ferme_id", id)
            startActivity(intent)
        }

        btnAjouterCulture.setOnClickListener {
            val intent = Intent(this, AjouterCultureActivity::class.java)
            intent.putExtra("ferme_id", id)
            startActivity(intent)
        }

        btnAjouterAnimaux.setOnClickListener {
            val intent = Intent(this, AjouterAnimalActivity::class.java)
            intent.putExtra("ferme_id", id)
            startActivity(intent)
        }
    }
}
