package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.firstsetup.myapplication.databinding.ActivityAjouterFermeBinding
import org.json.JSONObject

class AjouterFermeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjouterFermeBinding
    private var superficieValue: Int = 10 // valeur par défaut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjouterFermeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🌀 Initialiser le Spinner avec les types de sol
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.types_sol,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeSol.adapter = adapter

        // 📏 Gérer la SeekBar de superficie
        binding.seekBarSuperficie.progress = superficieValue
        binding.textSuperficieValue.text = "$superficieValue hectares"

        binding.seekBarSuperficie.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                superficieValue = progress
                binding.textSuperficieValue.text = "$progress hectares"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ➕ Bouton ajouter la ferme
        binding.btnAjouterFerme.setOnClickListener {
            ajouterFerme()
        }
    }

    private fun ajouterFerme() {
        val nom = binding.editNomF.text.toString().trim()
        val localisation = binding.editLocalisation.text.toString().trim()
        val typeSol = binding.spinnerTypeSol.selectedItem.toString()

        // Vérifier les champs requis
        if (nom.isEmpty() || localisation.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Récupérer l'ID du cultivateur depuis SharedPreferences
        val cultivateurId = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getInt("cultivateur_id", -1)

        if (cultivateurId == -1) {
            Toast.makeText(this, "Erreur : identifiant cultivateur manquant ❌", Toast.LENGTH_LONG).show()
            return
        }

        // Préparer le corps JSON de la requête
        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficieValue)
            put("localisation", localisation)
            put("type_sol", typeSol)
            put("cultivateur_id", cultivateurId)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes"

        // Requête POST avec Volley
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                Toast.makeText(this, "Ferme ajoutée ✅", Toast.LENGTH_SHORT).show()

                // 🚀 Aller à FermeListActivity
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        // Ajouter la requête à la file
        Volley.newRequestQueue(this).add(request)
    }
}