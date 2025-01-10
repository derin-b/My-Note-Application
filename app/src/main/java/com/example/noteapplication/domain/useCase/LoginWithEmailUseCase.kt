package com.example.noteapplication.domain.useCase

import android.content.Context
import com.example.noteapplication.utils.Utils
import com.google.firebase.auth.FirebaseAuth

class LoginWithEmailUseCase(private val auth: FirebaseAuth, private val context: Context) {
    fun execute(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (Utils.isConnected(context)) {
            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnSuccessListener {
                    onSuccess() // Invoke success callback
                }
                .addOnFailureListener {
                    onFailure(it.message ?: "Login failed") // Invoke failure callback
                }
        } else {
            onFailure("No Internet Connection")
        }
    }
}