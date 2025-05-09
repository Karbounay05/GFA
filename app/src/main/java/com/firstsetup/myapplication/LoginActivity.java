package com.firstsetup.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firstsetup.myapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    private CardView pingCard;
    private TextView pingText;
    private ProgressBar pingProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);
        pingCard = findViewById(R.id.pingCard);
        pingText = findViewById(R.id.pingText);
        pingProgress = findViewById(R.id.pingProgress);

// Show card and ping server
        ServerPing serverPing = new ServerPing();
        serverPing.pingServer(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        TextView forgotPassword = findViewById(R.id.textOublierMotDePasse);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();

                if (emailInput.isEmpty()) {
                    pingCard.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> pingCard.setVisibility(View.GONE), 10000);
                    Toast.makeText(LoginActivity.this, "Entrez votre e-mail d’abord", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject body = new JSONObject();
                try {
                    body.put("email", emailInput);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        "https://fluorescent-boiled-butter.glitch.me/demanderResetMotDePasse",
                        body,
                        response -> {
                            Toast.makeText(LoginActivity.this, "📧 Vérifie ton e-mail !", Toast.LENGTH_LONG).show();
                        },
                        error -> {
                            Toast.makeText(LoginActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );

                Volley.newRequestQueue(LoginActivity.this).add(request);
            }
        });
        // Bouton de connexion
        Button loginButton = findViewById(R.id.btnSubmit2);
        loginButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (!emailInput.isEmpty() && !passwordInput.isEmpty()) {
                User user = new User(emailInput, passwordInput);
                sendUserToServer(user);
            } else {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton d'inscription
        Button signupButton = findViewById(R.id.btnSubmit);
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Page1Activity.class);
            startActivity(intent);
        });
    }

    private void sendUserToServer(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fluorescent-boiled-butter.glitch.me/loginCultivateur";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", user.getEmail());
            jsonBody.put("password", user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        // Récupération du token JWT et de l'ID utilisateur
                        String token = response.getString("token");
                        int userId = response.getInt("user_id");

                        // Sauvegarde dans SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("cultivateur_id", userId);
                        editor.putString("jwt_token", token); // 🔐 Enregistrement du JWT
                        editor.apply();

                        Log.d("LOGIN", "Token JWT: " + token);
                        Log.d("LOGIN", "ID sauvegardé = " + userId);

                        Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Acceuil.class));
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erreur lors du traitement de la réponse", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(LoginActivity.this, "Erreur serveur : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LOGIN", "Erreur: ", error);
                });

        queue.add(request);
    }
}
