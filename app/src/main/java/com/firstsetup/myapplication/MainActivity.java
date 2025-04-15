package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView weatherText;
    private static final String API_KEY = "132b349bd472e322f99891150708a288";  // Remplace par ta clé API
    private static final String CITY_NAME = "Rabat";       // Ville pour la météo
    private static final String URL = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY_NAME + "&appid=" + API_KEY + "&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acceuil_activity);

        weatherText = findViewById(R.id.weatherText); // TextView pour afficher la météo

        // Crée une requête Volley pour obtenir les données météo
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extraction des données depuis le JSON
                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp");
                            String weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description");

                            // Affichage des informations météo dans un TextView
                            weatherText.setText("Température: " + temperature + "°C\nDescription: " + weatherDescription);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Erreur de données météo", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });

        // Ajoute la requête à la queue Volley
        queue.add(jsonObjectRequest);

        // Start IntroActivity and finish MainActivity
        Intent intent = new Intent(MainActivity.this, logoActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity to prevent looping back
    }
}
