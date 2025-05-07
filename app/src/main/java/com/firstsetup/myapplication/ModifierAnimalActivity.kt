package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierAnimalActivity : AppCompatActivity() {

    private lateinit var seekBarNombre: SeekBar
    private lateinit var textNombre: TextView
    private lateinit var checkboxBonneSante: CheckBox
    private lateinit var checkboxMaladie: CheckBox
    private lateinit var checkboxAutre: CheckBox
    private lateinit var btnModifier: Button
    private var animalId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_animal)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        // Initialisation
        seekBarNombre = findViewById(R.id.seekBarNombre)
        textNombre = findViewById(R.id.textNombre)
        checkboxBonneSante = findViewById(R.id.checkboxBonneSante)
        checkboxMaladie = findViewById(R.id.checkboxMaladie)
        checkboxAutre = findViewById(R.id.checkboxAutre)
        btnModifier = findViewById(R.id.btnModifierAnimal)

        // Récupérer les données envoyées
        animalId = intent.getIntExtra("animal_id", -1)
        val nombre = intent.getStringExtra("nombre")?.toIntOrNull() ?: 0
        val statutSanitaire = intent.getStringExtra("statut_sanitaire") ?: ""

        seekBarNombre.progress = nombre
        textNombre.text = "Nombre : $nombre"

        // Sélectionner le bon checkbox en fonction du statut existant
        when (statutSanitaire) {
            "Bonne Santé" -> checkboxBonneSante.isChecked = true
            "Maladie" -> checkboxMaladie.isChecked = true
            "Autre" -> checkboxAutre.isChecked = true
        }

        // Mise à jour du texte quand on bouge le seekbar
        seekBarNombre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textNombre.text = "Nombre : $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Bouton Modifier
        btnModifier.setOnClickListener {
            modifierAnimal()
        }
    }

    private fun modifierAnimal() {
        val nombre = seekBarNombre.progress

        val statutSanitaire = when {
            checkboxBonneSante.isChecked -> "Bonne Santé"
            checkboxMaladie.isChecked -> "Maladie"
            checkboxAutre.isChecked -> "Autre"
            else -> ""
        }

        if (statutSanitaire.isBlank()) {
            Toast.makeText(this, "Veuillez sélectionner un statut sanitaire", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nombre", nombre)
            put("statut_sanitaire", statutSanitaire)
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