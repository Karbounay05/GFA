package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ModifierCultureActivity : AppCompatActivity() {

    private lateinit var textSurface: TextView
    private lateinit var seekBarSurface: SeekBar
    private lateinit var spinnerSaison: Spinner
    private lateinit var checkboxBonneSante: CheckBox
    private lateinit var checkboxMaladie: CheckBox
    private lateinit var checkboxSeche: CheckBox
    private lateinit var btnModifierCulture: Button

    private var cultureId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_culture)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        // Initialisation
        textSurface = findViewById(R.id.textSurface)
        seekBarSurface = findViewById(R.id.seekBarSurface)
        spinnerSaison = findViewById(R.id.spinnerSaison)
        checkboxBonneSante = findViewById(R.id.checkboxBonneSante)
        checkboxMaladie = findViewById(R.id.checkboxMaladie)
        checkboxSeche = findViewById(R.id.checkboxSeche)
        btnModifierCulture = findViewById(R.id.btnModifierCulture)

        // Récupérer données envoyées
        cultureId = intent.getIntExtra("culture_id", -1)
        val surface = intent.getStringExtra("surface")?.toDoubleOrNull() ?: 0.0
        val saison = intent.getStringExtra("saison") ?: ""
        val etatSante = intent.getStringExtra("etat_sante") ?: ""

        // Afficher la surface
        seekBarSurface.progress = surface.toInt()
        textSurface.text = "Surface : ${surface.toInt()} m²"

        // Faire bouger la surface avec SeekBar
        seekBarSurface.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSurface.text = "Surface : $progress m²"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Remplir le Spinner Saison
        val saisons = listOf("Printemps", "Été", "Automne", "Hiver")
        spinnerSaison.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, saisons)
        spinnerSaison.setSelection(
            saisons.indexOfFirst { it.equals(saison, ignoreCase = true) }.coerceAtLeast(0)
        )

        // Remplir les CheckBox
        when (etatSante) {
            "Bonne santé" -> checkboxBonneSante.isChecked = true
            "Maladie" -> checkboxMaladie.isChecked = true
            "Séchage" -> checkboxSeche.isChecked = true
        }

        // CheckBoxes exclusifs
        checkboxBonneSante.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxMaladie.isChecked = false
                checkboxSeche.isChecked = false
            }
        }
        checkboxMaladie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxBonneSante.isChecked = false
                checkboxSeche.isChecked = false
            }
        }
        checkboxSeche.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxBonneSante.isChecked = false
                checkboxMaladie.isChecked = false
            }
        }

        // Modifier Culture
        btnModifierCulture.setOnClickListener {
            modifierCulture()
        }
    }

    private fun modifierCulture() {
        val surface = seekBarSurface.progress
        val saison = spinnerSaison.selectedItem.toString()
        val etat = when {
            checkboxBonneSante.isChecked -> "Bonne santé"
            checkboxMaladie.isChecked -> "Maladie"
            checkboxSeche.isChecked -> "Séchage"
            else -> ""
        }

        if (surface == 0 || saison.isBlank() || etat.isBlank()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("surface", surface)
            put("saison", saison)
            put("etat_sante", etat)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/culture/$cultureId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
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