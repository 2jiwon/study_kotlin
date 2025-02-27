package com.fancytank.mypaws

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageContainer: ConstraintLayout = itemView.findViewById(R.id.messageContainer)

        fun bind(message: ChatMessage) {
            if (message.isInitial) return
            messageText.text = message.text

            val constraintParams = messageContainer.layoutParams as? ConstraintLayout.LayoutParams
            constraintParams?.let {
                if (message.isUserMessage) {
                    it.startToStart = ConstraintLayout.LayoutParams.UNSET
                    it.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                } else {
                    it.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    it.endToEnd = ConstraintLayout.LayoutParams.UNSET
                }
                messageContainer.layoutParams = it
            }

            // 배경 설정
            if (message.isUserMessage) {
                messageText.setBackgroundResource(R.drawable.bg_user_message)
            } else {
                messageText.setBackgroundResource(R.drawable.bg_ai_message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem === newItem // 객체 비교
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem // 데이터 비교
    }
}