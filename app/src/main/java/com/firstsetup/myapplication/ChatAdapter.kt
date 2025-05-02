package com.firstsetup.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<Message>,
    private val onOptionClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_AI = 0
        private const val TYPE_OPTION = 1
        private const val TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isOption -> TYPE_OPTION
            message.isUser -> TYPE_USER
            else -> TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_AI -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ai_message, parent, false)
                AiViewHolder(view)
            }
            TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_message, parent, false)
                UserViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_option, parent, false)
                OptionViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is AiViewHolder -> holder.bind(message.text)
            is UserViewHolder -> holder.bind(message.text)
            is OptionViewHolder -> holder.bind(message.text)
        }
    }

    inner class AiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val aiText: TextView = view.findViewById(R.id.textAiMessage)
        fun bind(text: String) {
            aiText.text = text
        }
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userText: TextView = view.findViewById(R.id.textUserMessage)
        fun bind(text: String) {
            userText.text = text
        }
    }

    inner class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val optionButton: Button = view.findViewById(R.id.buttonOption)
        fun bind(text: String) {
            optionButton.text = text
            optionButton.setOnClickListener {
                onOptionClick(text)
            }
        }
    }
}