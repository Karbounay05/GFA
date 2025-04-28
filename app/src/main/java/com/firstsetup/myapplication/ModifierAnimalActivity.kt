package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierAnimalActivity : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var etatInput: EditText
    private lateinit var btnModifier: Button
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
        etatInput.setText(intent.getStringExtra("statut_sanitaire")) // 🛠️ correction ici (status_sanitaire → **statut_sanitaire**)

        btnModifier.setOnClickListener {
            modifierAnimal()
        }
    }

    private fun modifierAnimal() {
        val nombreStr = nombreInput.text.toString()
        val etat = etatInput.text.toString()

        if (nombreStr.isBlank() || etat.isBlank()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nombre", nombreStr.toInt()) // ✅ convertir en Int ici
            put("statut_sanitaire", etat)    // ✅ orthographe correcte
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/animaux/$animalId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Animal modifié ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
