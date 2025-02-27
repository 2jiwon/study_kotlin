package com.fancytank.mypaws

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel() {
    private val _chatHistory = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val chatHistory: LiveData<MutableList<ChatMessage>> = _chatHistory

    private var hasSentInitPrompt = false // 중복 호출 방지

    fun setInitialPrompt(prompt: String) {
        if (!hasSentInitPrompt) {
            hasSentInitPrompt = true
            _chatHistory.value = mutableListOf(ChatMessage(prompt, false, true))
        }

    }

    fun addMessage(message: ChatMessage) {
        _chatHistory.value = (_chatHistory.value ?: mutableListOf()).apply {
            add(message)
        }
    }


}