package com.fancytank.mypaws

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fancytank.mypaws.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty()) {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 반가워요! 부분을 안보이게 하고
                binding.tvWelcome.visibility = View.INVISIBLE
                // 새로운 질문을 넣는다
                binding.tvQUsername.text = "세상에서 가장 이쁜 내새꾸의 이름은 무엇인가요?"

            }
        }

    }
}