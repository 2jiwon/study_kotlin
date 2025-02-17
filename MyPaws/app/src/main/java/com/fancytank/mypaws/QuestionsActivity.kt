package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class QuestionsActivity : AppCompatActivity() {

    private val openAIClient = OpenAIClient()

    // Constants.answers의 [0]은 사용자명
    var cUserName: String = Constants.answers[0]
    // Constants.answers의 [1]은 내새꾸 이름
    var cPetsName: String = Constants.answers[1]

    // 질문 종류
    var currentQuestion: Question? = null

    // View Binding
    private lateinit var tvQuestions: TextView
    private lateinit var spinnerOptions: Spinner
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        Log.d("USER_NAME : ", cUserName.toString())

        // QuestionData 초기화
        QuestionData.initialize(this)

        // QuestionData에서 가져온 질문들 할당
        val petTypeQuestion: Question = QuestionData.petTypeQuestion
        val dogBreedsQuestion: Question = QuestionData.dogBreedsQuestion
        val catBreedsQuestion: Question = QuestionData.catBreedsQuestion
        // 질문 종류 할당
        currentQuestion = petTypeQuestion

        // 뷰 요소 연결
        tvQuestions = findViewById(R.id.tv_questions)
        spinnerOptions = findViewById(R.id.spinnerOptions)
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

            // spinner 옵션 설정
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOf(getString(R.string.select_answer_prompt)) + currentQuestion!!.options.map { it.text }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOptions.adapter = adapter
        }
    }

    fun handleAnswer() {
        // 선택한 옵션
        val selectedIndex = spinnerOptions.selectedItemPosition
        // 답변이 선택 되지 않은 경우
        if (selectedIndex == -1) {
            Toast.makeText(this, "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            // 다음 질문으로 넘어가기

            currentQuestion?.let { question ->
                Constants.answers.add(question.options[selectedIndex - 1].text) // 선택된 답변 저장

                val selectedOption = currentQuestion?.options?.get(selectedIndex - 1)

                currentQuestion = selectedOption?.nextQuestion

                if (currentQuestion != null) {
                    showQuestion()
                } else {
                    Toast.makeText(this, "모든 질문이 완료되었습니다.", Toast.LENGTH_SHORT).show()

//                    generateInitAIResponse(Constants.answers)
                }
            }
        }
    }

    fun createInitPrompt(selectedAnswers: List<String>): String {
        val petType = selectedAnswers[2]
        val breed = selectedAnswers[3]
        val bodyColor = selectedAnswers[4]

        return """
            You are a $bodyColor $breed $petType. Your name is $cPetsName. Talk to me as $petType, and call me as $cUserName.
        """.trimIndent()
    }

    fun generateInitAIResponse(selectedAnswers: List<String>) {
        val prompt = createInitPrompt(selectedAnswers)

        openAIClient.generateResponse(prompt,
            onSuccess = { response ->
                runOnUiThread {
                    // ChatActivity 이동
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