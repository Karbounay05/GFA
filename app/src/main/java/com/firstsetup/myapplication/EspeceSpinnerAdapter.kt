package com.firstsetup.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class EspeceSpinnerAdapter(
    context: Context,
    private val especes: List<String>,
    private val images: List<Int>
) : ArrayAdapter<String>(context, 0, especes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        val imageEspece = view.findViewById<ImageView>(R.id.imageEspece)
        val textEspece = view.findViewById<TextView>(R.id.textEspece)

        imageEspece.setImageResource(images[position])
        textEspece.text = especes[position]

        return view
    }
}
