package com.example.myquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class QuizQuestionsActivity : AppCompatActivity() {

    // 진행바
    private var progressBar : ProgressBar? = null
    // 진행바 옆의 0/9 텍스트
    private var tvProgress : TextView? = null
    // 질문
    private var tvQuestion : TextView? = null
    // 이미지
    private var ivImage : ImageView? = null
    // 선택 옵션들
    private var tvOptionOne : TextView? = null
    private var tvOptionTwo : TextView? = null
    private var tvOptionThree : TextView? = null
    private var tvOptionFour : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        // UI와 연결
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)


        // 질문들 가져오기
        val questionsList = Constants.getQuestions()

        // 질문들의 size 체크
        Log.i("QuestionsList size is ", "${questionsList.size}")

        // 질문들을 미리 로그로 확인해보기
        for (i in questionsList) {
            // Log.e로 출력하면 빨갛게 표시된다.
            Log.e("Questions", i.question)
        }

        // 현재 위치를 이동하면서 질문을 가져올 수 있도록 (index 0 대신 1을 쓰는 이유는 텍스트 표시를 쉽게 하기 위함임)
        var currentPosition = 1
        var question : Question = questionsList[currentPosition - 1]
        // 진행상태 나타내는 바
        progressBar?.progress = currentPosition
        // 옆의 텍스트
        tvProgress?.text = "$currentPosition/${progressBar?.max}"
        // 질문 자체
        tvQuestion?.text = question.question
        // 질문에 속한 이미지
        ivImage?.setImageResource(question.image)
        // 선택지 옵션들
        tvOptionOne?.text = question.optionOne
        tvOptionTwo?.text = question.optionTwo
        tvOptionThree?.text = question.optionThree
        tvOptionFour?.text = question.optionFour

    }
}