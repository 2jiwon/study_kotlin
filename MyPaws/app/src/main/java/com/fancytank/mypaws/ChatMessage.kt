package com.fancytank.mypaws

data class ChatMessage(
    val text: String,
    val isUserMessage: Boolean, // true: 사용자, false: AI
    val isInitial: Boolean
)