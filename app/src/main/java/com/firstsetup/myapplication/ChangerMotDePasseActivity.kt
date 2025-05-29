package com.firstsetup.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ChangerMotDePasseActivity : AppCompatActivity() {

    private lateinit var ancienMotDePasse: EditText
    private lateinit var nouveauMotDePasse: EditText
    private lateinit var confirmerMotDePasse: EditText
    private lateinit var btnValider: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changer_mot_de_passe)

        ancienMotDePasse = findViewById(R.id.editAncienMotDePasse)
        nouveauMotDePasse = findViewById(R.id.editNouveauMotDePasse)
        confirmerMotDePasse = findViewById(R.id.editConfirmerMotDePasse)
        btnValider = findViewById(R.id.btnValiderChangement)

        btnValider.setOnClickListener {
            val oldPass = ancienMotDePasse.text.toString().trim()
            val newPass = nouveauMotDePasse.text.toString().trim()
            val confirmPass = confirmerMotDePasse.text.toString().trim()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)
            if (token == null) {
                Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = JSONObject().apply {
                put("ancien", oldPass)
                put("nouveau", newPass)
            }

            val request = object : JsonObjectRequest(
                Request.Method.PUT,
                "https://fluorescent-boiled-butter.glitch.me/changerMotDePasse",
                body,
                { response ->
                    Toast.makeText(this, "Mot de passe mis à jour ✅", Toast.LENGTH_SHORT).show()
                    finish()
                },
                { error ->
                    Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }

            Volley.newRequestQueue(this).add(request)
        }
    }
}
