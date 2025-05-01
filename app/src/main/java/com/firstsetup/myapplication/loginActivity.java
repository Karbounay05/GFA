package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class loginActivity extends AppCompatActivity {
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        Button signupButton = findViewById(R.id.btnSubmit2);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (!emailInput.isEmpty() && !passwordInput.isEmpty()) {
                    User user = new User(emailInput, passwordInput);
                    sendUserToServer(user);
                } else {
                    Toast.makeText(loginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signupButton2 = findViewById(R.id.btnSubmit);
        signupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, Page1Activity.class);
                startActivity(intent);
            }
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
                        int userId = response.getJSONObject("user").getInt("user_id");
                        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                .edit()
                                .putInt("cultivateur_id", userId)
                                .apply();


                        Toast.makeText(loginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(loginActivity.this, Acceuil.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(loginActivity.this, "Erreur parsing réponse", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(loginActivity.this, "Server error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }
}
