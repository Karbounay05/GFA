package com.firstsetup.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class introductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPagerDone);

        // Create a list of SlideItems (text + image)
        List<slideAdapter.SlideItem> slides = new ArrayList<>();
        slides.add(new slideAdapter.SlideItem("Slide 1","Bienvenue",R.drawable.rounded_image_bg, R.drawable.img1, 0));
        slides.add(new slideAdapter.SlideItem("Slide 2","Gérer vvotre ferme",R.drawable.rounded_image_bg, R.drawable.img2,0));
        slides.add(new slideAdapter.SlideItem("Slide 3","Suivre la parcelle",R.drawable.rounded_image_bg, R.drawable.img3,0));
        slides.add(new slideAdapter.SlideItem("Slide 4","Infos prix",R.drawable.rounded_image_bg, R.drawable.img4,0));
        slides.add(new slideAdapter.SlideItem("Slide 5","Méteo",R.drawable.rounded_image_bg, R.drawable.img5,0));
        slides.add(new slideAdapter.SlideItem("Slide 5","Calculer la superficier",R.drawable.rounded_image_bg, R.drawable.img5,0));
        slides.add(new slideAdapter.SlideItem("Slide 5","",R.drawable.rounded_image_bg, 0,0));

        // Create the adapter with the SlideItem list
        slideAdapter adapter = new slideAdapter(slides, viewPager);

        // Set the adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }
}
