package com.firstsetup.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import kotlin.math.abs
import kotlin.math.sin
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.widget.Spinner



class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var btnReset: Button
    private var polygon: Polygon? = null
    private lateinit var spinnerMapType: Spinner


    private val points = mutableListOf<GeoPoint>()
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setContentView(R.layout.activity_map)
        spinnerMapType = findViewById(R.id.spinnerMapType)

        val tileOptions = listOf("Standard", "Satellite", "Hike & Bike", "USGS Topo", "OpenTopo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tileOptions)
        spinnerMapType.adapter = adapter

        spinnerMapType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> map.setTileSource(TileSourceFactory.MAPNIK) // Standard
                    1 -> map.setTileSource(TileSourceFactory.USGS_SAT) // Satellite
                    2 -> map.setTileSource(TileSourceFactory.HIKEBIKEMAP) // Hike & Bike
                    3 -> map.setTileSource(TileSourceFactory.USGS_TOPO) // Topographic
                    4 -> map.setTileSource(TileSourceFactory.OpenTopo) // OpenTopo
                }
                map.invalidate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        map = findViewById(R.id.map)
        btnReset = findViewById(R.id.btnReset)

        map.setMultiTouchControls(true)
        map.controller.setZoom(16.0)
        map.controller.setCenter(GeoPoint(34.0209, -6.8416)) // Rabat

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    if (points.size < 4) {
                        points.add(it)
                        addMarker(it)

                        if (points.size == 4) {
                            drawPolygon()
                            val area = calculateArea(points)
                            Toast.makeText(
                                this@MapActivity,
                                "Surface ≈ %.2f m²".format(area),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })

        map.overlays.add(mapEventsOverlay)

        btnReset.setOnClickListener {
            markers.forEach { map.overlays.remove(it) }
            markers.clear()

            polygon?.let { map.overlays.remove(it) }
            polygon = null

            points.clear()
            map.invalidate()
        }
    }

    private fun addMarker(point: GeoPoint) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
        markers.add(marker)
        map.invalidate()
    }

    private fun drawPolygon() {
        if (points.size < 3) return // évite l'erreur

        polygon = Polygon().apply {
            points = this@MapActivity.points + this@MapActivity.points[0] // refermer le polygone
            strokeWidth = 5f
            fillColor = 0x12121212
        }
        map.overlays.add(polygon)
        map.invalidate()
    }


    private fun calculateArea(points: List<GeoPoint>): Double {
        if (points.size < 3) return 0.0
        val R = 6371000.0

        var total = 0.0
        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]
            val lat1 = Math.toRadians(p1.latitude)
            val lon1 = Math.toRadians(p1.longitude)
            val lat2 = Math.toRadians(p2.latitude)
            val lon2 = Math.toRadians(p2.longitude)
            total += (lon2 - lon1) * (2 + sin(lat1) + sin(lat2))
        }

        return abs(total * R * R / 2.0)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
