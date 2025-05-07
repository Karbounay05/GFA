package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.firstsetup.myapplication.databinding.ActivityModifierFermeBinding
import org.json.JSONObject


class ModifierFermeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifierFermeBinding
    private var superficieValue: Int = 10 // valeur par défaut
    private var fermeId: Int = -1

    private lateinit var pingCard: CardView
    private lateinit var pingText: TextView
    private lateinit var pingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifierFermeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromMap = intent.getBooleanExtra("fromMap", false)

        // 🌀 Initialiser le Spinner avec les types de sol
        val typesSol = listOf("Argileux", "Sablonneux", "Limoneux", "Tourbeux")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesSol)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeSol.adapter = adapter

        // 🧠 Récupérer les données
        fermeId = intent.getIntExtra("ferme_id", -1)
        val nom = intent.getStringExtra("nom") ?: ""
        val superficie = intent.getStringExtra("superficie")?.toDoubleOrNull() ?: 0.0
        val localisation = intent.getStringExtra("localisation") ?: ""
        val typeSol = intent.getStringExtra("type_sol") ?: ""

        // 🎯 Remplir les champs
        binding.editNomF.setText(nom)
        superficieValue = superficie.toInt()
        binding.seekBarSuperficie.progress = superficieValue
        binding.textSuperficieValue.text = "$superficieValue hectares"
        binding.editLocalisation.setText(localisation)
        binding.spinnerTypeSol.setSelection(typesSol.indexOfFirst { it.equals(typeSol, true) }.coerceAtLeast(0))

        // 📏 Gérer la SeekBar de superficie
        binding.seekBarSuperficie.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                superficieValue = progress
                binding.textSuperficieValue.text = "$progress hectares"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        if (fromMap) {
            binding.btnModifierFerme.text = "Retour à la carte"
        }



            binding.btnModifierFerme.setOnClickListener {
                if (fromMap) {
                    modifierFermeEtRetourCarte()
                } else {
                    modifierFerme()
                }
            }


    }

    private fun modifierFerme() {
        val nom = binding.editNomF.text.toString().trim()
        val localisation = binding.editLocalisation.text.toString().trim()
        val typeSol = binding.spinnerTypeSol.selectedItem.toString()

        if (nom.isEmpty() || localisation.isEmpty() || superficieValue == 0) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficieValue)
            put("localisation", localisation)
            put("type_sol", typeSol)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$fermeId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Ferme modifiée ✅", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, FermeListActivity::class.java))
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun modifierFermeEtRetourCarte() {
        val nom = binding.editNomF.text.toString().trim()
        val localisation = binding.editLocalisation.text.toString().trim()
        val typeSol = binding.spinnerTypeSol.selectedItem.toString()

        if (nom.isEmpty() || localisation.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficieValue)
            put("localisation", localisation)
            put("type_sol", typeSol)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$fermeId"

        val request = JsonObjectRequest(
            Request.Method.PUT, url, body,
            {
                Toast.makeText(this, "Modifié 🛠️", Toast.LENGTH_SHORT).show()
                val returnIntent = Intent().apply {
                    putExtra("lat", intent.getDoubleExtra("latitude", 0.0))
                    putExtra("lon", intent.getDoubleExtra("longitude", 0.0))
                    putExtra("nom", nom)
                    putExtra("taille", superficieValue)
                    putExtra("localisation", localisation)
                    putExtra("type_sol", typeSol)
                }
                setResult(RESULT_OK, returnIntent)
                finish()
            },
            {
                Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

}
