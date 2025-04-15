package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Page4Activity extends AppCompatActivity {
    EditText passwordEditText, confirmPasswordEditText;
    Button suivantBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page4);

        passwordEditText = findViewById(R.id.editText);
        confirmPasswordEditText = findViewById(R.id.editText2);
        suivantBtn = findViewById(R.id.button2);

        // Récupérer toutes les données précédentes
        String nom = getIntent().getStringExtra("nom");
        String prenom = getIntent().getStringExtra("prenom");
        String tel = getIntent().getStringExtra("tel");
        String email = getIntent().getStringExtra("email");
        String region = getIntent().getStringExtra("region");
        String ville = getIntent().getStringExtra("ville");
        String zone = getIntent().getStringExtra("zone");


        suivantBtn.setOnClickListener(view -> {
            String password = passwordEditText.getText().toString();
            String confirm = confirmPasswordEditText.getText().toString();

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(nom, prenom, tel, email, password, region, ville, zone);
            sendUserToServer(user);
        });
    }

    private void sendUserToServer(User user) {
        // Utilisation de Volley pour envoyer les données
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.207:3000/addCultivateur"; // Pour l'émulateur Android Studio

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nom", user.getNom());
            jsonBody.put("prenom", user.getPrenom());
            jsonBody.put("tel", user.getTel());
            jsonBody.put("email", user.getEmail());
            jsonBody.put("password", user.getPassword());
            jsonBody.put("region", user.getRegion());
            jsonBody.put("ville", user.getVille());
            jsonBody.put("zone", user.getZone());

            getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("val", 1)
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response ->{ Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Page4Activity.this, accueilActivity.class);
                    startActivity(intent);
                },
                error -> Toast.makeText(this, "Erreur serveur : " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }
}

