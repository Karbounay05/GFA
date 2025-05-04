package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Page1Activity extends AppCompatActivity {
    EditText nomEditText, prenomEditText;
    Button suivantBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1);

        nomEditText = findViewById(R.id.editText);
        prenomEditText = findViewById(R.id.editText2);
        suivantBtn = findViewById(R.id.button2);

        suivantBtn.setOnClickListener(view -> {
            String nom = nomEditText.getText().toString();
            String prenom = prenomEditText.getText().toString();

            Intent intent = new Intent(Page1Activity.this, Page2Activity.class);
            intent.putExtra("nom", nom);
            intent.putExtra("prenom", prenom);
            startActivity(intent);
        });
    }
}





