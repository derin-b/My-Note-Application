package com.example.noteapplication.utils

object TextValidator {
    fun validateFirstName(firstName:String):Boolean{
        return (firstName.isNotBlank() && firstName.length > 2)
    }

    fun validateLastName(lastName:String):Boolean {
        return (lastName.isNotBlank() && lastName.length > 2)
    }

    fun validateEmail(email:String): Boolean{
        return (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun validatePassword(password:String): Boolean{
        return (password.isNotBlank() && password.length >= 4)
    }
}

data class ValidationStatus(
    val  status: Boolean = false
)