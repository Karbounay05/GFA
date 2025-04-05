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
        slides.add(new slideAdapter.SlideItem("Slide 1",R.drawable.rounded_image_bg, R.drawable.img1));
        slides.add(new slideAdapter.SlideItem("Slide 2",R.drawable.rounded_image_bg, R.drawable.img2));
        slides.add(new slideAdapter.SlideItem("Slide 3",R.drawable.rounded_image_bg, R.drawable.img3));
        slides.add(new slideAdapter.SlideItem("Slide 4",R.drawable.rounded_image_bg, R.drawable.img4));
        slides.add(new slideAdapter.SlideItem("Slide 5",R.drawable.rounded_image_bg, R.drawable.img5));
        slides.add(new slideAdapter.SlideItem("Slide 5",R.drawable.rounded_image_bg, 0));

        // Create the adapter with the SlideItem list
        slideAdapter adapter = new slideAdapter(slides);

        // Set the adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }
}
