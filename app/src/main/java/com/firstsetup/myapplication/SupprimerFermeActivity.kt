package com.firstsetup.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SupprimerFermeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supprimer_ferme)

        val fermeId = intent.getIntExtra("ferme_id", -1)
        val btnSupprimer = findViewById<Button>(R.id.btnSupprimer)

        btnSupprimer.setOnClickListener {
            if (fermeId != -1) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Confirmation")
                alertDialog.setMessage("Voulez-vous vraiment supprimer cette ferme ?")
                alertDialog.setPositiveButton("Oui") { _, _ -> supprimerFerme(fermeId) }
                alertDialog.setNegativeButton("Non") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            } else {
                Toast.makeText(this, "ID de la ferme invalide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun supprimerFerme(id: Int) {
        val url = "https://fluorescent-boiled-butter.glitch.me/fermes/$id"

        val request = StringRequest(Request.Method.DELETE, url,
            {
                Toast.makeText(this, "Ferme supprimée avec succès ✅", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FermeListActivity::class.java)
                startActivity(intent)
                finish()
            },
            { error ->
                Toast.makeText(this, "Erreur de suppression: ${error.message}", Toast.LENGTH_LONG).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

}
