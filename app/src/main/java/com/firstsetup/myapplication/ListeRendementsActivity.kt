package com.firstsetup.myapplication

import com.firstsetup.myapplication.Rendement
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
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

class ListeRendementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cultivateurId = 5
    private lateinit var lineChart: LineChart
    private lateinit var spinnerAnnee: Spinner
    private var allRendements = listOf<Rendement>()
    private val handler = android.os.Handler()
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchRendements()
            handler.postDelayed(this, 1500) // relancer toutes les 3s
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_rendements)

        val serverPing = ServerPing()  // Create an instance of the ServerPing class
        serverPing.pingServer(this)

        recyclerView = findViewById(R.id.recyclerViewRendements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        lineChart = findViewById(R.id.lineChart)
        spinnerAnnee = findViewById(R.id.spinnerAnnee)

        fetchRendements()

        val btnCorbeille = findViewById<Button>(R.id.btnCorbeille)
        btnCorbeille.setOnClickListener {
            val intent = Intent(this, CorbeilleActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = recyclerView.adapter as RendementAdapter
                val rendement = adapter.getItem(position)

                Toast.makeText(this@ListeRendementsActivity, "🗑️ Suppression dans 4 secondes...", Toast.LENGTH_SHORT).show()

                // Laisse l'élément affiché pendant 4 secondes (y compris l'image)
                recyclerView.postDelayed({
                    val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)

                    if (token.isNullOrEmpty()) {
                        Toast.makeText(this@ListeRendementsActivity, "❌ Token manquant", Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(position)
                        return@postDelayed
                    }

                    val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/${rendement.id}"
                    val queue = Volley.newRequestQueue(this@ListeRendementsActivity)

                    val req = object : com.android.volley.toolbox.StringRequest(
                        com.android.volley.Request.Method.DELETE, url,
                        {
                            adapter.removeItem(position)
                        },
                        { error ->
                            Toast.makeText(this@ListeRendementsActivity, "❌ Erreur suppression : ${error.message}", Toast.LENGTH_SHORT).show()
                            adapter.notifyItemChanged(position)
                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            return hashMapOf("Authorization" to "Bearer $token")
                        }
                    }

                    queue.add(req)
                }, 1500)

            }



            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint().apply { color = Color.RED }

                // Dessiner fond rouge à droite
                val background = RectF(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, paint)

                // Afficher icône dropferme à droite
                val icon = ContextCompat.getDrawable(this@ListeRendementsActivity, R.drawable.dropferme)
                icon?.let {
                    val iconSize = minOf(itemView.height, 100) // carré max 100px
                    val iconTop = itemView.top + (itemView.height - iconSize) / 2
                    val iconRight = itemView.right - 32
                    val iconLeft = iconRight - iconSize
                    val iconBottom = iconTop + iconSize

                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    it.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }


        })

        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable) // démarre le refresh auto
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable) // stop quand on quitte l’activité
    }


    private fun fetchRendements() {
        val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "❌ Utilisateur non connecté", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements"

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
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
                val statusCode = error.networkResponse?.statusCode
                val responseData = error.networkResponse?.data?.toString(Charsets.UTF_8)

                Toast.makeText(this, "Erreur [$statusCode] : ${responseData ?: "inconnue"}", Toast.LENGTH_LONG).show()
                Log.e("RENDEMENT_ERROR", "Code: $statusCode\n$responseData")
            }

        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
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
                afficherFiltreParAnnee(parent.getItemAtPosition(position).toString())
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

        lineChart.data = LineData(dataSetRendement, dataSetPertes)
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(labels)
        }

        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            fetchRendements() // recharger les rendements après suppression/restauration
        }
    }
}
