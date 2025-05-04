package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONObject

class RendementActivity : AppCompatActivity() {

    private lateinit var inputCategorie: EditText
    private lateinit var inputSuperficie: EditText
    private lateinit var inputProduction: EditText
    private lateinit var inputPertes: EditText
    private lateinit var inputMois: EditText
    private lateinit var inputAnnee: EditText
    private lateinit var btnEnvoyer: Button
    private lateinit var btnVoir: Button

    private val cultivateurId = 5 // à remplacer dynamiquement si besoin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rendement)

        inputCategorie = findViewById(R.id.inputCategorie)
        inputSuperficie = findViewById(R.id.inputSuperficie)
        inputProduction = findViewById(R.id.inputProduction)
        inputPertes = findViewById(R.id.inputPertes)
        inputMois = findViewById(R.id.inputMois)
        inputAnnee = findViewById(R.id.inputAnnee)
        btnEnvoyer = findViewById(R.id.btnEnvoyerRendement)
        btnVoir = findViewById(R.id.btnVoirRendements)

        btnEnvoyer.setOnClickListener {
            envoyerRendement()
        }

        btnVoir.setOnClickListener {
            val intent = Intent(this, ListeRendementsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun envoyerRendement() {
        val categorie = inputCategorie.text.toString().trim()
        val superficie = inputSuperficie.text.toString().toDoubleOrNull()
        val production = inputProduction.text.toString().toDoubleOrNull()
        val pertes = inputPertes.text.toString().toDoubleOrNull()
        val mois = inputMois.text.toString().trim()
        val annee = inputAnnee.text.toString().trim()

        if (categorie.isEmpty() || superficie == null || production == null || pertes == null || mois.isEmpty() || annee.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs correctement", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements"

        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "✅ Rendement enregistré", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur lors de l’envoi : ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getBodyContentType(): String = "application/json"

            override fun getBody(): ByteArray {
                val json = JSONObject()
                json.put("cultivateur_id", cultivateurId)
                json.put("categorie", categorie)
                json.put("superficie", superficie)
                json.put("production", production)
                json.put("pertes", pertes)
                json.put("mois", mois)
                json.put("annee", annee)
                return json.toString().toByteArray()
            }
        }

        queue.add(request)
    }
}
