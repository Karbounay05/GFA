package com.firstsetup.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);

        List<String> slides = Arrays.asList("Slide 1", "Slide 2", "Slide 3");
        SlideAdapter adapter = new SlideAdapter(slides);
        viewPager.setAdapter(adapter);
    }
}
