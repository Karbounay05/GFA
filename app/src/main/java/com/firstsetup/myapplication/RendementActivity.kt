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

class RendementActivity : AppCompatActivity() {

    private lateinit var inputCategorie: EditText
    private lateinit var inputSuperficie: EditText
    private lateinit var inputProduction: EditText
    private lateinit var inputPertes: EditText
    private lateinit var btnCalculer: Button
    private lateinit var btnVoir: Button
    private lateinit var textRendementParHa: TextView
    private lateinit var textPertesParHa: TextView
    private lateinit var textDateChoisie: TextView

    private var moisChoisi = ""
    private var anneeChoisie = ""

    private val cultivateurId = 5 // à remplacer dynamiquement si besoin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rendement)

        inputCategorie = findViewById(R.id.inputCategorie)
        inputSuperficie = findViewById(R.id.inputSuperficie)
        inputProduction = findViewById(R.id.inputProduction)
        inputPertes = findViewById(R.id.inputPertes)
        btnCalculer = findViewById(R.id.btnEnvoyerRendement)
        btnVoir = findViewById(R.id.btnVoirRendements)
        textRendementParHa = findViewById(R.id.textRendementParHa)
        textPertesParHa = findViewById(R.id.textPertesParHa)
        textDateChoisie = findViewById(R.id.textDateChoisie)

        // ➤ Sélecteur de date
        textDateChoisie.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, _ ->
                val moisNoms = arrayOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet",
                    "Août", "Septembre", "Octobre", "Novembre", "Décembre")

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
        val categorie = inputCategorie.text.toString().trim()
        val superficie = inputSuperficie.text.toString().toDoubleOrNull()
        val production = inputProduction.text.toString().toDoubleOrNull()
        val pertes = inputPertes.text.toString().toDoubleOrNull()

        // Vérif champs
        if (categorie.isEmpty() || superficie == null || production == null || pertes == null || moisChoisi.isEmpty() || anneeChoisie.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs et sélectionner la date", Toast.LENGTH_LONG).show()
            return
        }

        // 🔢 Calcul
        val rendementParHa = production / superficie
        val pertesParHa = pertes / superficie

        // 🧾 Affichage local
        textRendementParHa.text = "Rendement par hectare : %.2f".format(rendementParHa)
        textPertesParHa.text = "Pertes par hectare : %.2f".format(pertesParHa)

        // 📡 Envoi serveur
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Request.Method.POST, url,
            { _ ->
                Toast.makeText(this, "✅ Rendement enregistré", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur : ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getBodyContentType(): String = "application/json"

            override fun getBody(): ByteArray {
                val json = JSONObject()
                json.put("cultivateur_id", cultivateurId)
                json.put("categorie", categorie)
                json.put("superficie", superficie)
                json.put("production", production)
                json.put("pertes", pertes)
                json.put("mois", moisChoisi)
                json.put("annee", anneeChoisie)
                json.put("rendement_par_ha", rendementParHa)
                json.put("pertes_par_ha", pertesParHa)
                return json.toString().toByteArray()
            }
        }

        queue.add(request)
    }
}
