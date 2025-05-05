package com.firstsetup.myapplication

import Rendement
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONObject

class ListeRendementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cultivateurId = 5
    private lateinit var lineChart: LineChart
    private lateinit var spinnerAnnee: Spinner
    private var allRendements = listOf<Rendement>()
    private var corbeille = mutableListOf<Rendement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_rendements)

        recyclerView = findViewById(R.id.recyclerViewRendements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        lineChart = findViewById(R.id.lineChart)
        spinnerAnnee = findViewById(R.id.spinnerAnnee)

        corbeille = loadCorbeille()

        fetchRendements()

        val btnCorbeille = findViewById<Button>(R.id.btnCorbeille)
        btnCorbeille.setOnClickListener {
            val intent = Intent(this, CorbeilleActivity::class.java)
            intent.putParcelableArrayListExtra("corbeille", ArrayList(corbeille))
            startActivity(intent)
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val rendement = (recyclerView.adapter as RendementAdapter).getItem(position)

                corbeille.add(rendement)
                saveCorbeilleLocally()

                (recyclerView.adapter as RendementAdapter).removeItem(position)
                Toast.makeText(this@ListeRendementsActivity, "🗑️ Supprimé : ${rendement.categorie}", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                paint.color = Color.RED

                val icon = ContextCompat.getDrawable(this@ListeRendementsActivity, R.drawable.dropferme)
                val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2

                val background = RectF(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )

                c.drawRect(background, paint)

                icon?.let {
                    val iconTop = itemView.top + iconMargin
                    val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + it.intrinsicHeight

                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    it.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun saveCorbeilleLocally() {
        val prefs = getSharedPreferences("corbeille", MODE_PRIVATE)
        val editor = prefs.edit()
        val json = JSONArray()

        for (item in corbeille) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("categorie", item.categorie)
            obj.put("superficie", item.superficie)
            obj.put("production", item.production)
            obj.put("pertes", item.pertes)
            obj.put("mois", item.mois)
            obj.put("annee", item.annee)
            obj.put("rendementParHa", item.rendementParHa)
            obj.put("pertesParHa", item.pertesParHa)
            json.put(obj)
        }

        editor.putString("liste", json.toString())
        editor.apply()
    }

    private fun loadCorbeille(): MutableList<Rendement> {
        val prefs = getSharedPreferences("corbeille", MODE_PRIVATE)
        val corbeille = mutableListOf<Rendement>()
        val data = prefs.getString("liste", null) ?: return corbeille

        val array = JSONArray(data)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            corbeille.add(
                Rendement(
                    id = obj.getInt("id"),
                    categorie = obj.getString("categorie"),
                    superficie = obj.getDouble("superficie"),
                    production = obj.getDouble("production"),
                    pertes = obj.getDouble("pertes"),
                    mois = obj.getString("mois"),
                    annee = obj.getString("annee"),
                    rendementParHa = obj.getDouble("rendementParHa"),
                    pertesParHa = obj.getDouble("pertesParHa")
                )
            )
        }
        return corbeille
    }

    private fun fetchRendements() {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/$cultivateurId"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            url,
            { response ->
                val rendements = mutableListOf<Rendement>()
                val array = response.getJSONArray("rendements")

                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    rendements.add(
                        Rendement(
                            id = item.getInt("id"),
                            categorie = item.getString("categorie"),
                            superficie = item.getDouble("superficie"),
                            production = item.getDouble("production"),
                            pertes = item.getDouble("pertes"),
                            mois = item.getString("mois"),
                            annee = item.getString("annee"),
                            rendementParHa = item.getDouble("rendement_par_ha"),
                            pertesParHa = item.getDouble("pertes_par_ha")
                        )
                    )
                }

                allRendements = rendements
                initialiserFiltrageParAnnee()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur chargement : ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }

    private fun initialiserFiltrageParAnnee() {
        val annees = allRendements.map { it.annee }.distinct().sortedDescending()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, annees)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnnee.adapter = adapter

        if (annees.isNotEmpty()) {
            spinnerAnnee.setSelection(0)
            afficherFiltreParAnnee(annees[0])
        }

        spinnerAnnee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val annee = parent.getItemAtPosition(position).toString()
                afficherFiltreParAnnee(annee)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun afficherFiltreParAnnee(annee: String) {
        val rendementsParAnnee = allRendements.filter { it.annee == annee }.toMutableList()
        recyclerView.adapter = RendementAdapter(rendementsParAnnee)
        afficherGraphique(rendementsParAnnee)
    }



    private fun afficherGraphique(rendements: List<Rendement>) {
        val entriesRendement = ArrayList<Entry>()
        val entriesPertes = ArrayList<Entry>()
        val labels = ArrayList<String>()

        for ((index, r) in rendements.withIndex()) {
            entriesRendement.add(Entry(index.toFloat(), r.rendementParHa.toFloat()))
            entriesPertes.add(Entry(index.toFloat(), r.pertesParHa.toFloat()))
            labels.add("${r.mois} ${r.annee}")
        }

        val dataSetRendement = LineDataSet(entriesRendement, "Rendement/ha").apply {
            color = Color.GREEN
            circleRadius = 4f
            setCircleColor(Color.GREEN)
            lineWidth = 2f
        }

        val dataSetPertes = LineDataSet(entriesPertes, "Pertes/ha").apply {
            color = Color.RED
            circleRadius = 4f
            setCircleColor(Color.RED)
            lineWidth = 2f
        }

        val lineData = LineData(dataSetRendement, dataSetPertes)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }
}
