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
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView

class AjouterFermeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjouterFermeBinding
    private var superficieValue: Int = 10 // valeur par défaut

    private lateinit var pingCard: CardView
    private lateinit var pingText: TextView
    private lateinit var pingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjouterFermeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromMap = intent.getBooleanExtra("fromMap", false)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        pingCard = findViewById(R.id.pingCard)
        pingText = findViewById(R.id.pingText)
        pingProgress = findViewById(R.id.pingProgress)

        // 🌀 Initialiser le Spinner avec les types de sol
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.types_sol,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeSol.adapter = adapter
        if (fromMap) {
            binding.btnAjouterFerme.text = "Retour à la carte"
        }

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
            if (fromMap) {
                pingCard.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    pingCard.visibility = View.GONE
                }, 10000)
                ajouterFermeInMap()
            } else {
                // comportement normal
                pingCard.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    pingCard.visibility = View.GONE
                }, 10000)
                ajouterFerme()
            }
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
        val request = object : JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                Toast.makeText(this, "Ferme ajoutée ✅", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)
                return hashMapOf("Authorization" to "Bearer $token")
            }
        }

        // Ajouter la requête à la file
        Volley.newRequestQueue(this).add(request)
    }

    fun ajouterFermeInMap() {
        val nom = binding.editNomF.text.toString().trim()
        val localisation = binding.editLocalisation.text.toString().trim()
        val typeSol = binding.spinnerTypeSol.selectedItem.toString()
        val superficie = superficieValue

        if (nom.isEmpty() || localisation.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }


        // ✅ Récupérer la position depuis l'intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // ✅ Construire le body avec lat/lon inclus
        val body = JSONObject().apply {
            put("nom", nom)
            put("taille", superficie)
            put("localisation", localisation)
            put("type_sol", typeSol)
            put("lat", latitude)
            put("lon", longitude)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/map"

        val request = object : JsonObjectRequest(
            Request.Method.POST, url, body,
            { response ->
                Toast.makeText(this, "Ferme ajoutée à la carte 🗺️", Toast.LENGTH_SHORT).show()
                val returnIntent = Intent().apply {
                    putExtra("lat", latitude)
                    putExtra("lon", longitude)
                    putExtra("nom", nom)
                    putExtra("taille", superficie)
                    putExtra("localisation", localisation)
                    putExtra("type_sol", typeSol)
                }
                setResult(RESULT_OK, returnIntent)
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)
                return hashMapOf("Authorization" to "Bearer $token")
            }
        }


        Volley.newRequestQueue(this).add(request)
    }

}