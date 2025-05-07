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
import com.firstsetup.myapplication.server.PingService;


public class LogoActivity extends AppCompatActivity {

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

        // ** Wake up Glitch server as soon as the app launches**
        ServerPing serverPing = new ServerPing();
        serverPing.pingServer(this);

        // **Wait for 5 seconds and go to MainActivity**
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                int val = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        .getInt("val", 0);
                if(val==0){
                Intent intent = new Intent(LogoActivity.this, IntroductionActivity.class);
                startActivity(intent);
                finish(); }
                else if(val == 1){

                    Intent intent = new Intent(LogoActivity.this, Acceuil.class);
                    startActivity(intent);
                    finish();
                }else if(val == 2){
                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{}
            }
        }, 5300);

    }
}
