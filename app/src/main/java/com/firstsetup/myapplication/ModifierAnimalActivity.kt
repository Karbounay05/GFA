package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierAnimalActivity {
    private lateinit var nombreInput: EditText
    private lateinit var etatInput: EditText
    private var animalId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_animal)

       nombreInput = findViewById(R.id.editNombre)
        etatInput = findViewById(R.id.editStatutSanitaire)
        btnModifier = findViewById(R.id.btnModifierAnimal)

        // Récupérer les données
        animalId = intent.getIntExtra("animal_id", -1)
        nombreInput.setText(intent.getStringExtra("nombre"))
        etatInput.setText(intent.getStringExtra("status_sanitaire"))

        btnModifier.setOnClickListener {
            modifierCulture()
        }
    }

    private fun modifierCulture() {
        val surface = surfaceInput.text.toString()
        val saison = saisonInput.text.toString()
        val etat = etatInput.text.toString()

        if (surface.isBlank() || saison.isBlank() || etat.isBlank()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("surface", surface.toDouble())
            put("saison", saison)
            put("etat_sante", etat)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/animaux/$animalId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "animal modifiée ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}