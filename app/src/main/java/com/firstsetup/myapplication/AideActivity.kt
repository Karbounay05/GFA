package com.firstsetup.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firstsetup.myapplication.model.AideItem
import org.json.JSONArray

class AideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aide)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAide)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val listeAide = chargerAideDepuisAssets()
        recyclerView.adapter = AideAdapter(listeAide)
    }

    private fun chargerAideDepuisAssets(): List<AideItem> {
        val inputStream = assets.open("aide_agricole.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val liste = mutableListOf<AideItem>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val titre = obj.getString("titre")
            val solution = obj.getString("solution")
            liste.add(AideItem(titre, solution))
        }

        return liste
    }
}
