package com.firstsetup.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
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
import java.util.Calendar
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.cardview.widget.CardView

class AjouterCultureActivity : AppCompatActivity() {

    private lateinit var spinnerTypeCulture: Spinner
    private lateinit var seekBarSurface: SeekBar
    private lateinit var textSurface: TextView
    private lateinit var spinnerSaison: Spinner
    private lateinit var buttonDatePlantation: Button
    private lateinit var checkboxBonneSante: CheckBox
    private lateinit var checkboxMaladie: CheckBox
    private lateinit var checkboxSeche: CheckBox
    private lateinit var btnAjouterCulture: Button

    private var fermeId: Int = -1
    private var selectedDatePlantation: String = ""

    private lateinit var pingCard: CardView
    private lateinit var pingText: TextView
    private lateinit var pingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_culture)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        pingCard = findViewById(R.id.pingCard)
        pingText = findViewById(R.id.pingText)
        pingProgress = findViewById(R.id.pingProgress)

        // Initialisation
        spinnerTypeCulture = findViewById(R.id.spinnerTypeCulture)
        seekBarSurface = findViewById(R.id.seekBarSurface)
        textSurface = findViewById(R.id.textSurface)
        spinnerSaison = findViewById(R.id.spinnerSaison)
        buttonDatePlantation = findViewById(R.id.buttonDatePlantation)
        checkboxBonneSante = findViewById(R.id.checkboxBonneSante)
        checkboxMaladie = findViewById(R.id.checkboxMaladie)
        checkboxSeche = findViewById(R.id.checkboxSeche)
        btnAjouterCulture = findViewById(R.id.btnAjouterCulture)

        fermeId = intent.getIntExtra("ferme_id", -1)

        // Spinner Type de Culture (avec images)
        val typesCulture = listOf("Blé dur", "Orge", "Maïs")
        val imagesType = listOf(
            R.drawable.ble, R.drawable.orge, R.drawable.mais
        )
        spinnerTypeCulture.adapter = EspeceSpinnerAdapter(this, typesCulture, imagesType)

        // Spinner Saison (avec images)
        val saisons = listOf("Printemps", "Été", "Automne", "Hiver")
        val imagesSaison = listOf(
            R.drawable.printemp, R.drawable.ete, R.drawable.automne, R.drawable.hiver
        )
        spinnerSaison.adapter = EspeceSpinnerAdapter(this, saisons, imagesSaison)

        // SeekBar Surface
        seekBarSurface.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSurface.text = "Surface : $progress m²"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Date plantation bouton
        buttonDatePlantation.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    selectedDatePlantation = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDayOfMonth
                    )
                    buttonDatePlantation.text = selectedDatePlantation
                }, year, month, day
            )
            datePickerDialog.show()
        }

        // CheckBoxes Exclusifs
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

        // Ajouter Culture
        btnAjouterCulture.setOnClickListener {
            pingCard.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                pingCard.visibility = View.GONE
            }, 10000)
            ajouterCulture()
        }
    }

    private fun ajouterCulture() {
        val type = spinnerTypeCulture.selectedItem.toString()
        val surface = seekBarSurface.progress
        val saison = spinnerSaison.selectedItem.toString()
        val etat = when {
            checkboxBonneSante.isChecked -> "Bonne santé"
            checkboxMaladie.isChecked -> "Maladie"
            checkboxSeche.isChecked -> "Séchage"
            else -> ""
        }

        if (type.isEmpty() || surface == 0 || saison.isEmpty() || selectedDatePlantation.isEmpty() || etat.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("ferme_id", fermeId)
            put("type", type)
            put("surface", surface)
            put("saison", saison)
            put("date_plantation", selectedDatePlantation)
            put("etat_sante", etat)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/culture"

        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            {
                Toast.makeText(this, "Culture ajoutée ✅", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}