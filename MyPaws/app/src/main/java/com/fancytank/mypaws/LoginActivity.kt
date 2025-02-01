package com.fancytank.mypaws

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fancytank.mypaws.databinding.ActivityLoginBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleLoginHelper: GoogleSignInManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LOGIN ACTIVITY 여기까지 실행됨?", "1")

        googleLoginHelper = GoogleSignInManager
        binding.btnSignInGoogle.setOnClickListener {
           signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            GoogleSignInManager.googleSignIn(
                context = this@LoginActivity,
                filterByAuthorizedAccounts = false,
                doOnSuccess = { displayName ->
                    Toast.makeText(this@LoginActivity, "Welcome, $displayName!", Toast.LENGTH_LONG).show()
                },
                doOnError = { exception ->
                    Toast.makeText(this@LoginActivity, "Login Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.d("LOGIN FAILED :: ", "${exception.message}")
                }
            )
        }
    }

}