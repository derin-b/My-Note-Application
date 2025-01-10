package com.example.noteapplication.domain.useCase

import android.content.Context
import com.example.noteapplication.utils.GoogleSignIn
import com.example.noteapplication.utils.Utils

class GoogleSignInUseCase(private val context: Context) {
    suspend fun signIn(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Utils.isConnected(context)) {
            try {
                GoogleSignIn(context).signIn(onSuccess = {
                    onSuccess() // Sign-in was successful, invoke onSuccess callback
                }, onFailure = { error ->
                    onFailure(error) // Handle failure, invoke onFailure callback
                })
            } catch (e: Exception) {
                onFailure(e.message ?: "Unknown error occurred")
            }
        } else {
            onFailure("No Internet Connection")
        }
    }
}