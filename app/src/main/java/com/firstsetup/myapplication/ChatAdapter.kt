package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firstsetup.myapplication.model.Message

class ChatAdapter(
    private val messages: MutableList<Message>,
    private val onOptionClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_AI = 1
        private const val TYPE_OPTION = 2
    }

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]
        return when {
            msg.isOption -> TYPE_OPTION
            msg.isUser -> TYPE_USER
            else -> TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_message, parent, false)
                UserViewHolder(view)
            }
            TYPE_OPTION -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option_message, parent, false)
                OptionViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ai_message, parent, false)
                AiViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (holder) {
            is UserViewHolder -> holder.bind(msg.text)
            is AiViewHolder -> holder.bind(msg.text)
            is OptionViewHolder -> holder.bind(msg.text, onOptionClick)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.textUserMessage)
        fun bind(msg: String) {
            text.text = msg
        }
    }


    class AiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.textAiMessage)
        fun bind(msg: String) {
            text.text = msg
        }
    }


    class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btn: Button = itemView.findViewById(R.id.btnOption)
        fun bind(option: String, clickListener: (String) -> Unit) {
            btn.text = option
            btn.setOnClickListener {
                clickListener(option)
            }
        }
    }
}
