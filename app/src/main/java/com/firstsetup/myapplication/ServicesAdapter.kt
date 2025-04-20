package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServicesAdapter(private val slideTexts: List<String>) :
    RecyclerView.Adapter<ServicesAdapter.SlideViewHolder>() {

    class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.slideText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.services_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.textView.text = slideTexts[position]
    }

    override fun getItemCount(): Int = slideTexts.size
}
