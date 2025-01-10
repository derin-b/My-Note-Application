package com.example.noteapplication.presentation.login


sealed class LoginScreenEvent{
    data class EmailTextChange(val email: String): LoginScreenEvent()
    data class PasswordTextChange(val password: String): LoginScreenEvent()
    data object LoginButtonClicked: LoginScreenEvent()
    data object GoogleLogin: LoginScreenEvent()
}

sealed class LoginScreenUiEvent {
    data object LoginSuccessful: LoginScreenUiEvent()
}


