package com.firstsetup.myapplication

import Rendement
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CorbeilleActivity : AppCompatActivity(), CorbeilleAdapter.ActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CorbeilleAdapter
    private val rendements = mutableListOf<Rendement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corbeille)

        recyclerView = findViewById(R.id.recyclerViewCorbeille)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val liste = intent.getParcelableArrayListExtra<Rendement>("corbeille")
        rendements.addAll(liste ?: emptyList())

        adapter = CorbeilleAdapter(rendements, this)
        recyclerView.adapter = adapter
    }

    override fun onRestore(rendement: Rendement) {
        Toast.makeText(this, "✅ Restauration non enregistrée (fonction locale)", Toast.LENGTH_SHORT).show()
        rendements.remove(rendement)
        adapter.notifyDataSetChanged()
    }

    override fun onDelete(rendement: Rendement) {
        val url = "https://fluorescent-boiled-butter.glitch.me/api/rendements/${rendement.id}"
        val queue = com.android.volley.toolbox.Volley.newRequestQueue(this)
        val req = com.android.volley.toolbox.StringRequest(
            com.android.volley.Request.Method.DELETE, url,
            { _ ->
                Toast.makeText(this, "🗑️ Supprimé définitivement", Toast.LENGTH_SHORT).show()
                rendements.remove(rendement)
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "❌ Erreur API : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(req)
    }
}
