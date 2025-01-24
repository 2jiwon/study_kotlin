package com.fancytank.mypaws

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fancytank.mypaws.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleLoginHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LOGIN ACTIVITY 여기까지 실행됨?", "1")

        googleLoginHelper = GoogleSignInHelper(this)
        binding.btnSignInGoogle.setOnClickListener {
            googleLoginHelper.requestGoogleLogin(
                onSuccess = { message ->
                    Log.d("LOGIN ACTIVITY GoogleLogin 성공", message)
//                    val intent = Intent(this, MainActivity::class.java)
//
//                    startActivity(intent)
//
//                    finish()
                },
                onFailure = { errorMessage ->
                    Log.d("LOGIN ACTIVITY 여기까지 실행됨?", "2")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }




}