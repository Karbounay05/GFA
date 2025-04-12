package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Start IntroActivity and finish MainActivity
        Intent intent = new Intent(MainActivity.this, logoActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity to prevent looping back
    }
}
