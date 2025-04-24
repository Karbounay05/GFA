// AjouterFermeActivity.java
package com.firstsetup.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AjouterFermeActivity extends AppCompatActivity {

    EditText editNom, editSuperficie, editLocalisation;
    Spinner spinnerTypeSol;
    Button btnAjouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_ferme);

        editNom = findViewById(R.id.editNomF);
        editSuperficie = findViewById(R.id.editSuperficie);
        editLocalisation = findViewById(R.id.editLocalisation);
        spinnerTypeSol = findViewById(R.id.spinnerTypeSol);
        btnAjouter = findViewById(R.id.btnAjouterFerme);

        btnAjouter.setOnClickListener(v -> ajouterFerme());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_de_sol, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeSol.setAdapter(adapter);
    }


    private void ajouterFerme() {
        String nom = editNom.getText().toString();
        String superficie = editSuperficie.getText().toString();
        String localisation = editLocalisation.getText().toString();
        String typeSol = spinnerTypeSol.getSelectedItem().toString();

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
