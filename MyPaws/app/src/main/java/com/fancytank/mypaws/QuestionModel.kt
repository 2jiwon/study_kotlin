package com.fancytank.mypaws

data class Question(
    val text: String, // 질문 텍스트
    val options: List<Option> // 보기 목록
)

data class Option(
    val text: String, // 보기 텍스트
    val nextQuestion: Question? = null // 선택 시 다음 질문 (없으면 null)
)

object QuestionData { // 질문 데이터를 관리하는 객체
    // 초기화 전에 참조 가능하도록 lateinit 사용
    lateinit var petTypeQuestion: Question
    lateinit var dogBreedsQuestion: Question
    lateinit var catBreedsQuestion: Question

    init {

        // 강아지 종류 질문
        dogBreedsQuestion = Question(
            text = "강아지 종류를 선택해주세요.",
            options = listOf(
                Option("말티즈", null),
                Option("포메라니안", null),
                Option("푸들", null),
                Option("시츄", null),
                Option("치와와", null),
                Option("래프라도 리트리버", null),
                Option("불독", null),
                Option("스피츠", null),
                Option("비숑 프리제", null),
                Option("시베리안 허스키", null),
                Option("도베르만", null),
                Option("차우차우", null),
            )
        )

        // 고양이 종류 질문
        catBreedsQuestion = Question(
            text = "고양이 종류를 선택해주세요.",
            options = listOf(
                Option("페르시안", null),
                Option("페르시안 친칠라", null),
                Option("러시안 블루", null),
                Option("샴", null),
                Option("스코티시 폴드", null),
                Option("노르웨이 숲", null),
                Option("뱅갈", null),
                Option("먼치킨", null),
                Option("아메리칸 숏헤어", null),
                Option("코리안 숏헤어", null),
                Option("터키시 앙고라", null),
                Option("랙돌", null),
                Option("스핑크스", null),
            )
        )

        // 첫번째 질문: 반려동물 유형 선택
        petTypeQuestion = Question(
            text = "내새꾸는 어떤 동물인가요?",
            options = listOf(
                Option("강아지", nextQuestion = dogBreedsQuestion),
                Option("고양이", nextQuestion = catBreedsQuestion),
                Option("햄스터", null),
                Option("앵무새", null),
            )
        )
    }


}