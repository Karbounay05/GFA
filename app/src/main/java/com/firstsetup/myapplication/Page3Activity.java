package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Page3Activity extends AppCompatActivity {
    EditText regionEditText, villeEditText, zoneEditText;
    Button suivantBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3);

        regionEditText = findViewById(R.id.editText);
        villeEditText = findViewById(R.id.editText2);
        zoneEditText = findViewById(R.id.editText3);
        suivantBtn = findViewById(R.id.button2);

        String nom = getIntent().getStringExtra("nom");
        String prenom = getIntent().getStringExtra("prenom");
        String tel = getIntent().getStringExtra("tel");
        String email = getIntent().getStringExtra("email");

        suivantBtn.setOnClickListener(view -> {
            String region = regionEditText.getText().toString();
            String ville = villeEditText.getText().toString();
            String zone = zoneEditText.getText().toString();

            Intent intent = new Intent(Page3Activity.this, Page4Activity.class);
            intent.putExtra("nom", nom);
            intent.putExtra("prenom", prenom);
            intent.putExtra("tel", tel);
            intent.putExtra("email", email);
            intent.putExtra("region", region);
            intent.putExtra("ville", ville);
            intent.putExtra("zone", zone);
            startActivity(intent);
        });
    }
}


