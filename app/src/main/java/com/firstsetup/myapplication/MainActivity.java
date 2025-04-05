package com.firstsetup.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.firstsetup.myapplication.User;
import com.firstsetup.myapplication.ApiServer.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    // Déclarations des champs de saisie
    private EditText  editTextEmail, editTextPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);


        // L'action lorsqu'on clique sur le bouton "Soumettre"
        btnSubmit.setOnClickListener(v -> {

            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer un objet User avec les données saisies
            User user = new User(email, password);

            // Initialiser Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost:3000") // Adresse de ton serveur Node.js
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Créer l'instance de l'API
            ApiService apiService = retrofit.create(ApiService.class);

            // Créer la requête POST
            Call<Void> call = apiService.registerUser(user);

            // Exécuter la requête
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Si l'inscription réussit
                        Toast.makeText(MainActivity.this, "Utilisateur enregistré avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si l'inscription échoue
                        Toast.makeText(MainActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Si une erreur réseau se produit
                    Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
