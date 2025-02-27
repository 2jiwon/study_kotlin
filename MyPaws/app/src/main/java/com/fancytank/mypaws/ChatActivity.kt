package com.fancytank.mypaws

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fancytank.mypaws.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private val TAG = "CHAT ACTIVITY :: "

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var viewModel: ChatViewModel  // Chat history 관리

    private lateinit var binding: ActivityChatBinding

    private val openAIClient = OpenAIClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "실행됨 ? ")

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recycler_chat)
        adapter = ChatAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // QuestionsActivity에서 전달된 초기 프롬프트 전달
        val initPrompt = intent.getStringExtra("INIT_PROMPT")
        initPrompt?.let {
            viewModel.setInitialPrompt(it)

            // AI 응답 요청
            openAIClient.generateResponse(it, viewModel.chatHistory.value ?: emptyList(), true,
                onSuccess = { response ->
                    runOnUiThread {
                        addAIMessage(response)
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        Log.d("Error :", error)
                    }
                })
        }

        viewModel.chatHistory.observe(this) { chatList ->
            adapter.submitList(chatList.toList())  // 마지막 메시지만 추가
            recyclerView.scrollToPosition(chatList.size - 1)
        }

        binding.btnSend.setOnClickListener {
            val message = binding.editMessage.text.toString()
            if (message.isNotEmpty()) {
                addUserMessage(message)
                binding.editMessage.text.clear()
            }
        }
    }

    // 사용자 메시지 추가
    private fun addUserMessage(text: String) {
        viewModel.addMessage(ChatMessage(text, true, false))

        // AI 응답 요청
        openAIClient.generateResponse(text, viewModel.chatHistory.value ?: emptyList(), false,
            onSuccess = { response ->
                runOnUiThread {
                    addAIMessage(response)
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    Log.d("Error :", error)
                }
            })
    }

    private fun addAIMessage(text: String) {
        viewModel.addMessage(ChatMessage(text, false, false))
    }
}