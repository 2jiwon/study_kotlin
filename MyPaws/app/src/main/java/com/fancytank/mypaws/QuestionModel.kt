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

    lateinit var bodyColorQuestion: Question
    lateinit var eyesColorQuestion: Question


    init {
        // 눈 색상 질문
        eyesColorQuestion = Question(
            text = "눈동자는 어떤 색인가요?",
            options = listOf(
                Option("흰색", null),
                Option("검정", null),
                Option("갈색", null),
                Option("회색", null),
                Option("밝은 갈색", null),
                Option("어두운 갈색", null),
                Option("노란색", null),
                Option("붉은색", null),
                Option("초록색", null),
                Option("하늘색", null),
            )
        )

        // 바디 색상 질문
        bodyColorQuestion = Question(
            text = "내새꾸는 어떤 색인가요?",
            options = listOf(
                Option("흰색", eyesColorQuestion),
                Option("검정", eyesColorQuestion),
                Option("갈색", eyesColorQuestion),
                Option("회색", eyesColorQuestion),
                Option("밝은 갈색", eyesColorQuestion),
                Option("어두운 갈색", eyesColorQuestion),
                Option("주로 검정 + 흰색 섞임", eyesColorQuestion),
                Option("주로 흰색 + 검정 섞임", eyesColorQuestion),
                Option("주로 검정 + 갈색 섞임", eyesColorQuestion),
                Option("주로 흰색 + 갈색 섞임", eyesColorQuestion),
                Option("흰 바탕에 검정 얼룩 무늬", eyesColorQuestion),
                Option("검정 바탕에 흰색 얼룩 무늬", eyesColorQuestion),
                Option("호랑이 무늬", eyesColorQuestion),
                Option("주로 흰색 + 눈 주위만 검정", eyesColorQuestion),
                Option("주로 흰색 + 눈 주위만 갈색", eyesColorQuestion),
                Option("주로 흰색 + 귀 부분만 검정", eyesColorQuestion),
                Option("주로 흰색 + 귀 부분만 갈색", eyesColorQuestion),
            )
        )

        // 강아지 종류 질문
        dogBreedsQuestion = Question(
            text = "강아지 종류를 선택해주세요.",
            options = listOf(
                Option("말티즈", bodyColorQuestion),
                Option("포메라니안", bodyColorQuestion),
                Option("푸들", bodyColorQuestion),
                Option("시츄", bodyColorQuestion),
                Option("치와와", bodyColorQuestion),
                Option("래프라도 리트리버", bodyColorQuestion),
                Option("불독", bodyColorQuestion),
                Option("스피츠", bodyColorQuestion),
                Option("비숑 프리제", bodyColorQuestion),
                Option("시베리안 허스키", bodyColorQuestion),
                Option("도베르만", bodyColorQuestion),
                Option("차우차우", bodyColorQuestion),
            )
        )

        // 고양이 종류 질문
        catBreedsQuestion = Question(
            text = "고양이 종류를 선택해주세요.",
            options = listOf(
                Option("페르시안", bodyColorQuestion),
                Option("페르시안 친칠라", bodyColorQuestion),
                Option("러시안 블루", bodyColorQuestion),
                Option("샴", bodyColorQuestion),
                Option("스코티시 폴드", bodyColorQuestion),
                Option("노르웨이 숲", bodyColorQuestion),
                Option("뱅갈", bodyColorQuestion),
                Option("먼치킨", bodyColorQuestion),
                Option("아메리칸 숏헤어", bodyColorQuestion),
                Option("코리안 숏헤어", bodyColorQuestion),
                Option("터키시 앙고라", bodyColorQuestion),
                Option("랙돌", bodyColorQuestion),
                Option("스핑크스", bodyColorQuestion),
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