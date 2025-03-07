package com.example.newquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    // 진행바
    private var progressBar: ProgressBar? = null

    // 진행바 옆 텍스트
    private var tvProgress: TextView? = null

    // 질문
    private var tvQuestion: TextView? = null

    // 오디오 버튼
    private var btnAudio: ImageButton? = null

    // 이미지
    private var ivImage: ImageView? = null

    // 선택 옵션들
    private var tvOptionOne: TextView? = null
    private var tvOptionTwo: TextView? = null
    private var tvOptionThree: TextView? = null
    private var tvOptionFour: TextView? = null

    // 현재 위치
    private var mCurrentPosition: Int = 1

    // 질문 배열 리스트
    private var mQuestionList: ArrayList<Question>? = null

    // 어떤 옵션을 선택했는지 위치값
    private var mSelectedOptionPosition: Int = 0

    // submit 버튼
    private var btnSubmit: Button? = null

    // 메인 액티비티에서 넘긴 사용자 이름 값
    private var mUserName: String? = null

    // 정답을 맞춘 갯수
    private var mCorrectAnswers: Int = 0

    // 답을 맞추려고 시도한 수
    private var trial = 0

    // TODO :: 질문과 선택 옵션을 소리로 들을 수 있게 하기

    // TODO :: 옵션 선택을 안하고 확인을 눌러도 다음문제로 넘어가지는 문제 해결하기 -> 완료

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        tts = Helper().initTextToSpeech(this)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        // UI 레이아웃과 연결하는 부분
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)

        btnSubmit = findViewById(R.id.btn_submit)
        btnAudio = findViewById(R.id.btn_audio)

        // 질문 리스트 가져오기
        mQuestionList = Constants.getQuestions(this)

        setQuestion()

        btnAudio?.setOnClickListener(this)

        // 각 옵션들을 클릭 가능하도록 만들기
        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)

//        if (mSelectedOptionPosition > 0) {
            // 제출 버튼 클릭
            btnSubmit?.setOnClickListener(this)
//        }
    }

    private fun setQuestion() {
        // 현재 위치를 이동하면서 질문을 가져오기 (!!는 non-nullable type에 사용)
        var question: Question = mQuestionList!![mCurrentPosition - 1]

        // 모든 옵션 기본 설정
        defaultOptionsView()

        // 진행상태 나타내는 바
        progressBar?.progress = mCurrentPosition
        // 진행상태 옆 텍스트
        tvProgress?.text = "$mCurrentPosition/${progressBar?.max}"

        // 질문 텍스트
        tvQuestion?.text = question.question
        // 질문에 속한 이미지
        ivImage?.setImageResource(question.image)
        // 선택 옵션 텍스트
        tvOptionOne?.text = question.optionOne
        tvOptionTwo?.text = question.optionTwo
        tvOptionThree?.text = question.optionThree
        tvOptionFour?.text = question.optionFour

        if (mCurrentPosition == mQuestionList!!.size) {
            btnSubmit?.text = "내 점수 확인"
        } else {
            btnSubmit?.text = "확인"
        }
    }

    // 옵션들의 디폴트 뷰를 설정하는 메서드
    private fun defaultOptionsView() {
        var trial = 0
        val options = ArrayList<TextView>()
        // tvOptionOne,Two...를 options 배열에 넣기
        tvOptionOne?.let { options.add(it) }
        tvOptionTwo?.let { options.add(it) }
        tvOptionThree?.let { options.add(it) }
        tvOptionFour?.let { options.add(it) }

        // 반복문을 통해 기본 스타일 정의
        for (option in options) {
            option.setTextColor(Color.parseColor("#748089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    // 옵션을 선택하면 색상이 바뀌도록 하는 메서드
    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        // 먼저 디폴트 뷰를 설정
        defaultOptionsView()

        // 이 메서드가 옵션을 클릭하면 실행되기 때문에, 여기에서 선택한 옵션 위치 지정
        mSelectedOptionPosition = selectedOptionNum

        // 선택된 옵션의 스타일을 변경
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    // 선택 옵션을 클릭(터치)했을 때
    override fun onClick(view: View?) {
        // 클릭된 view 요소에 따라 메서드 실행
        when (view?.id) {
            R.id.btn_audio -> {
                val question = mQuestionList?.get(mCurrentPosition - 1)
                question?.let { Helper().ttsSpeak(tts, question.question) }
            }

            R.id.tv_option_one -> {
                tvOptionOne?.let { selectedOptionView(it, 1) }
            }

            R.id.tv_option_two -> {
                tvOptionTwo?.let { selectedOptionView(it, 2) }
            }

            R.id.tv_option_three -> {
                tvOptionThree?.let { selectedOptionView(it, 3) }
            }

            R.id.tv_option_four -> {
                tvOptionFour?.let { selectedOptionView(it, 4) }
            }

            // 제출 버튼을 눌렀을 때
            R.id.btn_submit -> {

                Log.d("trial1 :::::::::: ", trial.toString())
                Log.i("mCurrentPosition ::: ", mCurrentPosition.toString())
                Log.i("mSelectedOptionPosition ::: ", mSelectedOptionPosition.toString())

                // 아직 선택옵션 위치가 기본값이면 아무 옵션을 선택하지 않은 것

                // 선택옵션 위치가 0이 아니면 옵션을 선택한 것이니 정답 체크
                if (mSelectedOptionPosition > 0) {

                    val question = mQuestionList?.get(mCurrentPosition - 1)

                    // 선택한 옵션과 정답이 일치하지 않은 경우
                    if (question!!.correctAnswer != mSelectedOptionPosition) {
                        // 메서드로 만들어서 띄워도 같은 에러 메시지 나온다. 다만 아직 앱이 crash되지는 않음.
//                      Helper().makeToast(this, "오답입니다.")
                        trial++

                        // 2번까지 다시 지정 가능하게
                        AlertDialog.Builder(this).run {
                            setMessage("오답입니다.")
                            setPositiveButton("OK", null)
                            show()
                        }
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                        btnSubmit?.text = "확인1"
                        mSelectedOptionPosition = 0

                    } else { // 정답인 경우
//                        Toast.makeText(this, "정답입니다.", Toast.LENGTH_SHORT).show()
                        mCorrectAnswers++
                        answerView(question.correctAnswer, R.drawable.correct_option_border_bg)
                        checkAnswer()
                    }

                    Log.d("trial2 :::::::::: ", trial.toString())

                    // 2번 오답 체크시 정답 보여줌
                    if (trial >= 2) {
                        answerView(question.correctAnswer, R.drawable.correct_option_border_bg)
                        checkAnswer()
                    }

                } else {

                    // 질문이 아직 남아있다면 다음 질문으로 이동
                    when {
                        mCurrentPosition <= mQuestionList!!.size -> {
                            setQuestion()
                        }

                        else -> { // 질문이 더이상 없다면
                            // 일단 Toast 띄움
                            // Toast.makeText(this, "축하합니다. 퀴즈를 끝까지 풀었습니다.", Toast.LENGTH_SHORT).show()
                            /**
                             * Toast로 테스트하다보니 Package has already queued 5 toasts. not showing more. 라고 에러가 발생했고, 이 에러에서 빠져나올 수 없었다.
                             * 알고보니 Android 12에서 Toast를 5개로 제한한다고...(이걸 통해 공격이 가능하기 때문)
                             * 내가 계속 같은 동작을 해서, 그게 연속 토스트로 인식되는 모양이다.
                             * 그러면 토스트를 다른걸로 대체해야 하는건지, 아니면 하나 띄운걸 취소하고 다시 띄우는 식으로 하면 되는지 알아봐야겠다.
                             */

                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionList?.size)

                            // 위에서 만든 intent로 액티비티를 시작
                            startActivity(intent)
                            // 액티비티 종료
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
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

    private fun checkAnswer() {
        // 마지막 질문이 아니라면 다음 질문으로 넘어가도록
        if (mCurrentPosition == mQuestionList!!.size) {
            btnSubmit?.text = "내 점수 확인"
        } else {
            btnSubmit?.text = "다음 문제로"
        }

        // 선택한 옵션값 초기화
        mSelectedOptionPosition = 0
        // 몇번 시도했는지 초기화
        trial = 0

        // 답을 보여줬기 때문에 위치를 1증가
        mCurrentPosition++
    }
}

