package com.firstsetup.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class GererFermeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gerer_ferme)

        val moveUp = AnimationUtils.loadAnimation(this, R.anim.move_up2)

        val frame2 = findViewById<FrameLayout>(R.id.frameLayout2)
        val frame3 = findViewById<FrameLayout>(R.id.frameLayout3)
        val frame4 = findViewById<FrameLayout>(R.id.frameLayout4)

        // Animate first frame
        frame2.startAnimation(moveUp)

        moveUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Show and animate frame3 after frame2 animation ends
                frame3.visibility = FrameLayout.VISIBLE
                val moveUp2 = AnimationUtils.loadAnimation(applicationContext, R.anim.move_up2)

                frame3.startAnimation(moveUp2)

                moveUp2.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        // Show and animate frame4 after frame3 animation ends
                        frame4.visibility = FrameLayout.VISIBLE
                        val moveUp3 = AnimationUtils.loadAnimation(applicationContext, R.anim.move_up2)
                        frame4.startAnimation(moveUp3)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        val button = findViewById<Button>(R.id.buttonFrame1);
        button.setOnClickListener {
        val intent= Intent(this, AjouterFermeActivity::class.java)
        startActivity(intent)
        }
    }
}