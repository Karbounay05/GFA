package com.firstsetup.myapplication
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firstsetup.myapplication.model.AideItem
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.firstsetup.myapplication.R

class AideAdapter(private val listeAide: List<AideItem>) :
    RecyclerView.Adapter<AideAdapter.AideViewHolder>() {

    inner class AideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titre: TextView = itemView.findViewById(R.id.textTitre)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aide, parent, false)
        return AideViewHolder(view)
    }

    override fun onBindViewHolder(holder: AideViewHolder, position: Int) {
        val item = listeAide[position]
        holder.titre.text = item.titre

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(item.titre)
                .setMessage(item.solution)
                .setPositiveButton("Fermer", null)
                .show()
        }
    }

    override fun getItemCount() = listeAide.size
}
