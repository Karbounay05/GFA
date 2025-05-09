package com.firstsetup.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONObject
import java.util.*
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.cardview.widget.CardView

class RendementActivity : AppCompatActivity() {

    private lateinit var spinnerCategorie: Spinner
    private lateinit var seekBarSuperficie: SeekBar
    private lateinit var labelSuperficie: TextView
    private lateinit var inputProduction: EditText
    private lateinit var inputPertes: EditText
    private lateinit var btnCalculer: Button
    private lateinit var btnVoir: Button
    private lateinit var textRendementParHa: TextView
    private lateinit var textPertesParHa: TextView
    private lateinit var textDateChoisie: TextView

    private var moisChoisi = ""
    private var anneeChoisie = ""


    private lateinit var pingCard: CardView
    private lateinit var pingText: TextView
    private lateinit var pingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rendement)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        pingCard = findViewById(R.id.pingCard)
        pingText = findViewById(R.id.pingText)
        pingProgress = findViewById(R.id.pingProgress)

        // 🌾 Categorie Spinner
        spinnerCategorie = findViewById(R.id.spinnerCategorie)
        val categories = listOf("Blé", "Maïs", "Olives", "Pommes de terre", "Autres")
        spinnerCategorie.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        // 📏 Superficie SeekBar
        seekBarSuperficie = findViewById(R.id.seekBarSuperficie)
        labelSuperficie = findViewById(R.id.labelSuperficie)

        seekBarSuperficie.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                labelSuperficie.text = "Superficie : $progress ha"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        inputProduction = findViewById(R.id.inputProduction)
        inputPertes = findViewById(R.id.inputPertes)
        btnCalculer = findViewById(R.id.btnEnvoyerRendement)
        btnVoir = findViewById(R.id.btnVoirRendements)
        textRendementParHa = findViewById(R.id.textRendementParHa)
        textPertesParHa = findViewById(R.id.textPertesParHa)
        textDateChoisie = findViewById(R.id.textDateChoisie)

        // 📅 Date picker
        textDateChoisie.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, _ ->
                val moisNoms = arrayOf(
                    "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet",
                    "Août", "Septembre", "Octobre", "Novembre", "Décembre"
                )
                moisChoisi = moisNoms[selectedMonth]
                anneeChoisie = selectedYear.toString()
                textDateChoisie.text = "$moisChoisi $anneeChoisie"
            }, year, month, day)

            dpd.show()
        }

        btnCalculer.setOnClickListener {

            calculerEtEnvoyer()
        }

        btnVoir.setOnClickListener {
            startActivity(Intent(this, ListeRendementsActivity::class.java))
        }
    }

    private fun calculerEtEnvoyer() {
        val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)

        val categorie = spinnerCategorie.selectedItem.toString()
        val superficie = seekBarSuperficie.progress.toDouble()
        val production = inputProduction.text.toString().toDoubleOrNull()
        val pertes = inputPertes.text.toString().toDoubleOrNull()

        if (categorie.isEmpty() || superficie == 0.0 || production == null || pertes == null || moisChoisi.isEmpty() || anneeChoisie.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs et sélectionner la date", Toast.LENGTH_LONG).show()
            return
        }

        val rendementParHa = production / superficie
        val pertesParHa = pertes / superficie

        pingCard.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            pingCard.visibility = View.GONE
        }, 10000)

        textRendementParHa.text = "Rendement par hectare : %.2f".format(rendementParHa)
        textPertesParHa.text = "Pertes par hectare : %.2f".format(pertesParHa)

        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Request.Method.POST, url,
            {
                Toast.makeText(this, "✅ Rendement enregistré", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getBodyContentType(): String = "application/json"

            override fun getBody(): ByteArray {
                val json = JSONObject().apply {
                    put("categorie", categorie)
                    put("superficie", superficie)
                    put("production", production)
                    put("pertes", pertes)
                    put("mois", moisChoisi)
                    put("annee", anneeChoisie)
                    put("rendement_par_ha", rendementParHa)
                    put("pertes_par_ha", pertesParHa)
                }
                return json.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer $token")
            }
        }

        queue.add(request)
    }

}
