package com.example.myquizapp

object Constants {
    // 질문 목록을 받아오는 함수. 이것을 Main에서 사용하면 질문들을 표시할 수 있음
    fun getQuestions(): ArrayList<Question> {
        // 원래는 질문들을 xml 파일 등 리소스에 놓고 거기에서 가져다가 쓰는 것이 정석이나 일단은 하드코딩해서 사용

        val questionsList = ArrayList<Question>()

        val quiz1 = Question(
            1,
            "What country does this flag belong to?",
            R.drawable.ic_flag_of_argentina,
            "Argentina", "Austrailia", "Armenia", "Austria",
            0
        )

        // 질문 목록에 방금 만든 퀴즈 추가
        questionsList.add(quiz1)

        // 질문 목록 반환
        return questionsList
    }
}