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

class AjouterAnimalActivity : AppCompatActivity() {

    private lateinit var spinnerEspece: Spinner
    private lateinit var seekBarNombre: SeekBar
    private lateinit var textNombre: TextView
    private lateinit var buttonDateEntree: Button
    private lateinit var checkboxBonneSante: CheckBox
    private lateinit var checkboxMaladie: CheckBox
    private lateinit var checkboxAutre: CheckBox
    private lateinit var btnAjouterAnimal: Button

    private var fermeId: Int = -1
    private var selectedNombre: Int = 0
    private var selectedDateEntree: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_animal)

        spinnerEspece = findViewById(R.id.spinnerEspece)
        seekBarNombre = findViewById(R.id.seekBarNombre)
        textNombre = findViewById(R.id.textNombre)
        buttonDateEntree = findViewById(R.id.buttonDateEntree)
        checkboxBonneSante = findViewById(R.id.checkboxBonneSante)
        checkboxMaladie = findViewById(R.id.checkboxMaladie)
        checkboxAutre = findViewById(R.id.checkboxAutre)
        btnAjouterAnimal = findViewById(R.id.btnAjouterAnimal)

        fermeId = intent.getIntExtra("ferme_id", -1)

        // Spinner espèces avec image
        val especes = listOf("Vache", "Mouton", "Poulet", "Chèvre", "chien", "âne", "dinde")
        val images = listOf(
            R.drawable.ic_vache,
            R.drawable.ic_mouton,
            R.drawable.ic_poulet,
            R.drawable.ic_chevre,
            R.drawable.ic_dog,
            R.drawable.ic_donkey,
            R.drawable.ic_turkey
        )

        val adapter = EspeceSpinnerAdapter(this, especes, images)
        spinnerEspece.adapter = adapter

        // SeekBar pour nombre
        seekBarNombre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedNombre = progress
                textNombre.text = "Nombre : $selectedNombre"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Date Picker
        buttonDateEntree.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    selectedDateEntree = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDayOfMonth
                    )
                    buttonDateEntree.text = selectedDateEntree
                }, year, month, day
            )

            datePickerDialog.show()
        }

        // Checkboxes exclusives
        checkboxBonneSante.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxMaladie.isChecked = false
                checkboxAutre.isChecked = false
            }
        }
        checkboxMaladie.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxBonneSante.isChecked = false
                checkboxAutre.isChecked = false
            }
        }
        checkboxAutre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkboxBonneSante.isChecked = false
                checkboxMaladie.isChecked = false
            }
        }

        btnAjouterAnimal.setOnClickListener {
            ajouterAnimal()
        }
    }

    private fun ajouterAnimal() {
        val espece = spinnerEspece.selectedItem.toString()

        val statutSanitaire = when {
            checkboxBonneSante.isChecked -> "Bonne Santé"
            checkboxMaladie.isChecked -> "Maladie"
            checkboxAutre.isChecked -> "Autre"
            else -> ""
        }

        if (espece.isEmpty() || selectedNombre == 0 || selectedDateEntree.isEmpty() || statutSanitaire.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs correctement", Toast.LENGTH_SHORT).show()
            return
        }

        val body = JSONObject().apply {
            put("ferme_id", fermeId)
            put("espece", espece)
            put("nombre", selectedNombre)
            put("date_entree", selectedDateEntree)
            put("statut_sanitaire", statutSanitaire)
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/animaux"

        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
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