package com.fancytank.mypaws

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fancytank.mypaws.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val base_questions = listOf( // 질문 목록
        "사용자님의 이름을 입력해주세요.",
        "세상에서 가장 이쁜 내새꾸의 이름은 무엇인가요?"
    )
    private var currentQuestionIndex = 0 // 현재 질문의 인덱스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 첫 번째 질문 설정
        binding.tvQuestions.text = base_questions[currentQuestionIndex]

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


        binding.btnNext.setOnClickListener {
            val input = binding.etAnswers.text.toString()

            if (input.isEmpty()) {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 반가워요! 부분을 안보이게 하고
                binding.tvWelcome.visibility = View.INVISIBLE
                // 입력된 데이터 저장
                Constants.answers.add(input)
                // 입력란 초기화
                binding.etAnswers.text!!.clear()

                // 다음 질문으로 이동
                if (currentQuestionIndex < base_questions.size -1) {
                    currentQuestionIndex++
                    binding.tvQuestions.text = base_questions[currentQuestionIndex]
                } else {
                    // 질문이 끝났을 경우
//                    binding.tvQuestions.text = "감사합니다."
//                    binding.etAnswers.visibility = View.GONE
//                    binding.btnNext.visibility = View.GONE
//                    binding.tvWelcome.visibility = View.VISIBLE
//                    binding.tvWelcome.text = "답변: ${answers.joinToString(",")}"

                    val intent = Intent(this, QuestionsActivity::class.java)

                    startActivity(intent)

                    finish()
                }
            }
        }
    }
}