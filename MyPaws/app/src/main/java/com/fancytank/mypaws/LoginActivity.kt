package com.fancytank.mypaws

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.fancytank.mypaws.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleLoginManager: GoogleSignInManager
    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LOGIN ACTIVITY 여기까지 실행됨?", "1")

        userId = UserPreferences.getUserId(this)
        if (userId != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        googleLoginManager = GoogleSignInManager
        binding.btnSignInGoogle.setOnClickListener {

           signInWithGoogle(this)
        }
    }

    private fun signInWithGoogle(context: Context) {

        lifecycleScope.launch {
            googleLoginManager.googleSignIn(
                context = this@LoginActivity,
                filterByAuthorizedAccounts = false,
                doOnSuccess = { credential ->
                    Toast.makeText(this@LoginActivity, "Welcome, $credential.displayName!", Toast.LENGTH_LONG).show()
                    // 로그인 성공했으면 메인 액티비티 실행
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("GOOGLE_ID", credential.id)
                    intent.putExtra("DISPLAY_NAME", credential.displayName)
                    intent.putExtra("PROFILE_PICTURE_URI", credential.profilePictureUri)
                    startActivity(intent)
                    finish()
                },
                doOnError = { exception ->
                    Toast.makeText(this@LoginActivity, "Login Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.d("LOGIN FAILED :: ", "${exception.message}")
                }
            )
        }
    }

}