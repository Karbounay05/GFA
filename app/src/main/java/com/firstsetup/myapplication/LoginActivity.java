package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private CardView pingCard;
    private TextView pingText;
    private ProgressBar pingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        // 🟩 Set up ping card
        pingCard = findViewById(R.id.pingCard);
        pingText = findViewById(R.id.pingText);
        pingProgress = findViewById(R.id.pingProgress);

// Show card and ping server
        ServerPing serverPing = new ServerPing();
        serverPing.pingServer(this);

// Hide card after 10 seconds
        new Handler().postDelayed(() -> pingCard.setVisibility(View.GONE), 10000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.btnSubmit2);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (!emailInput.isEmpty() && !passwordInput.isEmpty()) {
                    pingCard.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> pingCard.setVisibility(View.GONE), 10000);
                    User user = new User(emailInput, passwordInput);
                    sendUserToServer(user);
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signupButton = findViewById(R.id.btnSubmit);
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Page1Activity.class);
            startActivity(intent);
        });
    }

    // 🔐 Function to send login credentials to Glitch server
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
                        int userId = response.getJSONObject("user").getInt("user_id");
                        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                .edit()
                                .putInt("cultivateur_id", userId)
                                .apply();

                        Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, AcceuilActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erreur parsing réponse", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = (error.getMessage() != null) ? error.getMessage() : "Unknown error";
                    Toast.makeText(LoginActivity.this, "Server error: " + msg, Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }
}
