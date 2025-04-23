// AjouterFermeActivity.java
package com.firstsetup.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AjouterFermeActivity extends AppCompatActivity {

    EditText editNom, editSuperficie, editLocalisation, editTypeSol;
    Button btnAjouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_ferme);

        editNom = findViewById(R.id.editNomF);
        editSuperficie = findViewById(R.id.editSuperficie);
        editLocalisation = findViewById(R.id.editLocalisation);
        editTypeSol = findViewById(R.id.editTypeSol);
        btnAjouter = findViewById(R.id.btnAjouterFerme);

        btnAjouter.setOnClickListener(v -> ajouterFerme());
    }

    private void ajouterFerme() {
        String nom = editNom.getText().toString();
        String superficie = editSuperficie.getText().toString();
        String localisation = editLocalisation.getText().toString();
        String typeSol = editTypeSol.getText().toString();

        if (nom.isEmpty() || superficie.isEmpty() || localisation.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            int cultivateurId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("cultivateur_id", -1);

            if (cultivateurId == -1) {
                Toast.makeText(this, "Erreur : identifiant cultivateur manquant ❌", Toast.LENGTH_LONG).show();
                return;
            }

            body.put("nom", nom);
            body.put("superficie", Double.parseDouble(superficie));
            body.put("localisation", localisation);
            body.put("type_sol", typeSol);
            body.put("cultivateur_id", cultivateurId); // ✅ utilisé dynamiquement
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://fluorescent-boiled-butter.glitch.me/fermes";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    Toast.makeText(this, "Ferme ajoutée ✅", Toast.LENGTH_SHORT).show();
                    finish(); // ferme l’activité
                },
                error -> {
                    Toast.makeText(this, "Erreur : " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

}
