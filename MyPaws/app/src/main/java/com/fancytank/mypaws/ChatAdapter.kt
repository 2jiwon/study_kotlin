package com.fancytank.mypaws

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageContainer: ConstraintLayout = itemView.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        val constraintParams = holder.messageContainer.layoutParams as? ConstraintLayout.LayoutParams
        if (constraintParams != null) {

            if (message.isUserMessage) {
                constraintParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                constraintParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            } else {
                constraintParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                constraintParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            }

            holder.messageContainer.layoutParams = constraintParams
        }

        holder.messageText.text = message.text

        // 배경 설정
        if (message.isUserMessage) {
            holder.messageText.setBackgroundResource(R.drawable.bg_user_message)
        } else {
            holder.messageText.setBackgroundResource(R.drawable.bg_ai_message)
        }
    }

    override fun getItemCount() : Int = messages.size
}