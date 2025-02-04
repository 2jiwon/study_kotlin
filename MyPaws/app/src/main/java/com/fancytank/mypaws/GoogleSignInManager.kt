package com.fancytank.mypaws

import android.content.Context
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 *
 * https://proandroiddev.com/google-sign-in-with-credential-manager-c54762376170
 */
object GoogleSignInManager {

    private lateinit var credentialManager: CredentialManager

    suspend fun googleSignIn(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
        doOnSuccess: (String) -> Unit,
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
        doOnSuccess: (String) -> Unit,
        doOnError: (Exception) -> Unit,
    ) {
        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context,
            )
            val displayName = handleCredentials(result.credential)
            displayName?.let {
                doOnSuccess(displayName)
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

    private fun handleCredentials(credential: Credential): String? {
        Log.e("LOGIN credential :: ", credential.toString())

        when (credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        Log.e("LOGIN", "googleIdTokenCredential: ${googleIdTokenCredential}")
                        Log.e("LOGIN", "Google ID Token: ${googleIdTokenCredential.id}")
                        Log.e("LOGIN", "Display Name: ${googleIdTokenCredential.displayName}")
                        Log.e("LOGIN", "Email: ${googleIdTokenCredential.id}")

                        return googleIdTokenCredential.displayName
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
}