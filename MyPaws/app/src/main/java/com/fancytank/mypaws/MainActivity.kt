package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fancytank.mypaws.data.database.AppDatabase
import com.fancytank.mypaws.data.entity.Pet
import com.fancytank.mypaws.data.entity.User
import com.fancytank.mypaws.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val TAG = "MAIN ACTIVITY :: "

    private lateinit var binding: ActivityMainBinding
    private lateinit var baseQuestions: List<String>
    private lateinit var baseHints: List<String>

    var user : User? = null
    var pet : List<Pet>? = null

    private var currentQuestionIndex = 0 // 현재 질문의 인덱스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면 전환 전에 UI 숨기기
        binding.root.visibility = View.GONE

        // strings.xml에서 기본 질문,힌트 가져오기
        baseQuestions = resources.getStringArray(R.array.base_questions).toList()
        baseHints = resources.getStringArray(R.array.base_hints).toList()

        // LoginActivity에서 전달받은 정보 가져오기
        val googleId = intent.getStringExtra("GOOGLE_ID")
        googleId?.let { Log.d(TAG, it) }

        // 기존에 이미 사용자 정보가 있는지 확인
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
            googleId?.let { user = userDao.getUserByEmail(it) }

            Log.d(TAG, "user :: $user")

            if (user != null) {
                user?.let { u ->
                    val petDao = AppDatabase.getInstance(this@MainActivity).petDao()
                    u.id.let { pet = petDao.getPetsByUserId(it) }

                    Log.d(TAG, user.toString())
                    Log.d(TAG, pet.toString())

                    if (pet.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            // 숨겼던 UI 복원
                            binding.root.visibility = View.VISIBLE

                            // 첫 번째 질문 설정
                            binding.tvQuestions.text = baseQuestions[currentQuestionIndex]
                            // 힌트
                            binding.tilName.hint = baseHints[currentQuestionIndex]

                            // 처음에 "다음" 버튼 숨김
                            binding.btnNext.visibility = View.GONE

                            binding.etAnswers.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                    //                TODO("Not yet implemented")
                                }

                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                    // 텍스트가 변경될 때 호출
                                    binding.btnNext.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                                }

                                override fun afterTextChanged(s: Editable?) {
                                    //                TODO("Not yet implemented")
                                }
                            })

                            binding.etAnswers.setText(u.username)

                            binding.btnNext.setOnClickListener {
                                val input = binding.etAnswers.text.toString()

                                Log.d(TAG, "input :: $input")

                                if (input.isEmpty()) {
                                    Toast.makeText(this@MainActivity, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    // 반가워요! 부분을 안보이게 하고
                                    binding.tvWelcome.visibility = View.INVISIBLE
                                    // 입력된 데이터 저장
                                    Constants.answers.add(input)

                                    // 테이블에 저장
                                    if (currentQuestionIndex == 0) {
                                        u.nickname = input

                                        GlobalScope.launch(Dispatchers.IO) {
                                            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
                                            userDao.updateUser(u)
                                        }
                                    } else {

                                        GlobalScope.launch(Dispatchers.IO) {
                                            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
                                            val user = userDao.getUserByEmail(u.email)

                                            val pet = Pet(
                                                name = input,
                                                userId = user?.id ?: 0L,
                                                type = "",
                                                breed = "",
                                                age = 0,
                                                bodyColor = "",
                                                eyeColor = ""
                                            )
                                            val petDao = AppDatabase.getInstance(this@MainActivity).petDao()
                                            petDao.insertPet(pet)
                                        }
                                    }

                                    // 입력란 초기화
                                    binding.etAnswers.text!!.clear()

                                    // 다음 질문으로 이동
                                    if (currentQuestionIndex < baseQuestions.size - 1) {
                                        currentQuestionIndex++
                                        binding.tvQuestions.text = baseQuestions[currentQuestionIndex]
                                        binding.tilName.hint = baseHints[currentQuestionIndex]
                                    } else {
                                        // 질문이 끝났을 경우
                                        val intent = Intent(this@MainActivity, QuestionsActivity::class.java)

                                        startActivity(intent)

                                        finish()
                                    }
                                }
                            }
                        }
                    } else {
                        // 만약 둘 다 이미 정보가 있으면 바로 ChatActivity로 넘어감
                        withContext(Dispatchers.Main) {

                            user?.let { u ->
                                pet?.let { p ->
                                    val prompt = OpenAIClient().generatePrompt(u, p[0])

                                    OpenAIClient().generateResponse(
                                        prompt,
                                        onSuccess = { response ->
                                            runOnUiThread {
                                                // 결과를 화면에 표시
                                                val intent = Intent(this@MainActivity, ChatActivity::class.java)
                                                intent.putExtra("AI_RESPONSE", response)
                                                startActivity(intent)
                                            }
                                        },
                                        onError = { error ->
                                            runOnUiThread {
                                                Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                                                Log.d("Error :", error)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

            } else {

                withContext(Dispatchers.Main) {
                    // user가 null일 경우 아래 부분 실행
                    val username: String = intent.getStringExtra("DISPLAY_NAME") ?: ""
                    val profilePicture = intent.getStringExtra("PROFILE_PICTURE_URI") ?: ""

                    Log.d(TAG, "username :: $username")

                    // 사용자 정보를 먼저 저장
                    val userInfo = User(
                        email = googleId.toString(),
                        password = "1234",
                        username = username,
                        nickname = "",
                        pictureUrl = profilePicture
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
                        userDao.insertUser(userInfo)
                    }

                    // 숨겼던 UI 복원
                    binding.root.visibility = View.VISIBLE

                    // 첫 번째 질문 설정
                    binding.tvQuestions.text = baseQuestions[currentQuestionIndex]
                    // 힌트
                    binding.tilName.hint = baseHints[currentQuestionIndex]

                    // 처음에 "다음" 버튼 숨김
                    binding.btnNext.visibility = View.GONE

                    binding.etAnswers.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            //                TODO("Not yet implemented")
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            // 텍스트가 변경될 때 호출
                            binding.btnNext.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                        }

                        override fun afterTextChanged(s: Editable?) {
                            //                TODO("Not yet implemented")
                        }
                    })

                    binding.etAnswers.setText(username)

                    binding.btnNext.setOnClickListener {
                        val input = binding.etAnswers.text.toString()

                        Log.d(TAG, "input :: $input")

                        if (input.isEmpty()) {
                            Toast.makeText(this@MainActivity, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 반가워요! 부분을 안보이게 하고
                            binding.tvWelcome.visibility = View.INVISIBLE
                            // 입력된 데이터 저장
                            Constants.answers.add(input)

                            // 테이블에 저장
                            if (currentQuestionIndex == 0) {
                                userInfo.nickname = input

                                GlobalScope.launch(Dispatchers.IO) {
                                    val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
                                    userDao.updateUser(userInfo)
                                }
                            } else {

                                GlobalScope.launch(Dispatchers.IO) {
                                    val userDao = AppDatabase.getInstance(this@MainActivity).userDao()
                                    val user = userDao.getUserByEmail(userInfo.email)

                                    val pet = Pet(
                                        name = input,
                                        userId = user?.id ?: 0L,
                                        type = "",
                                        breed = "",
                                        age = 0,
                                        bodyColor = "",
                                        eyeColor = ""
                                    )
                                    val petDao = AppDatabase.getInstance(this@MainActivity).petDao()
                                    petDao.insertPet(pet)
                                }
                            }

                            // 입력란 초기화
                            binding.etAnswers.text!!.clear()

                            // 다음 질문으로 이동
                            if (currentQuestionIndex < baseQuestions.size - 1) {
                                currentQuestionIndex++
                                binding.tvQuestions.text = baseQuestions[currentQuestionIndex]
                                binding.tilName.hint = baseHints[currentQuestionIndex]
                            } else {
                                // 질문이 끝났을 경우
                                val intent = Intent(this@MainActivity, QuestionsActivity::class.java)

                                startActivity(intent)

                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}
