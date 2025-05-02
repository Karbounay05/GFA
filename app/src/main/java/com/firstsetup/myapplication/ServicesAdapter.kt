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

        holder.slideText1.text = slideData.getOrNull(0) ?: ""
        holder.slideText2.text = slideData.getOrNull(1) ?: ""
        holder.slideText3.text = slideData.getOrNull(2) ?: ""

        val imgIds = listOf(
            R.drawable.ex1, R.drawable.ex3, R.drawable.ex2,
            R.drawable.ex5, R.drawable.ex6, R.drawable.ex4
        )
        val startIndex = position * 3
        holder.button1.background = ContextCompat.getDrawable(context, imgIds[startIndex])
        holder.button2.background = ContextCompat.getDrawable(context, imgIds[startIndex + 1])
        holder.button3.background = ContextCompat.getDrawable(context, imgIds[startIndex + 2])

        // 🔥 Bouton 1 : Gérer la ferme
        holder.button1.setOnClickListener {
            if (position == 0) {
                if (context is Acceuil) {
                    context.verifierFerme()
                }
            }
        }

        // 🔥 Bouton 3 : Assistant AI (2e slide)
        holder.button3.setOnClickListener {
            if (position == 1) {
                if (context is Acceuil) {
                    val aiFragment =
                        context.supportFragmentManager.findFragmentByTag("AI_FRAGMENT") as? AIFragment

                    if (aiFragment != null) {
                        aiFragment.expandPopup()
                    } else {
                        context.hideAccueilButtons()
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
