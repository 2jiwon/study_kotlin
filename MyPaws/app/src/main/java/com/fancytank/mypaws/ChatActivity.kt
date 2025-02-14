package com.fancytank.mypaws

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fancytank.mypaws.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>() // 메시지 리스트

    private lateinit var binding: ActivityChatBinding

    private val openAIClient = OpenAIClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recycler_chat)
        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 테스트용 사용자 메시지
//        addUserMessage("안녕?")

        // QuestionsActivity에서 전달된 AI 응답 표시
        val initResponse = intent.getStringExtra("AI_RESPONSE")
        initResponse?.let {
            addAIMessage(it)
        }

        val message = binding.editMessage.text.toString()
        binding.btnSend.setOnClickListener {
            Log.d("버튼 눌림?? ", message)
            if (message.isNotEmpty()) {
                addUserMessage(message)
            }
        }

    }

    // 사용자 메시지 추가
    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        // AI 응답 요청
        openAIClient.generateResponse(text,
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
        messages.add(ChatMessage(text, false))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }
}