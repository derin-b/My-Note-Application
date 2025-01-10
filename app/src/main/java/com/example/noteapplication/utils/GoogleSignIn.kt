package com.example.noteapplication.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.noteapplication.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

/**
 * A class to handle Google Sign-In functionality using Credential Manager and Firebase Authentication.
 *
 * @param context The application or activity context required for Credential Manager and Firebase operations.
 */
class GoogleSignIn(private val context: Context) {
    // credentialManager instance to manage sign-in credentials
    private val credentialManager = CredentialManager.create(context)
    // firebaseAuth instance to handle authentication
    private val auth = FirebaseAuth.getInstance()

    /**
     * Initiates the Google Sign-In process.
     *
     * @param onSuccess Callback function to be invoked on successful sign-in.
     * @param onFailure Callback function to be invoked if sign-in fails, with an error message.
     */
    suspend fun signIn(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            // build and fetch credentials using Credential Manager
            val result = buildCredentialRequest()
            val credential = result.credential

            // check if the retrieved credential is a valid Google ID Token
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // parse the Google ID Token and authenticate with Firebase
                    val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()

                    // notify success or failure based on the authentication result
                    if (authResult != null) {
                        onSuccess()
                    } else {
                        onFailure("Authentication result is null")
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    // handle exceptions specific to Google ID Token parsing
                    onFailure("GoogleIdTokenParsingException: ${e.message}")
                }
            } else {
                // handle invalid credential type
                onFailure("Invalid credential type")
            }
        } catch (e: Exception) {
            // handle general exceptions and notify failure
            e.printStackTrace()
            if (e is CancellationException) throw e
            onFailure(e.message ?: "Unknown error occurred")
        }
    }

    // builds a request for fetching Google Sign-In credentials
    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val googleIdOption = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false) // allow any Google account, not just previously authorized ones
                    .setServerClientId(context.getString(R.string.web_client_id)) // set the server's client ID for the Google Sign-In
                    .setAutoSelectEnabled(false) // disable auto-selection of accounts
                    .build()
            ).build()

        // fetch the credential using the Credential Manager
        return credentialManager.getCredential(context = context, request = googleIdOption)
    }
}

