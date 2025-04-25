package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierFermeActivity : AppCompatActivity() {

    private lateinit var editNom: EditText
    private lateinit var editSuperficie: EditText
    private lateinit var editLocalisation: EditText
    private lateinit var editTypeSol: EditText
    private lateinit var btnModifier: Button

    private var fermeId: Int = -1 // récupéré via intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_ferme)

        editNom = findViewById(R.id.editNomF)
        editSuperficie = findViewById(R.id.editSuperficie)
        editLocalisation = findViewById(R.id.editLocalisation)
        editTypeSol = findViewById(R.id.editTypeSol)
        btnModifier = findViewById(R.id.btnModifierFerme)

        // Récupérer les données de la ferme à modifier depuis l'intent
        fermeId = intent.getIntExtra("ferme_id", -1)
        editNom.setText(intent.getStringExtra("nom"))
        editSuperficie.setText(intent.getStringExtra("superficie"))
        editLocalisation.setText(intent.getStringExtra("localisation"))
        editTypeSol.setText(intent.getStringExtra("type_sol"))

        btnModifier.setOnClickListener {
            modifierFerme()
        }
    }

    private fun modifierFerme() {
        val nom = editNom.text.toString()
        val superficie = editSuperficie.text.toString()
        val localisation = editLocalisation.text.toString()
        val typeSol = editTypeSol.text.toString()

        if (nom.isBlank() || superficie.isBlank() || localisation.isBlank()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficie.toDouble())
            put("localisation", localisation)
            put("type_sol", typeSol)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$fermeId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Ferme modifiée avec succès ✅", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            })

        // 💡 Ce qu’il manquait :
        Volley.newRequestQueue(this).add(request)
    }

}