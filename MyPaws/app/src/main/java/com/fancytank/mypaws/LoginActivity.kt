package com.fancytank.mypaws

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.fancytank.mypaws.databinding.ActivityLoginBinding
import com.fancytank.mypaws.ui.theme.AriDuriRegular
import com.fancytank.mypaws.ui.theme.Primary
import com.fancytank.mypaws.ui.theme.Regular
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleLoginManager: GoogleSignInManager
    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

//        binding = ActivityLoginBinding.inflate(layoutInflater)
    //        setContentView(binding.root)

            Log.d("LOGIN ACTIVITY 여기까지 실행됨?", "1")

            userId = UserPreferences.getUserId(this)
        if (userId != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        googleLoginManager = GoogleSignInManager
//        binding.btnSignInGoogle.setOnClickListener {
//           signInWithGoogle(this)
//        }
        setContent {
            LoginScreen { signInWithGoogle(this@LoginActivity) }
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

    @Composable
    fun LoginScreen(onGoogleSignInClick: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Primary) // primary color
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "내새꾸",
                fontSize = 50.sp,
                fontFamily = AriDuriRegular,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 5.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "반가워요!",
                        fontSize = 30.sp,
                        color = Color.Black,
                        fontFamily = AriDuriRegular
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "로그인 또는 회원가입",
                        fontSize = 16.sp,
                        color = Regular
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { onGoogleSignInClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        )) {
                        Image(
                            painter = painterResource(R.drawable.ic_google_log),
                            contentDescription = "google logo",
                            modifier = Modifier.height(25.dp)
                        )
                        Text(text = "Google 로그인")
                    }
                }
            }
        }
    }
}