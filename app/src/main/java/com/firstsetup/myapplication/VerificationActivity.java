package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {

    EditText codeInput;
    Button verifyBtn;
    String email; // récupéré via Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifcation);

        codeInput = findViewById(R.id.codeInput);
        verifyBtn = findViewById(R.id.verifyBtn);

        email = getIntent().getStringExtra("email");

        verifyBtn.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Saisis le code", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(email, code);
            }
        });
    }

    private void verifyCode(String email, String code) {
        String url = "https://fluorescent-boiled-butter.glitch.me/verifyEmail";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("verificationCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(this, "Email vérifié avec succès 🎉", Toast.LENGTH_SHORT).show();
                    // redirige vers la page principale
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Code invalide ❌", Toast.LENGTH_SHORT).show();
                    Log.e("VERIFY_ERROR", error.toString());
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
