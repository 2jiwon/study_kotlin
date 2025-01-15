package com.fancytank.mypaws

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class OpenAIClient {
    private val client = OkHttpClient()
    private val apiKey =  BuildConfig.OPENAI_API_KEY
    private val endPoint = "https://api.openai.com/v1/chat/completions"

    fun generateResponse(prompt: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val mediaType = "application/json".toMediaType()
        val json = JSONObject()
        json.put("model", "gpt-4o-mini")
        json.put("messages", JSONArray().put(JSONObject().apply {
            put("role", "system")
            put("content", JSONArray().put(JSONObject().apply {
                put("type", "text")
                put("text", prompt)
            }))
        }))
//      json.put("response_format", mapOf("type" to "text"))

        Log.d("body : ", json.toString())

        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(endPoint)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "API 요청 실패")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val requestBody = response.body?.string()
                    val content = JSONObject(requestBody).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")
                    onSuccess(content)
                    Log.d("content", content)
                } else {
                    onError("API 응답 오류: ${response}")
                }
            }
        })
    }
}