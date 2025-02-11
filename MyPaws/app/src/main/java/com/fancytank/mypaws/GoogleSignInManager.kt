package com.fancytank.mypaws

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 *
 * https://proandroiddev.com/google-sign-in-with-credential-manager-c54762376170
 */
object GoogleSignInManager {

    private lateinit var credentialManager: CredentialManager

    suspend fun googleSignIn(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
        doOnSuccess: (GoogleIdTokenCredential) -> Unit,
        doOnError: (Exception) -> Unit,
    ) {
        if (::credentialManager.isInitialized.not()) {
            credentialManager = CredentialManager
                .create(context)
        }

        val apiKey = context.getString(R.string.default_web_client_id)

        Log.d("LOGIN api key :: ", apiKey)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(apiKey)
            .setAutoSelectEnabled(false)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(googleIdOption)
            .build()

        requestSignIn(
            context,
            request,
            filterByAuthorizedAccounts,
            doOnSuccess,
            doOnError
        )
    }


    private suspend fun requestSignIn(
        context: Context,
        request: GetCredentialRequest,
        filterByAuthorizedAccounts: Boolean,
        doOnSuccess: (GoogleIdTokenCredential) -> Unit,
        doOnError: (Exception) -> Unit,
    ) {
        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context,
            )
            val credential = handleCredentials(result.credential)
            credential?.let {
                doOnSuccess(it)
            } ?: doOnError(Exception("Invalid user"))
        } catch (e: Exception){
            if (e is NoCredentialException && filterByAuthorizedAccounts) {
                googleSignIn(
                    context,
                    false,
                    doOnSuccess,
                    doOnError
                )
            } else {
                doOnError(e)
            }
        }
    }

    private fun handleCredentials(credential: Credential): GoogleIdTokenCredential? {
        Log.e("LOGIN credential :: ", credential.toString())

        when (credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        val expirationTime = getGoogleIdTokenExpiration(googleIdTokenCredential.idToken)
                        if (expirationTime != null) {
                            val currentTime = System.currentTimeMillis() / 1000 // Unix Timestamp 변환
                            if (currentTime >= expirationTime) {
                                Log.e("LOGIN", "ID Token expired. User needs to re-authenticate.")
                            } else {
                                Log.e("LOGIN", "ID Token is valid. Expiration time: $expirationTime")
                            }
                        }

//                        Log.e("LOGIN", "googleIdTokenCredential: ${googleIdTokenCredential}")
                        Log.e("LOGIN", "Google ID : ${googleIdTokenCredential.id}")
//                        Log.e("LOGIN", "Google ID Token: ${googleIdTokenCredential.idToken}")
                        Log.e("LOGIN", "Display Name: ${googleIdTokenCredential.displayName}")
                        Log.e("LOGIN", "Data: ${googleIdTokenCredential.data}")
                        Log.e("LOGIN", "Profile picture uri: ${googleIdTokenCredential.profilePictureUri}")

                        return googleIdTokenCredential
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("LOGIN", "Received an invalid google id token response $e")
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("LOGIN", "Unexpected type of credential 1")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("LOGIN", "Unexpected type of credential 2")
            }
        }
        return null
    }

    suspend fun signOut(context: Context) {
        if (::credentialManager.isInitialized.not()) {
            credentialManager = CredentialManager
                .create(context)
        }
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    fun getGoogleIdTokenExpiration(idToken: String): Long? {
        return try {
            // JWT 구조: header.payload.signature
            val payload = idToken.split(".")[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadJson = JSONObject(String(decodedBytes, StandardCharsets.UTF_8))

            // `exp` 값 가져오기 (Unix Timestamp)
            payloadJson.optLong("exp", 0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}