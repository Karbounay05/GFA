package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierFermeActivity : AppCompatActivity() {

    private lateinit var editNom: EditText
    private lateinit var seekBarSuperficie: SeekBar
    private lateinit var textSuperficieValue: TextView
    private lateinit var editLocalisation: EditText
    private lateinit var spinnerTypeSol: Spinner
    private lateinit var btnModifier: Button

    private var fermeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_ferme)

        // Initialisation
        editNom = findViewById(R.id.editNomF)
        seekBarSuperficie = findViewById(R.id.seekBarSuperficie)
        textSuperficieValue = findViewById(R.id.textSuperficieValue)
        editLocalisation = findViewById(R.id.editLocalisation)
        spinnerTypeSol = findViewById(R.id.spinnerTypeSol)
        btnModifier = findViewById(R.id.btnModifierFerme)

        // Récupérer les données envoyées
        fermeId = intent.getIntExtra("ferme_id", -1)
        val nom = intent.getStringExtra("nom") ?: ""
        val superficie = intent.getStringExtra("superficie")?.toDoubleOrNull() ?: 0.0
        val localisation = intent.getStringExtra("localisation") ?: ""
        val typeSol = intent.getStringExtra("type_sol") ?: ""

        // Remplir les champs
        editNom.setText(nom)
        seekBarSuperficie.progress = superficie.toInt()
        textSuperficieValue.text = "${superficie.toInt()} hectares"
        editLocalisation.setText(localisation)

        // Spinner Type de Sol
        val typesSol = listOf("Argileux", "Sablonneux", "Limoneux", "Tourbeux")
        spinnerTypeSol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesSol)
        spinnerTypeSol.setSelection(
            typesSol.indexOfFirst { it.equals(typeSol, ignoreCase = true) }.coerceAtLeast(0)
        )

        // Mettre à jour la superficie dynamiquement quand on bouge le SeekBar
        seekBarSuperficie.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSuperficieValue.text = "$progress hectares"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Action bouton
        btnModifier.setOnClickListener {
            modifierFerme()
        }
    }

    private fun modifierFerme() {
        val nom = editNom.text.toString()
        val superficie = seekBarSuperficie.progress
        val localisation = editLocalisation.text.toString()
        val typeSol = spinnerTypeSol.selectedItem.toString()

        if (nom.isBlank() || superficie == 0 || localisation.isBlank()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficie)
            put("localisation", localisation)
            put("type_sol", typeSol)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$fermeId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Ferme modifiée avec succès ✅", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}