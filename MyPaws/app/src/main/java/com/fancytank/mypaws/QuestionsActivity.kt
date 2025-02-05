package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class QuestionsActivity : AppCompatActivity() {

    private val openAIClient = OpenAIClient()

    // Constants.answers의 [0]은 사용자명
    var cUserName: String = Constants.answers[0]
    // Constants.answers의 [1]은 내새꾸 이름
    var cPetsName: String = Constants.answers[1]

    // QuestionData에서 질문 가져오기
    val petTypeQuestion: Question = QuestionData.petTypeQuestion
    val dogBreedsQuestion: Question = QuestionData.dogBreedsQuestion
    val catBreedsQuestion: Question = QuestionData.catBreedsQuestion

    // 질문 종류
    var currentQuestion: Question? = petTypeQuestion

    // View Binding
    private lateinit var tvQuestions: TextView
    private lateinit var radioGroupOptions: RadioGroup
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        Log.d("USER_NAME : ", cUserName.toString())

        // 뷰 요소 연결
        tvQuestions = findViewById(R.id.tv_questions)
        radioGroupOptions = findViewById(R.id.radioGroupOptions)
        btnNext = findViewById(R.id.btn_next)

        // 질문 시작
        showQuestion()

        // 버튼 클릭시 선택한 옵션 처리
        btnNext.setOnClickListener {
            handleAnswer()
        }
    }

    fun showQuestion() {
        currentQuestion?.let { question ->
            // 질문 텍스트 설정
            tvQuestions.text = question.text

            // 기존 RadioGroup의 RadioButton 모두 제거
            radioGroupOptions.removeAllViews()

            // 새로운 RadioButton 추가
            val options = question.options
            options.forEachIndexed { index, option ->
                val radioButton = RadioButton(this).apply {
                    id = View.generateViewId() // 고유 ID 생성
                    text = option.text
                    textSize = 16f
                    setPadding(8,10,8,10)
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                // RadioButton을 RadioGroup에 추가
                radioGroupOptions.addView(radioButton)
            }

            // RadioGroup 초기화 (선택해제)
            radioGroupOptions.clearCheck()
        }
    }

    fun handleAnswer() {
        // 선택한 옵션 가져오기
        val selectedOptionId = radioGroupOptions.checkedRadioButtonId
        // 답변이 선택 되지 않은 경우
        if (selectedOptionId == -1) {
            Toast.makeText(this, "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
        }

        // 선택된 RadioButton의 인덱스 가져오기
        val selectedIndex = radioGroupOptions.indexOfChild(findViewById(selectedOptionId))

        // 다음 질문으로 넘어가기
        if (selectedIndex != -1) {

            currentQuestion?.let { question ->
                Constants.answers.add(question.options[selectedIndex].text) // 선택된 답변 저장

                val selectedOption = currentQuestion?.options?.get(selectedIndex)
                currentQuestion = selectedOption?.nextQuestion

                if (currentQuestion != null) {
                    showQuestion()
                } else {
                     Toast.makeText(this, "모든 질문이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    generateAIResponse(Constants.answers)
                }
            }
        }
    }

    fun createPrompt(selectedAnswers: List<String>): String {
        val petType = selectedAnswers[2]
        val breed = selectedAnswers[3]
        val bodyColor = selectedAnswers[4]

        return """
            You are a $bodyColor $breed $petType. Your name is $cPetsName. Talk to me as $petType, and call me as $cUserName.
        """.trimIndent()
    }

    fun generateAIResponse(selectedAnswers: List<String>) {
        val prompt = createPrompt(selectedAnswers)

        openAIClient.generateResponse(prompt,
            onSuccess = { response ->
                runOnUiThread {
                    // 결과를 화면에 표시
//                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("AI_RESPONSE", response)
                    startActivity(intent)
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    Log.d("Error :", error)
                }
            }
        )
    }
}