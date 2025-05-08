package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class CultivateurProfileActivity : AppCompatActivity() {

    private lateinit var textNom: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNbFermes: TextView
    private lateinit var textSuperficie: TextView
    private lateinit var textNbAnimaux: TextView
    private lateinit var textNbCultures: TextView
    private lateinit var imageProfil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil_cultivateur)

        // Initialisation des vues
        textNom = findViewById(R.id.textNom)
        textEmail = findViewById(R.id.textEmail)
        textNbFermes = findViewById(R.id.textNbFermes)
        textSuperficie = findViewById(R.id.textSuperficie)
        textNbAnimaux = findViewById(R.id.textNbAnimaux)
        textNbCultures = findViewById(R.id.textNbCultures)
        imageProfil = findViewById(R.id.imageProfil)
        val btnChangerMotDePasse = findViewById<Button>(R.id.btnChangerMotDePasse)
        btnChangerMotDePasse.setOnClickListener {
            val intent = Intent(this, ChangerMotDePasseActivity::class.java)
            startActivity(intent)
        }

        val userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("cultivateur_id", -1)
        if (userId != -1) {
            chargerProfil(userId)
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chargerProfil(id: Int) {
        val url = "https://fluorescent-boiled-butter.glitch.me/profil/$id"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response -> afficherInfos(response) },
            { error ->
                Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
                Log.e("Profil", "❌ ${error.message}")
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun afficherInfos(json: JSONObject) {
        textNom.text = "${json.getString("nom")} ${json.getString("prenom")}"
        textEmail.text = json.getString("email")
        textNbFermes.text = "🌾 Fermes : ${json.getInt("nbFermes")}"
        textSuperficie.text = "📐 Superficie totale : ${json.getDouble("superficieTotale")} ha"
        textNbAnimaux.text = "🐄 Animaux : ${json.getInt("totalAnimaux")}"
        textNbCultures.text = "🌱 Cultures : ${json.getInt("totalCultures")}"
    }
}
