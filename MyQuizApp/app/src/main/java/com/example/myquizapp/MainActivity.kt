package com.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart : Button = findViewById<Button>(R.id.btn_start)
        val etName : EditText = findViewById(R.id.et_name)

        btnStart.setOnClickListener {
            if (etName.text.isEmpty()) {
                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_LONG).show()
            } else {
                // 여기에서 다른 액티비티로 넘어가는 처리
                val intent = Intent(this, QuizQuestionsActivity::class.java)

                // 액티비티를 시작하는 동시에 추가로 정보를 보내기 위해 putExtra 사용
                // intent로 한 액티비티에서 다른 액티비티로 추가 정보를 넘긴 다음, 다른 액티비티에서 정보값을 회수할 수 있다.
                intent.putExtra(Constants.USER_NAME, etName.text.toString())

                startActivity(intent)
                // finish()를 추가하면 이동한 액티비티에서 뒤로가기 버튼을 눌렀을 때 앱을 종료하게 됨
                finish()
            }
        }
    }
}