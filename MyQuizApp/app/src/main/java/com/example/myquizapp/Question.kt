package com.example.myquizapp

data class Question(
    val id: Int,                // 질문 구분을 위한 id
    val question: String,       // 질문
    val image: Int,             // 리소스 안에 있는 이미지를 int 속성으로 관리
    val optionOne: String,      // 선택할 옵션1
    val optionTwo: String,      // 선택할 옵션2
    val optionThree: String,    // 선택할 옵션3
    val optionFour: String,     // 선택할 옵션4
    val correctAnswer: Int      // 정답의 index
)
