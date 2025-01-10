package com.example.noteapplication.presentation.register


sealed class RegisterScreenEvent{
    data class FirstNameTextChange(val firstName: String): RegisterScreenEvent()
    data class LastNameTextChange(val lastName: String): RegisterScreenEvent()
    data class EmailTextChange(val email: String): RegisterScreenEvent()
    data class PasswordTextChange(val password: String): RegisterScreenEvent()
    data object RegisterButtonClicked: RegisterScreenEvent()
}

sealed class RegisterScreenUiEvent {
    data object RegistrationSuccessful: RegisterScreenUiEvent()
}



