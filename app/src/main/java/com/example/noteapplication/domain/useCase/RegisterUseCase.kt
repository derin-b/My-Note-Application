package com.example.noteapplication.domain.useCase

import android.content.Context
import com.example.noteapplication.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterUseCase(private val context: Context, private val auth: FirebaseAuth) {
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Utils.isConnected(context)) {
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnSuccessListener {
                        val user = auth.currentUser!!
                        val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName.trim())
                            .build()

                        user.updateProfile(profileUpdate)
                            .addOnSuccessListener {
                                // reload the user to fetch the updated profile
                                user.reload().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val updatedUser = auth.currentUser
                                        val displayName = updatedUser?.displayName
                                        onSuccess()  // Registration successful
                                    } else {
                                        onFailure("Failed to reload user profile")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                onFailure(it.message ?: "Failed to update profile")
                            }
                    }
                    .addOnFailureListener {
                        onFailure(it.message ?: "Registration failed")
                    }
            } catch (e: Exception) {
                onFailure(e.message ?: "Unknown error occurred")
            }
        } else {
            onFailure("No Internet Connection")
        }
    }
}
