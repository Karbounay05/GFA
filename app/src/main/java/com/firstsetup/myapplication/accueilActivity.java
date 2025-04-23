package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class accueilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acceuil_activity);

        Button signupButton = findViewById(R.id.buttonlogout);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        .edit()
                        .putInt("val", 2)
                        .apply();
                Intent intent = new Intent(accueilActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }
}
