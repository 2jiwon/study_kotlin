package com.example.newquizapp

data class Question(
    val id: Int,             // 질문 구분을 위한 id
    val question: String,    // 질문
    val image: Int,          // 리소스 이미지를 int로 관리
    val optionOne: String,   // 옵션1
    val optionTwo: String,   // 옵션2
    val optionThree: String, // 옵션3
    val optionFour: String,  // 옵션4
    val correctAnswer: Int   // 정답 index
)
