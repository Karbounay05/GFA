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

class ModifierCultureActivity : AppCompatActivity() {

    private lateinit var surfaceInput: EditText
    private lateinit var saisonInput: EditText
    private lateinit var etatInput: EditText
    private lateinit var btnModifier: Button

    private var cultureId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_culture)

        surfaceInput = findViewById(R.id.editSurfaceCulture)
        saisonInput = findViewById(R.id.editSaisonCulture)
        etatInput = findViewById(R.id.editEtatSante)
        btnModifier = findViewById(R.id.btnModifierCulture)

        // Récupérer les données
        cultureId = intent.getIntExtra("culture_id", -1)
        surfaceInput.setText(intent.getStringExtra("surface"))
        saisonInput.setText(intent.getStringExtra("saison"))
        etatInput.setText(intent.getStringExtra("etat_sante"))

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

        val url = "https://fluorescent-boiled-butter.glitch.me/culture/$cultureId"

        val request = JsonObjectRequest(Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Culture modifiée ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
