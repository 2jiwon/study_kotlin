package com.example.myquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class QuizQuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        // 질문들 가져오기
        val questionsList = Constants.getQuestions()

        // 질문들의 size 체크
        Log.i("QuestionsList size is ", "${questionsList.size}")
    }
}