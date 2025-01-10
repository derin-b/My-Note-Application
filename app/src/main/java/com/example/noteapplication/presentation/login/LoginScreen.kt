package com.example.noteapplication.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapplication.R
import com.example.noteapplication.domain.models.LoginUIState
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.presentation.register.RegisterScreenUiEvent
import com.example.noteapplication.ui.theme.Green
import com.example.noteapplication.utils.Utils
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(onLoginClick: () -> Unit, noteViewmodel: NoteViewmodel){
    val uiState by noteViewmodel.loginUIState.collectAsState() // Collect Flow
    val isButtonEnabled by noteViewmodel.isLoginButtonEnabled.collectAsState()
    val isLoading by noteViewmodel.isLoading.collectAsState()


    // launch a side-effect to collect one-time UI events
    LaunchedEffect(Unit) {
        noteViewmodel.loginUIEvent.collectLatest { event ->
            when (event) {
                // handle the navigate to note screen event
                is LoginScreenUiEvent.LoginSuccessful -> { onLoginClick() }
            }
        }
    }


    Utils.ShowCircularProgress(isLoading)


    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Green),
                startY = Float.POSITIVE_INFINITY,
                endY = 0f
            )
        )){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)) {
            Utils.HeaderText(
                text = stringResource(id = R.string.login_in),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.background)

        }
        LoginCard(modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
            isButtonEnabled = isButtonEnabled,
            uiState = uiState,
            noteViewmodel = noteViewmodel
        )
    }
}

@Composable
fun LoginCard(modifier :Modifier = Modifier,isButtonEnabled: Boolean, uiState: LoginUIState, noteViewmodel: NoteViewmodel) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    Card(modifier = modifier,
        shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,  // Background color
        )
    ) {
        Column(horizontalAlignment = Alignment.Start, // Aligns content horizontally to the center
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){

            LoginTextFields(
               uiState = uiState,
                showPassword = showPassword,
                noteViewmodel = noteViewmodel
            )

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = { noteViewmodel.loginScreenEvent(LoginScreenEvent.LoginButtonClicked) },
                modifier = Modifier
                    .fillMaxWidth() // Ensure it fills its allocated space
                    .height(50.dp),
                //.shadow(8.dp, shape = RoundedCornerShape(8.dp))
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green, // Background color
                    contentColor = Color.White   // Text color
                ),
                enabled = isButtonEnabled

                ) {
                Text(text = stringResource(id = R.string.login_in), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))

            GoogleLoginDisplay(text = stringResource(R.string.or_login_in_with), noteViewmodel = noteViewmodel)
        }

    }
}

@Composable
fun LoginTextFields(
    uiState: LoginUIState,
    showPassword: MutableState<Boolean>,
    noteViewmodel: NoteViewmodel){
    val focusManager = LocalFocusManager.current // To control focus movement

    Utils.TextFields(
        value = uiState.email,
        onValueChanged = {
            noteViewmodel.loginScreenEvent(LoginScreenEvent.EmailTextChange(it))
                         },
        textLabel = stringResource(R.string.email_address),
        modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
        leadingIcon =  Icons.Default.Email,
        iconTint = Green,
        keyboardType = KeyboardType.Email,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) } // Move to the next TextField
        ),
        errorStatus = uiState.emailError,
    )

    Utils.TextFields(
        value = uiState.password,
        onValueChanged = {
            noteViewmodel.loginScreenEvent(LoginScreenEvent.PasswordTextChange(it))
        },
        textLabel = stringResource(R.string.password),
        leadingIcon =  Icons.Default.Lock,
        iconTint = Green,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Utils.TrailingPasswordIcon(
                showPassword = showPassword,
                visibleIcon = R.drawable.ic_visibility,
                hiddenIcon = R.drawable.ic_visibility_off,
                iconTint = Green,

            )
        },
        imeAction = ImeAction.Done // Show "Done" action
        ,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() } // Close the keyboard
        ),
        errorStatus = uiState.passwordError,
    )
}

@Composable
fun GoogleLoginDisplay(
    text: String,
    modifier: Modifier = Modifier,
    noteViewmodel: NoteViewmodel
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Line on the left side
            Divider(
                modifier = Modifier
                    .weight(1f) // Take up equal space
                    .height(1.dp),
                color = Color.Gray
            )

            // Text in the center
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            // Line on the right side
            Divider(
                modifier = Modifier
                    .weight(1f) // Take up equal space
                    .height(1.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { noteViewmodel.loginScreenEvent(LoginScreenEvent.GoogleLogin) },
            modifier = Modifier
                .fillMaxWidth() // Ensure it fills its allocated space
                .height(50.dp)
                .border(1.dp, Green, RoundedCornerShape(10.dp)), // Add border ,
            //.shadow(8.dp, shape = RoundedCornerShape(8.dp))
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Background color
                contentColor = Color.Black   // Text color
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google), // Replace with your image resource
                    contentDescription = stringResource(R.string.login_with_google),
                    modifier = Modifier
                        .size(30.dp) // Size of the image
                )
                Spacer(modifier = Modifier.width(30.dp))

                Utils.ButtonText(
                    text = stringResource(R.string.login_with_google),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
