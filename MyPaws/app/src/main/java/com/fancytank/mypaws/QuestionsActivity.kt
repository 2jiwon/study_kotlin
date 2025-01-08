package com.fancytank.mypaws

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fancytank.mypaws.QuestionData
import com.fancytank.mypaws.Question

class QuestionsActivity : AppCompatActivity() {

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
    private lateinit var radioOption1: RadioButton
    private lateinit var radioOption2: RadioButton
    private lateinit var radioOption3: RadioButton
    private lateinit var radioOption4: RadioButton
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        Log.d("USER_NAME : ", cUserName.toString())

        // 뷰 요소 연결
        tvQuestions = findViewById(R.id.tv_questions)
        radioGroupOptions = findViewById(R.id.radioGroupOptions)
        radioOption1 = findViewById(R.id.radioOption1)
        radioOption2 = findViewById(R.id.radioOption2)
        radioOption3 = findViewById(R.id.radioOption3)
        radioOption4 = findViewById(R.id.radioOption4)
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

            // 보기 텍스트 및 가시성 설정
            val options = question.options
            val radioButtons = listOf(radioOption1, radioOption2, radioOption3, radioOption4)

            // 모든 RadioButton 숨기기
            radioButtons.forEach { it.visibility = View.GONE }

            // 필요한 만큼 RadioButton 업데이트
            options.forEachIndexed { index, option ->
                if (index < radioButtons.size) {
                    radioButtons[index].text = option.text
                    radioButtons[index].visibility = View.VISIBLE
                }
            }

            // RadioGroup 초기화
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
        val selectedIndex = when (selectedOptionId) {
            R.id.radioOption1 -> 0
            R.id.radioOption2 -> 1
            R.id.radioOption3 -> 2
            R.id.radioOption4 -> 3
            else -> -1
        }

        // 다음 질문으로 넘어가기
        if (selectedIndex != -1) {
            val selectedOption = currentQuestion?.options?.get(selectedIndex)
            currentQuestion = selectedOption?.nextQuestion

            if (currentQuestion != null) {
                showQuestion()
            } else {
                Toast.makeText(this, "모든 질문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}