package com.firstsetup.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ServicesAdapter(private val slideTexts: List<List<String>>) :
    RecyclerView.Adapter<ServicesAdapter.SlideViewHolder>() {

    class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slideText1: TextView = itemView.findViewById(R.id.slideText)
        val slideText2: TextView = itemView.findViewById(R.id.slideText2)
        val slideText3: TextView = itemView.findViewById(R.id.slideText3)

        val button1: Button = itemView.findViewById(R.id.button)
        val button2: Button = itemView.findViewById(R.id.button3)
        val button3: Button = itemView.findViewById(R.id.button4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.services_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slideData = slideTexts[position]
        val context = holder.itemView.context

        // Set texts (safe access)
        holder.slideText1.text = slideData.getOrNull(0) ?: ""
        holder.slideText2.text = slideData.getOrNull(1) ?: ""
        holder.slideText3.text = slideData.getOrNull(2) ?: ""

        // Button backgrounds for 2 slides (3 images each)
        val imgIds = listOf(
            R.drawable.ex1, R.drawable.ex3, R.drawable.ex2, // Slide 1;;:
            R.drawable.ex5, R.drawable.ex6, R.drawable.ex4  // Slide 2
        )

        val startIndex = position * 3
        holder.button1.background = ContextCompat.getDrawable(context, imgIds[startIndex])
        holder.button2.background = ContextCompat.getDrawable(context, imgIds[startIndex + 1])
        holder.button3.background = ContextCompat.getDrawable(context, imgIds[startIndex + 2])

        holder.button1.setOnClickListener {
            if (position == 0) { // Only for first slide
                val intent = Intent(context, GererFermeActivity::class.java)
                context.startActivity(intent)
            }else if (position == 1) {
                val intent = Intent(context, RendementActivity::class.java)
                context.startActivity(intent)
            }
        }
        holder.button2.setOnClickListener {
            if (position == 0) { // Only for first slide
                val intent = Intent(context, SuivreParcelleActivity::class.java)
                context.startActivity(intent)
            }else if (position == 1) {
                val intent = Intent(context, MapActivity::class.java)
                context.startActivity(intent)
            }
        }
        holder.button3.setOnClickListener {
            if (position == 0) {

            }else if (position == 1) {
                if (context is Acceuil) {
                    val aiFragment =
                        context.supportFragmentManager.findFragmentByTag("AI_FRAGMENT") as? AIFragment

                    if (aiFragment != null) {
                        aiFragment.expandPopup()
                    } else {
                        context.supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_bottom,
                                R.anim.fade_out,
                                R.anim.fade_in2,
                                R.anim.slide_out_bottom
                            )
                            .replace(R.id.content_frame, AIFragment(), "AI_FRAGMENT")
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int = slideTexts.size
}
