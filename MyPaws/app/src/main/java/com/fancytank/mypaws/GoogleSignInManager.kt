package com.fancytank.mypaws

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
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
        when (credential) {

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        return googleIdTokenCredential.displayName
                    } catch (e: GoogleIdTokenParsingException) {
                        println("Received an invalid google id token response $e")
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    println("Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                println("Unexpected type of credential")
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