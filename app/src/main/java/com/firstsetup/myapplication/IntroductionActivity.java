package com.firstsetup.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class IntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerPing serverPing = new ServerPing();
        serverPing.pingServer(this);

        // Initialize the ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPagerDone);

        // Create a list of SlideItems (text + image)
        List<SlideAdapter.SlideItem> slides = new ArrayList<>();
        slides.add(new SlideAdapter.SlideItem("Découvrez les services d'GFA pour une agriculture prospère et durable","Bienvenue",R.drawable.rounded_image_bg, R.drawable.img1, 0));
        slides.add(new SlideAdapter.SlideItem("Permettre à l’utilisateur de planifier, suivre et optimiser toutes les activités de sa ferme,incluant la gestion des cultures, du bétail, des ressources, et des ventes, depuis une seule interface intuitive.","Gérer votre ferme",R.drawable.rounded_image_bg, R.drawable.img2,0));
        slides.add(new SlideAdapter.SlideItem("Obtenez de l'assistance continue et des recommandation adaptées au cycle et à l'évolution de votre culture et à l'environnement associé","Suivre la parcelle",R.drawable.rounded_image_bg, R.drawable.img3,0));
        slides.add(new SlideAdapter.SlideItem("Consulter les cotations à jour des fruits, légumes et céréales sur les marchés de gros les plus dynamiques","Infos prix",R.drawable.rounded_image_bg, R.drawable.img4,0));
        slides.add(new SlideAdapter.SlideItem("Dispozer des données et des prévisions Météo les plus précises","Méteo",R.drawable.rounded_image_bg, R.drawable.img5,0));
        slides.add(new SlideAdapter.SlideItem("accélerer à notre map pour calculer la surface de superficier de votre ferme","Calculer la superficier",R.drawable.rounded_image_bg, R.drawable.img6,0));
        slides.add(new SlideAdapter.SlideItem("Nous vous souhaitons une bonne expérience. N'hésitez pas à commencer la gestion de votre ferme.","s'inscrire chez nous",R.drawable.rounded_image_bg, R.drawable.img7,0));

        // Create the adapter with the SlideItem list
        SlideAdapter adapter = new SlideAdapter(slides, viewPager);

        // Set the adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }
}
