package com.firstsetup.myapplication

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.events.MapListener
import org.osmdroid.events.ZoomEvent
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley


class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var btnReset: Button
    private lateinit var spinnerMapType: Spinner
    private lateinit var btnTogglePanel: Button
    private lateinit var iconPanel: LinearLayout
    private lateinit var legendPanel: LinearLayout
    private lateinit var markerIdContainer: LinearLayout
    private lateinit var toggleMarkerListBtn: Button
    private val markers = mutableListOf<Marker>()
    private lateinit var prefs: SharedPreferences
    private val markerIdToMarker = mutableMapOf<String, Marker>()
    private val markerCountMap = mutableMapOf<String, Int>()
    private lateinit var markerListCard: View
    private var isMarkerListVisible = false
    private var dX = 0f
    private var dY = 0f
    private fun getResizedDrawable(drawableId: Int, width: Int, height: Int): BitmapDrawable {
        val drawable = ContextCompat.getDrawable(this, drawableId) as BitmapDrawable
        val bitmap = Bitmap.createScaledBitmap(drawable.bitmap, width, height, true)
        return BitmapDrawable(resources, bitmap)
    }
    private var isDrawing = false
    private var drawStartPoint: GeoPoint? = null
    private var tempRectangle: Polygon? = null
    private val selectedPoints = mutableListOf<GeoPoint>()
    private var areaPolygon: Polygon? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_map)

        map = findViewById(R.id.map)
        btnReset = findViewById(R.id.btnReset)
        spinnerMapType = findViewById(R.id.spinnerMapType)
        btnTogglePanel = findViewById(R.id.btnTogglePanel)
        iconPanel = findViewById(R.id.iconPanel)
        legendPanel = findViewById(R.id.legendPanel)
        toggleMarkerListBtn = findViewById(R.id.btnToggleMarkerList)
        prefs = getSharedPreferences("markers", Context.MODE_PRIVATE)
        markerListCard = findViewById(R.id.markerIdCard)
        markerIdContainer = findViewById(R.id.markerIdList)

        val btnShowOptions = findViewById<Button>(R.id.btnShowOptions)
        val panelOptions = findViewById<LinearLayout>(R.id.panelOptions)
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        var isPanelVisible = false

        btnShowOptions.setOnClickListener {
            if (!isPanelVisible) {
                panelOptions.visibility = View.VISIBLE
                panelOptions.startAnimation(slideDown)
            } else {
                panelOptions.startAnimation(slideUp)
                Handler(Looper.getMainLooper()).postDelayed({
                    panelOptions.visibility = View.GONE
                }, 300)
            }
            isPanelVisible = !isPanelVisible
        }



        toggleMarkerListBtn.setOnClickListener {
            isMarkerListVisible = !isMarkerListVisible
            markerListCard.visibility = if (isMarkerListVisible) View.VISIBLE else View.GONE
        }

        val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Utilisateur non connecté ❌", Toast.LENGTH_LONG).show()
        } else {
            // Tu peux maintenant utiliser le token pour tes requêtes
            // par exemple lancer une fonction qui dépend de l'utilisateur connecté
            chargerFermesDepuisAPI() // ou autre fonction
        }


        markerListCard.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            true
        }
        val tileOptions = listOf("Standard", "Satellite", "Hike & Bike", "USGS Topo", "OpenTopo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tileOptions)
        spinnerMapType.adapter = adapter

        spinnerMapType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                map.setTileSource(
                    when (position) {
                        0 -> TileSourceFactory.MAPNIK
                        1 -> TileSourceFactory.USGS_SAT
                        2 -> TileSourceFactory.HIKEBIKEMAP
                        3 -> TileSourceFactory.USGS_TOPO
                        else -> TileSourceFactory.OpenTopo
                    }
                )
                map.invalidate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        map.setMultiTouchControls(true)
        map.controller.setZoom(16.0)
        map.controller.setCenter(GeoPoint(34.0209, -6.8416))

        map.setOnTouchListener(null)

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

            override fun longPressHelper(p: GeoPoint?) = false
        }))

        btnReset.setOnClickListener {

            selectedPoints.clear()
            areaPolygon?.let { map.overlays.remove(it) }
            // 1. Supprimer tous les overlays sauf la couche de carte
            val toRemove = map.overlays.filterNot { it is MapEventsOverlay }
            map.overlays.removeAll(toRemove)

            // 2. Vider les marqueurs
            markers.clear()
            markerIdToMarker.clear()
            markerIdContainer.removeAllViews()

            // 3. Vider les rectangles temporaires
            tempRectangle = null
            drawStartPoint = null
            isDrawing = false

            // 4. Cacher cartes UI (optionnel)
            markerListCard.visibility = View.GONE
            iconPanel.visibility = View.GONE

            // 5. Mise à jour de la carte
            map.invalidate()
            refreshLegend()
        }


        btnTogglePanel.setOnClickListener {
            iconPanel.visibility = if (iconPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        val icons = listOf(
            R.id.iconAnimal to "animal",
            R.id.iconCulture to "culture",
            R.id.iconSize to "size",
            R.id.iconEntity to "entity"
        )

        for ((iconId, label) in icons) {
            findViewById<ImageView>(iconId).setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("label", label)
                    val shadowBuilder = object : View.DragShadowBuilder(view) {
                        override fun onDrawShadow(canvas: Canvas) {
                            super.onDrawShadow(canvas)
                            val paint = Paint().apply {
                                color = Color.BLACK
                                textSize = 50f
                                isAntiAlias = true
                            }
                            canvas.drawText("hello", 10f, view.height.toFloat() / 2, paint)
                        }
                    }
                    view.startDragAndDrop(data, shadowBuilder, null, 0)
                    true
                } else false
            }
        }

        map.setOnDragListener { _, event ->
            if (event.action == DragEvent.ACTION_DROP) {
                val label = event.clipData.getItemAt(0).text.toString()
                val point = map.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                if (label == "size") {
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("Ajouter une nouvelle ferme")
                    builder.setMessage("Est-ce que vous aimeriez ajouter une nouvelle ferme à cet endroit ?")

                    builder.setPositiveButton("Ajouter") { _, _ ->
                        val intent = Intent(this, AjouterFermeActivity::class.java)
                        intent.putExtra("latitude", point.latitude)
                        intent.putExtra("longitude", point.longitude)
                        intent.putExtra("fromMap", true) // IMPORTANT !
                        startActivityForResult(intent, 100) // 👈 ici la clé de ton bug
                    }


                    builder.setNegativeButton("Annuler") { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.show()
                } else {
                    addImageMarker(label, point)
                    refreshLegend()
                }
            }
            true
        }

        refreshLegend()

        map.addMapListener(object : MapListener {
            override fun onZoom(event: ZoomEvent): Boolean {
                updateMarkerIconsByZoom(event.zoomLevel.toDouble())
                return true
            }

            override fun onScroll(event: org.osmdroid.events.ScrollEvent): Boolean {
                return false
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            chargerFermesDepuisAPI()
        }
    }


    private fun chargerFermesDepuisAPI() {
        val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "❌ Token manquant", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/map"

        val request = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                // Clear anciens marqueurs
                val toRemove = markers.filter { it.relatedObject == "size" }
                for (marker in toRemove) {
                    map.overlays.remove(marker)
                }
                markers.removeAll(toRemove)

                // Ajout nouveaux marqueurs
                for (i in 0 until response.length()) {
                    val ferme = response.getJSONObject(i)
                    val nom = ferme.getString("nom")
                    val localisation = ferme.getString("localisation")
                    val taille = ferme.getDouble("taille")
                    val typeSol = ferme.getString("type_sol")
                    val lat = ferme.optDouble("lat", 0.0)
                    val lon = ferme.optDouble("lon", 0.0)
                    val fermeId = ferme.getInt("id")

                    if (lat != 0.0 && lon != 0.0) {
                        val point = GeoPoint(lat, lon)
                        val marker = Marker(map).apply {
                            position = point
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = getResizedDrawable(R.drawable.addferme, 60, 60)
                            title = "🚜 $nom\n📐 $taille ha\n📍 $localisation\n🧱 $typeSol"
                            id = fermeId.toString()
                            setDraggable(true)
                            relatedObject = "size"
                        }

                        map.overlays.add(marker)
                        markers.add(marker)
                        setupMarkerClick(marker)
                    }
                }

                map.invalidate()
                refreshLegend()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur réseau : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }



    private fun setupMarkerClick(marker: Marker) {
        val markerCard = findViewById<LinearLayout>(R.id.markerCard)
        val markerText = findViewById<TextView>(R.id.markerCardText)
        val removeBtn = findViewById<Button>(R.id.markerCardRemove)
        val gotoBtn = findViewById<Button>(R.id.markerCardGoto)
        val modifierButton = findViewById<Button>(R.id.markerCardEdit)
        val addAnimalBtn = findViewById<Button>(R.id.markerCardAddAnimal)
        val addCultureBtn = findViewById<Button>(R.id.markerCardAddCulture)

        marker.setOnMarkerClickListener { m, _ ->
            m.showInfoWindow()

            // Affiche la carte en bas
            markerText.text = m.title ?: "🗺 Info non disponible"
            markerCard.visibility = View.VISIBLE
            gotoBtn.visibility = View.VISIBLE
            modifierButton.visibility = View.VISIBLE

            // ➖ Bouton SUPPRIMER avec confirmation
            removeBtn.setOnClickListener {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                builder.setMessage("🗑️ Est-ce que vous aimeriez supprimer cette ferme ?")

                builder.setPositiveButton("Oui") { _, _ ->
                    val fermeId = m.id?.toIntOrNull()

                    if (fermeId != null) {
                        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/map/$fermeId"

                        val request = object : com.android.volley.toolbox.StringRequest(
                            Request.Method.DELETE, url,
                            {
                                map.overlays.remove(m)
                                markers.remove(m)
                                markerCard.visibility = View.GONE
                                map.invalidate()
                                Toast.makeText(this, "✅ Ferme supprimée", Toast.LENGTH_SHORT).show()
                            },
                            { error ->
                                val statusCode = error.networkResponse?.statusCode
                                val data = error.networkResponse?.data?.toString(Charsets.UTF_8)

                                Toast.makeText(
                                    this@MapActivity,
                                    "❌ Code HTTP: $statusCode\n${data ?: "Erreur inconnue"}",
                                    Toast.LENGTH_LONG
                                ).show()

                                Log.e("SUPPRESSION_FERME", "Erreur suppression: Code $statusCode\n$data", error)
                            }


                        ) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val token = getSharedPreferences("user", MODE_PRIVATE).getString("jwt_token", null)
                                return hashMapOf("Authorization" to "Bearer $token")
                            }
                        }

                        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request)
                    } else {
                        Toast.makeText(this, "❌ ID invalide", Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("Annuler") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }


            // 🔁 Bouton GO TO
            gotoBtn.setOnClickListener {
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
            }




            modifierButton.setOnClickListener {
                val intent = Intent(this, ModifierFermeActivity::class.java)
                intent.putExtra("ferme_id", marker.id.toInt())
                intent.putExtra("nom", extractNomFromTitle(marker.title))
                intent.putExtra("superficie", extractTailleFromTitle(marker.title))
                intent.putExtra("localisation", extractLocalisationFromTitle(marker.title))
                intent.putExtra("type_sol", extractTypeSolFromTitle(marker.title))
                intent.putExtra("latitude", marker.position.latitude)
                intent.putExtra("longitude", marker.position.longitude)
                intent.putExtra("fromMap", true)
                startActivityForResult(intent, 100)
            }



            true
        }
        addAnimalBtn.setOnClickListener {
            val fermeId = marker.id?.toIntOrNull()
            if (fermeId != null) {
                val intent = Intent(this, AjouterAnimalActivity::class.java)
                intent.putExtra("ferme_id", fermeId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID invalide", Toast.LENGTH_SHORT).show()
            }
        }

        addCultureBtn.setOnClickListener {
            val fermeId = marker.id?.toIntOrNull()
            if (fermeId != null) {
                val intent = Intent(this, AjouterCultureActivity::class.java)
                intent.putExtra("ferme_id", fermeId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID invalide", Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun addImageMarker(type: String, point: GeoPoint, customTitle: String? = null) {
        val count = markerCountMap.getOrDefault(type, 0) + 1
        markerCountMap[type] = count
        val markerId = "$type-$count"

        val marker = Marker(map).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = when (type) {
                "animal" -> getResizedDrawable(R.drawable.livestock, 60, 60)
                "culture" -> getResizedDrawable(R.drawable.culture, 60, 60)
                "size" -> getResizedDrawable(R.drawable.addferme, 60, 60)
                "entity" -> getResizedDrawable(R.drawable.ex4, 60, 60)
                else -> getResizedDrawable(R.drawable.ex1, 60, 60)
            }
            relatedObject = type
            setDraggable(true)

            title = customTitle ?: "the marker is: $markerId"
        }

        markerIdContainer.addView(TextView(this).apply {
            text = markerId
            textSize = 13f
            setPadding(8, 4, 8, 4)
            setTextColor(Color.BLACK)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
            setOnClickListener {
                map.controller.animateTo(marker.position)
                marker.showInfoWindow()
            }
        })

        markerIdToMarker[markerId] = marker
        map.overlays.add(marker)
        markers.add(marker)
        map.invalidate()
    }

    private fun updateMarkerIconsByZoom(zoom: Double) {
        val baseSize = 50.0
        val scaleFactor = zoom / 16.0

        for (marker in markers) {
            val type = marker.relatedObject as? String ?: continue

            // 🔽 ZOOM OUT (cacher sauf "addferme")
            if (zoom <= 20 && type != "size") {
                if (map.overlays.contains(marker)) {
                    map.overlays.remove(marker)

                }
                continue
            }

            // 🔼 ZOOM IN (réafficher)
            if (!map.overlays.contains(marker)) {
                map.overlays.add(marker)
            }

            // Redimensionnement
            val drawableId = when (type) {
                "animal" -> R.drawable.livestock
                "culture" -> R.drawable.culture
                "size" -> R.drawable.addferme
                "entity" -> R.drawable.ex4
                else -> R.drawable.ex1
            }

            val newSize = (baseSize * scaleFactor).toInt().coerceIn(24, 100)

            val bitmap = Bitmap.createScaledBitmap(
                (ContextCompat.getDrawable(this, drawableId) as BitmapDrawable).bitmap,
                newSize,
                newSize,
                true
            )

            marker.icon = BitmapDrawable(resources, bitmap)
        }

        map.invalidate()
    }

    private fun refreshLegend() {
        markerCountMap.clear()  // 🧼 Clear the previous counts to avoid accumulation

        val counts = mutableMapOf<String, Int>()
        for (marker in markers) {
            val type = marker.relatedObject as? String ?: continue
            counts[type] = counts.getOrDefault(type, 0) + 1
        }

        markerCountMap.putAll(counts) // 🔁 Resynchronize the counter map

        legendPanel.removeAllViews()
        val title = TextView(this).apply {
            text = "🗺 Legend:"
            textSize = 16f
            setPadding(4, 4, 4, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        legendPanel.addView(title)
        for ((type, count) in counts) {
            val label = when (type) {
                "animal" -> "🐄 Animal"
                "culture" -> "🌾 Culture"
                "size" -> "📏 Size"
                "entity" -> "🏠 Entity"
                else -> type
            }
            val line = TextView(this).apply {
                text = "$label = ${count}x"
                textSize = 14f
                setPadding(4, 0, 4, 4)
            }
            legendPanel.addView(line)
        }
    }



    private fun extractNomFromTitle(title: String): String {
        return title.substringAfter("🚜 ").substringBefore("\n")
    }

    private fun extractTailleFromTitle(title: String): String {
        return title.substringAfter("📐 ").substringBefore(" ha")
    }

    private fun extractLocalisationFromTitle(title: String): String {
        return title.substringAfter("📍 ").substringBefore("\n")
    }

    private fun extractTypeSolFromTitle(title: String): String {
        return title.substringAfter("🧱 ")
    }


    override fun onResume() {
        super.onResume()
        map.onResume()

        chargerFermesDepuisAPI()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
