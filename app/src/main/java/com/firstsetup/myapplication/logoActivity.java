package com.firstsetup.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class logoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.logo_activity); // Intro layout

        // Apply animations
        TextView textView = findViewById(R.id.textanim);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation moveUp = AnimationUtils.loadAnimation(this, R.anim.move_up);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(moveUp);
        animationSet.setDuration(2000);
        textView.startAnimation(animationSet);

        // Second text animation
        TextView textView2 = findViewById(R.id.textanim2);
        Animation fadeIn2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation moveUp2 = AnimationUtils.loadAnimation(this, R.anim.move_up);

        AnimationSet animationSet2 = new AnimationSet(true);
        animationSet2.addAnimation(fadeIn2);
        animationSet2.addAnimation(moveUp2);
        animationSet2.setDuration(2000);
        textView2.startAnimation(animationSet2);

        // **Wait for 5 seconds and go to MainActivity**
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(logoActivity.this, introductionActivity.class);
                startActivity(intent);
                finish(); // Close IntroActivity to prevent going back
            }
        }, 5300); // 8 seconds delay
    }
}
