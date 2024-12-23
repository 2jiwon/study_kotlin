package com.example.myquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import java.lang.reflect.Type

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    // 진행바
    private var progressBar : ProgressBar? = null
    // 진행바 옆의 0/9 텍스트
    private var tvProgress : TextView? = null
    // 질문
    private var tvQuestion : TextView? = null
    // 이미지
    private var ivImage : ImageView? = null
    // 선택 옵션들
    private var tvOptionOne : TextView? = null
    private var tvOptionTwo : TextView? = null
    private var tvOptionThree : TextView? = null
    private var tvOptionFour : TextView? = null

    // 메서드에서 사용하기 위한 변수 셋팅
     // 현재 포지션
    private var mCurrentPosition : Int = 1
     // 질문 배열 리스트
    private var mQuestionsList : ArrayList<Question>? = null
     // 어떤 옵션을 선택했는지
    private var mSelectedOptionPosition : Int = 0

    // Submit 버튼
    private var btnSubmit : Button? = null

    // 메인 액티비티에서 넘긴 값들을 받기 위한 변수들
    private var mUserName: String? = null
    private var mCorrectAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        // 현재 액티비티에 USER_NAME의 위치에서 세부 사항을 가져온다
        mUserName = intent.getStringExtra(Constants.USER_NAME)


        // UI와 연결
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)

        btnSubmit = findViewById(R.id.btn_submit)
        // 질문들 가져오기
        mQuestionsList = Constants.getQuestions()

        setQuestion()

        // 각 옵션을 클릭 가능하게 만드는 부분
        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)
        // 제출 버튼 클릭
        btnSubmit?.setOnClickListener(this)
    }

    private fun setQuestion() {
        // 현재 위치를 이동하면서 질문을 가져올 수 있도록 (index 0 대신 1을 쓰는 이유는 텍스트 표시를 쉽게 하기 위함임)
        var question: Question = mQuestionsList!![mCurrentPosition - 1]

        // 모든 옵션을 리셋하기
        defaultOptionsView()

        // 진행상태 나타내는 바
        progressBar?.progress = mCurrentPosition
        // 옆의 텍스트
        tvProgress?.text = "$mCurrentPosition/${progressBar?.max}"
        // 질문 자체
        tvQuestion?.text = question.question
        // 질문에 속한 이미지
        ivImage?.setImageResource(question.image)
        // 선택지 옵션들
        tvOptionOne?.text = question.optionOne
        tvOptionTwo?.text = question.optionTwo
        tvOptionThree?.text = question.optionThree
        tvOptionFour?.text = question.optionFour

        // 현재 위치를 기준으로 버튼이 submit 이거나 finish가 되도록 결정하는 부분
            // 현재 위치가 마지막 질문이라면
        if (mCurrentPosition == mQuestionsList!!.size) {
            btnSubmit?.text = "FINISH"
        } else {
            btnSubmit?.text = "SUBMIT"
        }
    }

    // 기본 옵션의 색상을 정의하는 부분
    private fun defaultOptionsView() {
        // 옵션이 뭐가 있는지 알아야 하므로 배열 가져오기
        val options = ArrayList<TextView>()
        // 지금은 이 부분이 뭔지 확실하게 이해가 안감...
        tvOptionOne?.let{
            options.add(0, it)
        }
        tvOptionTwo?.let{
            options.add(1, it)
        }
        tvOptionThree?.let{
            options.add(2, it)
        }
        tvOptionFour?.let{
            options.add(3, it)
        }

        // 이제 for문으로 option들의 기본 스타일을 정의해준다
        for (option in options) {
            option.setTextColor(Color.parseColor("#748089"))
//            option.setTextColor(Color.parseColor("#FF0000"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    // 선택한 옵션의 색상을 변경하는 부분
    private fun selectedOptionView(tv:TextView, selectedOptionNum:Int) {
        // 옵션들을 기본 디폴트뷰로 설정하고
        defaultOptionsView()

        mSelectedOptionPosition = selectedOptionNum

        // 선택한 옵션의 스타일 변경
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    override fun onClick(view: View?) {
        // 옵션을 눌렀을 때 만들어둔 메소드를 불러오기
        when(view?.id) {
            R.id.tv_option_one -> {
                tvOptionOne?.let{
                    selectedOptionView(it, 1)
                }
            }
            R.id.tv_option_two -> {
                tvOptionTwo?.let{
                    selectedOptionView(it, 2)
                }
            }
            R.id.tv_option_three -> {
                tvOptionThree?.let{
                    selectedOptionView(it, 3)
                }
            }
            R.id.tv_option_four -> {
                tvOptionFour?.let{
                    selectedOptionView(it, 4)
                }
            }

            // 제출 버튼을 눌렀을 때는 선택한 옵션 위치를 리셋하고 다음 퀴즈로 넘어가야 한다.
            R.id.btn_submit -> {
                Log.i("mCurrentPosition", mCurrentPosition.toString())
                Log.i("mSelectedOptionPosition", mSelectedOptionPosition.toString())

                // 선택한 옵션이 0이면
                if (mSelectedOptionPosition == 0) { // 0가 기본값이기 때문에 0과 비교하는 것임
                    mCurrentPosition++

                    // 질문이 남아있다면 다음으로 이동
                    when{
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion()
                        }
                        // 질문이 더 없다면 -> 끝까지 다 왔다면
                        else -> {
                            // Toast.makeText(this, "Congratulation! You made it to the end!", Toast.LENGTH_SHORT)

                            // 이제 필요한 변수들을 result 액티비티로 넘기기
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList?.size)

                            // 위에서 만든 intent로 액티비티를 시작
                            startActivity(intent)
                            // 액티비티 종료
                            finish()
                        }
                    }
                } else { // 옵션이 0이 아니라면 옵션을 선택한 것이니 정답을 체크
                    val question = mQuestionsList?.get(mCurrentPosition - 1)
                    // 선택한 옵션과 정답이 일치하지 않은 경우
                    if (question!!.correctAnswer != mSelectedOptionPosition) {
                         answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    } else { // 정답인 경우
                        mCorrectAnswers++
                    }
                    // 기본적으로는 정답을 표시
                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    // 마지막 질문이 아니라면 다음 질문으로 넘어가도록
                    if (mCurrentPosition == mQuestionsList!!.size) {
                        btnSubmit?.text = "FINISH"
                    } else {
                        btnSubmit?.text = "Go To Next Question"
                    }
                    // 선택했던 옵션은 리셋
                    mSelectedOptionPosition = 0
                }

            }
        }
    }

    // 선택한 옵션의 배경색을 지정하는 부분(정답인지 아닌지에 따라)
    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            1 -> {
                tvOptionOne?.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                tvOptionTwo?.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                tvOptionThree?.background = ContextCompat.getDrawable(this, drawableView)
            }
            4 -> {
                tvOptionFour?.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }
}