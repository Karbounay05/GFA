package com.firstsetup.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Page2Activity extends AppCompatActivity {
    EditText telEditText, emailEditText;
    Button suivantBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2);

        telEditText = findViewById(R.id.editText);
        emailEditText = findViewById(R.id.editText2);
        suivantBtn = findViewById(R.id.button2);

        String nom = getIntent().getStringExtra("nom");
        String prenom = getIntent().getStringExtra("prenom");

        suivantBtn.setOnClickListener(view -> {
            String tel = telEditText.getText().toString();
            String email = emailEditText.getText().toString();

            Intent intent = new Intent(Page2Activity.this, Page3Activity.class);
            intent.putExtra("nom", nom);
            intent.putExtra("prenom", prenom);
            intent.putExtra("tel", tel);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}


