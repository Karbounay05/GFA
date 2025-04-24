package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.firstsetup.myapplication.databinding.ActivityAjouterFermeBinding
import org.json.JSONObject

class AjouterFermeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjouterFermeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjouterFermeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAjouterFerme.setOnClickListener {
            ajouterFerme()
        }
    }

    private fun ajouterFerme() {
        val nom = binding.editNomF.text.toString()
        val superficie = binding.editSuperficie.text.toString()
        val localisation = binding.editLocalisation.text.toString()
        val typeSol = binding.editTypeSol.text.toString()

        if (nom.isEmpty() || superficie.isEmpty() || localisation.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val cultivateurId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("cultivateur_id", -1)
        if (cultivateurId == -1) {
            Toast.makeText(this, "Erreur : identifiant cultivateur manquant ❌", Toast.LENGTH_LONG).show()
            return
        }

        val body = JSONObject().apply {
            put("nom", nom)
            put("superficie", superficie.toDouble())
            put("localisation", localisation)
            put("type_sol", typeSol)
            put("cultivateur_id", cultivateurId)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes"

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                Toast.makeText(this, "Ferme ajoutée ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
