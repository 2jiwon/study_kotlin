package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fancytank.mypaws.data.dao.PetDao
import com.fancytank.mypaws.data.database.AppDatabase
import com.fancytank.mypaws.data.entity.Pet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsActivity : AppCompatActivity() {

    private val TAG = "QUESTIONS ACTIVITY :: "

    private val openAIClient = OpenAIClient()

    private lateinit var petDao: PetDao

    // MainActivity에서 사용자와 펫 정보 저장된 값 가져오기
    private var userName: String = Constants.answers.getOrNull(0) ?: ""
    private var petName: String = Constants.answers.getOrNull(1) ?: ""

    // 질문 리스트
    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    // View Binding
    private lateinit var tvQuestions: TextView
    private lateinit var spinnerOptions: Spinner
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        Log.d(TAG, "username :: $userName")

        petDao = AppDatabase.getInstance(this).petDao()

        // 질문 초기화
        QuestionData.initialize(this)
        // QuestionData에서 가져온 질문들 할당
        questionList.addAll(listOf(
            QuestionData.petTypeQuestion,
            QuestionData.dogBreedsQuestion,
            QuestionData.catBreedsQuestion
        ))

        // 뷰 요소 연결
        tvQuestions = findViewById(R.id.tv_questions)
        spinnerOptions = findViewById(R.id.spinnerOptions)
        btnNext = findViewById(R.id.btn_next)

        // 첫번째 질문 표시
        showQuestion()

        // 버튼 클릭시 선택한 옵션 처리
        btnNext.setOnClickListener {
            handleAnswer()
        }
    }

    fun showQuestion() {
        if (currentQuestionIndex >= questionList.size) {
            completeQuestions()
            return
        }

        val question = questionList[currentQuestionIndex]
        tvQuestions.text = question.text

        // spinner 옵션 설정
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.select_answer_prompt)) + question.options.map { it.text }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter
    }

    fun handleAnswer() {
        // 선택한 옵션
        val selectedIndex = spinnerOptions.selectedItemPosition

        // 답변이 선택 되지 않은 경우
        if (selectedIndex == 0) {
            Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 다음 질문으로 넘어가기
        val selectedOption = questionList[currentQuestionIndex].options[selectedIndex - 1]
        Constants.answers.add(selectedOption.text) // 선택된 답변 저장

        currentQuestionIndex++

        if (currentQuestionIndex < questionList.size) {
            showQuestion()
        } else {
            completeQuestions()
        }
    }

    private fun completeQuestions() {
        val userId = UserPreferences.getUserId(this) ?: 0L
        // 모든 질문 완료시 DB 저장
        val pet = Pet(
            userId = userId,
            name = petName,
            type= Constants.answers[1],
            breed= Constants.answers[2],
            bodyColor= Constants.answers[3],
            eyeColor= Constants.answers[4])
        CoroutineScope(Dispatchers.IO).launch {
            petDao.insertPet(pet)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@QuestionsActivity, "펫 정보를 저장했습니다.", Toast.LENGTH_SHORT).show()
                generateInitAIResponse(pet)
            }
        }
    }

    fun createInitPrompt(pet: Pet): String {
        val petType = pet.type
        val breed = pet.breed
        val bodyColor = pet.bodyColor

        Log.d(TAG, "petname :: $petName")
        Log.d(TAG, "petType :: $petType")
        Log.d(TAG, "breed :: $breed")
        Log.d(TAG, "bodyColor :: $bodyColor")

        return """
            You are a $bodyColor $breed $petType. Your name is $petName. Talk to me as $petType until I say exactly 'We should stop this game.', and call me as $userName.
            Please mind that you are very beloved, friendly pet. 
        """.trimIndent()
    }

    fun generateInitAIResponse(pet: Pet) {
        val prompt = createInitPrompt(pet)

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