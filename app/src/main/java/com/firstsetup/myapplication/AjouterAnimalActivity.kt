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

class AjouterAnimalActivity : AppCompatActivity() {

    private lateinit var especeInput: EditText
    private lateinit var nombreInput: EditText
    private lateinit var dateEntreeInput: EditText
    private lateinit var statutSanitaireInput: EditText
    private lateinit var btnAjouterAnimal: Button

    private var fermeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_animal)

        especeInput = findViewById(R.id.editEspece)
        nombreInput = findViewById(R.id.editNombre)
        dateEntreeInput = findViewById(R.id.editDateEntree)
        statutSanitaireInput = findViewById(R.id.editStatutSanitaire)
        btnAjouterAnimal = findViewById(R.id.btnAjouterAnimal)

        fermeId = intent.getIntExtra("ferme_id", -1)

        btnAjouterAnimal.setOnClickListener {
            ajouterAnimal()
        }
    }

    private fun ajouterAnimal() {
        val espece = especeInput.text.toString()
        val nombre = nombreInput.text.toString()
        val dateEntree = dateEntreeInput.text.toString()
        val statutSanitaire = statutSanitaireInput.text.toString()

        if (espece.isEmpty() || nombre.isEmpty() || dateEntree.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("ferme_id", fermeId)
            put("espece", espece)
            put("nombre", nombre.toInt())
            put("date_entree", dateEntree)
            put("statut_sanitaire", statutSanitaire)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/animaux"

        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                Toast.makeText(this, "Animal ajouté ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
