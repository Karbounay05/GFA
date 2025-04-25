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

class AjouterCultureActivity : AppCompatActivity() {

    private lateinit var typeInput: EditText
    private lateinit var surfaceInput: EditText
    private lateinit var saisonInput: EditText
    private lateinit var datePlantationInput: EditText
    private lateinit var etatSante: EditText
    private lateinit var btnAjouterCulture: Button

    private var fermeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_culture)

        typeInput = findViewById(R.id.editTypeCulture)
        surfaceInput = findViewById(R.id.editSurfaceCulture)
        saisonInput = findViewById(R.id.editSaisonCulture)
        datePlantationInput = findViewById(R.id.editDatePlantationCulture)
        etatSante = findViewById(R.id.editEtatSante)
        btnAjouterCulture = findViewById(R.id.btnAjouterCulture)

        fermeId = intent.getIntExtra("ferme_id", -1)

        btnAjouterCulture.setOnClickListener {
            ajouterCulture()
        }
    }

    private fun ajouterCulture() {
        val type = typeInput.text.toString()
        val surface = surfaceInput.text.toString()
        val saison = saisonInput.text.toString()
        val datePlantation = datePlantationInput.text.toString()
        val etat= etatSante.text.toString()
        if (type.isEmpty() || surface.isEmpty() || saison.isEmpty() || datePlantation.isEmpty()|| etat.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("ferme_id", fermeId)
            put("type", type)
            put("surface", surface.toDouble())
            put("saison", saison)
            put("date_plantation", datePlantation)
            put("etat_sante", etat)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/culture"

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                Toast.makeText(this, "Culture ajoutée ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
