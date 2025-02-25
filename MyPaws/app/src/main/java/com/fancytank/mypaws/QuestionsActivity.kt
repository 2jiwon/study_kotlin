package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fancytank.mypaws.QuestionData.petTypeQuestion
import com.fancytank.mypaws.data.dao.PetDao
import com.fancytank.mypaws.data.dao.UserDao
import com.fancytank.mypaws.data.database.AppDatabase
import com.fancytank.mypaws.data.entity.Pet
import com.fancytank.mypaws.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsActivity : AppCompatActivity() {

    private val TAG = "QUESTIONS ACTIVITY :: "

    private val openAIClient = OpenAIClient()

    private lateinit var petDao: PetDao
    private lateinit var userDao: UserDao

    // MainActivity에서 사용자와 펫 정보 저장된 값 가져오기
    private var userName: String = Constants.answers.getOrNull(0) ?: ""
    private var petName: String = Constants.answers.getOrNull(1) ?: ""

    // 질문 리스트
    private val questionList = mutableListOf<Question>()
    private var currentQuestion: Question? = null
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
        userDao = AppDatabase.getInstance(this).userDao()

        // 질문 초기화
        QuestionData.initialize(this)
        val petTypeQuestion = QuestionData.petTypeQuestion
        val dogBreedQuestion = QuestionData.dogBreedsQuestion
        val catBreedQuestion = QuestionData.catBreedsQuestion

        // QuestionData에서 가져온 질문들 할당
        questionList.addAll(listOf(
            petTypeQuestion,
            dogBreedQuestion,
            catBreedQuestion
        ))

        currentQuestion = petTypeQuestion

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

        currentQuestion?.let { question ->
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
    }

    fun handleAnswer() {
        // 선택한 옵션
        val selectedIndex = spinnerOptions.selectedItemPosition

        // 답변이 선택 되지 않은 경우
        if (selectedIndex == 0) {
            Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        currentQuestion?.let { question ->
            Constants.answers.add(question.options[selectedIndex - 1].text) // 선택된 답변 저장

            val selectedOption = currentQuestion?.options?.get(selectedIndex - 1)

            currentQuestion = selectedOption?.nextQuestion

            if (currentQuestion != null) {
                showQuestion()
            } else {
                Toast.makeText(this, "모든 질문이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                completeQuestions()
            }
        }
    }



    private fun completeQuestions() {
        val userId = UserPreferences.getUserId(this) ?: 0L

        GlobalScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId)

            // 모든 질문 완료시 DB 저장
            val pet = Pet(
                userId = userId,
                name = petName,
                type= Constants.answers[2],
                breed= Constants.answers[3],
                bodyColor= Constants.answers[4],
                eyeColor= Constants.answers[5])

            CoroutineScope(Dispatchers.IO).launch {
                petDao.insertPet(pet)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuestionsActivity, "펫 정보를 저장했습니다.", Toast.LENGTH_SHORT).show()
                    if (user != null) {
                        Toast.makeText(this@QuestionsActivity, user.toString(), Toast.LENGTH_SHORT).show()
                        generateInitAIResponse(pet, userName)
                    }
                }
            }
        }
    }


    fun generateInitAIResponse(pet: Pet, userName: String) {
        val prompt = OpenAIClient().createInitPrompt(pet, userName)

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