package com.firstsetup.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.firstsetup.myapplication.model.Ferme
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SuivreParcelleActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var spinnerFerme: Spinner
    private lateinit var pieChartMain: PieChart
    private lateinit var pieChartEtat: PieChart
    private lateinit var barChartValeur: BarChart
    private lateinit var timerText: TextView
    private lateinit var switchType: Switch

    private var typeAffichage = "culture"
    private var currentDataArray: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivre_parcelle)

        spinnerFerme = findViewById(R.id.spinnerFerme)
        pieChartMain = findViewById(R.id.pieChart)
        pieChartEtat = findViewById(R.id.pieChartEtat)
        barChartValeur = findViewById(R.id.barChartValeur)
        timerText = findViewById(R.id.timer_text)
        switchType = findViewById(R.id.switchType)

        pieChartMain.setOnChartValueSelectedListener(this)

     chargerFermes()

        switchType.setOnCheckedChangeListener { _, isChecked ->
            typeAffichage = if (isChecked) "animal" else "culture"
            val selectedFerme = spinnerFerme.selectedItem as? Ferme
            selectedFerme?.let { afficherGraphiquePrincipal(it.id) }
        }
    }

    private fun chargerFermes() {
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show()
            Log.e("FERME", "❌ Token manquant dans SharedPreferences")
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/mes-fermes"

        val request = object : com.android.volley.toolbox.StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val fermes = mutableListOf<Ferme>()
                try {
                    val json = JSONObject(response)
                    val fermeArray = json.getJSONArray("fermes")
                    for (i in 0 until fermeArray.length()) {
                        val obj = fermeArray.getJSONObject(i)
                        fermes.add(
                            Ferme(
                                obj.getInt("id"),
                                obj.getString("nom"),
                                obj.getString("localisation"),
                                obj.getDouble("taille"),
                                obj.getString("type_sol")
                            )
                        )
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fermes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFerme.adapter = adapter

                    spinnerFerme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            afficherGraphiquePrincipal(fermes[position].id)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                } catch (e: Exception) {
                    Log.e("FERME", "Parsing error", e)
                }
            },
            { error ->
                Log.e("FERME", "Erreur réseau: ${error.message}")
                Toast.makeText(this, "Erreur réseau ou token refusé", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }


    private fun afficherGraphiquePrincipal(fermeId: Int) {
        val url = if (typeAffichage == "culture")
            "https://fluorescent-boiled-butter.glitch.me/culture/$fermeId"
        else
            "https://fluorescent-boiled-butter.glitch.me/animaux/$fermeId"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                currentDataArray = if (typeAffichage == "culture") response.getJSONArray("cultures") else response.getJSONArray("animaux")
                val typeMap = mutableMapOf<String, Float>()

                for (i in 0 until currentDataArray!!.length()) {
                    val item = currentDataArray!!.getJSONObject(i)
                    val type = if (typeAffichage == "culture") item.getString("type") else item.getString("espece")
                    val valeur = if (typeAffichage == "culture")
                        item.getString("surface").toFloatOrNull() ?: 0f
                    else item.getInt("nombre").toFloat()

                    typeMap[type] = typeMap.getOrDefault(type, 0f) + valeur
                }

                val pieEntries = typeMap.map { PieEntry(it.value, it.key) }
                val dataSet = PieDataSet(pieEntries, "Répartition")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                pieChartMain.data = PieData(dataSet)
                pieChartMain.invalidate()
            },
            { Log.e("Volley", "Erreur graphique: ${it.message}") }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val label = (e as? PieEntry)?.label ?: return
        val champEtat = if (typeAffichage == "culture") "etat_sante" else "statut_sanitaire"
        val champDate = if (typeAffichage == "culture") "date_plantation" else "date_entree"
        val champValeur = if (typeAffichage == "culture") "surface" else "nombre"

        val selected = (0 until currentDataArray!!.length())
            .map { currentDataArray!!.getJSONObject(it) }
            .find {
                val type = if (typeAffichage == "culture") it.getString("type") else it.getString("espece")
                type == label
            } ?: return

        val dateStr = selected.getString(champDate)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val dateDebut = formatter.parse(dateStr) ?: Date()
        val diffJours = ((Date().time - dateDebut.time) / (1000 * 60 * 60 * 24))
        val diffSemaines = diffJours / 7

        timerText.text = "Depuis : $diffJours jours ($diffSemaines semaines)"

        val etat = selected.getString(champEtat)
        val valeur = if (typeAffichage == "culture") selected.getString("surface").toFloatOrNull() ?: 0f
        else selected.getInt("nombre").toFloat()

        // PieChart état
        val etatEntries = listOf(PieEntry(1f, etat))
        val etatSet = PieDataSet(etatEntries, "État de santé")
        etatSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        pieChartEtat.data = PieData(etatSet)
        pieChartEtat.invalidate()

        // BarChart valeur
        val barEntries = listOf(BarEntry(0f, valeur))
        val barDataSet = BarDataSet(barEntries, if (typeAffichage == "culture") "Surface (ha)" else "Nombre")
        barDataSet.color = Color.rgb(100, 149, 237)
        val barData = BarData(barDataSet)
        barData.barWidth = 0.4f

        barChartValeur.data = barData
        barChartValeur.description.text = ""
        barChartValeur.setFitBars(true)
        barChartValeur.axisLeft.axisMinimum = 0f
        barChartValeur.axisRight.isEnabled = false
        barChartValeur.xAxis.valueFormatter = IndexAxisValueFormatter(listOf(label))
        barChartValeur.xAxis.granularity = 1f
        barChartValeur.invalidate()
    }

    override fun onNothingSelected() {}
}
