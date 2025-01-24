package com.fancytank.mypaws

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleSignInHelper(
    private val context: Context
) {

    companion object {
        const val TAG = "Google LOGIN"
        const val WEB_CLIENT_ID = "웹 클라이언트 ID"
        //const val SERVER_URL = ""
    }
    private val credentialManager : CredentialManager
    private val googleIdOption : GetGoogleIdOption
    private val request : GetCredentialRequest
    private val coroutineScope: CoroutineScope

    init {
        credentialManager = CredentialManager.create(context)

        Log.d("LOGIN_DEBUG_INFO", "Context during CredentialManager initialization: ${context.toString()}")

        Log.d("LOGIN_DEBUG_INFO", "CredentialManager: $credentialManager")

        googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(WEB_CLIENT_ID) // 웹 클라이언트 ID
            .setFilterByAuthorizedAccounts(false) // 기존 계정 필터링 해제
            .setAutoSelectEnabled(true) //이전에 선택한 계정을 기억함
            .build()
        request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()


        coroutineScope = CoroutineScope(Dispatchers.Main)

        Log.d("LOGIN_DEBUG_INFO", "request initialization: ${request.toString()}")


        Log.d("LOGIN_DEBUG", "Network connectivity available: ${isNetworkAvailable()}")

        val isGooglePlayServicesAvailable =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)

        Log.d("LOGIN_DEBUG", "Google Play Services availability: $isGooglePlayServicesAvailable")

    }

    fun isNetworkAvailable() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }



    fun requestGoogleLogin(onSuccess: (String) -> Unit,
                           onFailure: (String) -> Unit) {
        coroutineScope.launch {
            Log.d("LOGIN DEBUG_INFO", "Before calling getCredential")

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                Log.d("LOGIN_RESULT", "Credential result: $result")

                handleSignInResult(result, onSuccess, onFailure)
            } catch (e: GetCredentialException){
                Log.d("LOGIN_ERROR", "Error: ${e.localizedMessage}", e)

                onFailure("Google Sign-in failed: ${e.localizedMessage}")
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Unexpected error occurred during getCredential: ${e.localizedMessage}", e)
            }

        }
    }

    fun handleSignInResult(
        result: GetCredentialResponse,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        when(val credential = result.credential) {
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        //sendTokenToServer(token, onSuccess, onFailure) 서버로 토큰을 전송하고 결과에 따른 처리
                        Log.d(TAG, idToken) //토큰
                        Log.d(TAG, googleIdTokenCredential.id) //이메일
                        googleIdTokenCredential.displayName?.let { Log.d(TAG, it) } //이름
                        onSuccess("구글 로그인 성공") //성공 시 처리 함수, 서버 응답 후 실행, 여기서는 테스트를 위해 이곳에서 실행
                    }
                    catch (e: GoogleIdTokenParsingException){
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                }
                else {
                    Log.e(TAG, "Unexpected type of credential")
                    onFailure("구글 로그인에 실패하였습니다. 다시 시도해주세요.")
                }
            }
            else -> {
                Log.e(TAG, "Unexpected type of credential")
                onFailure("구글 로그인에 실패하였습니다. 다시 시도해주세요.")
            }
        }
    }
}