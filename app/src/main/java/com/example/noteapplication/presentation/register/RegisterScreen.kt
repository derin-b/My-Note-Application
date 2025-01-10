package com.example.noteapplication.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapplication.R
import com.example.noteapplication.domain.models.RegistrationUIState
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.ui.theme.ArmyGreen
import com.example.noteapplication.utils.Utils
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(noteViewmodel: NoteViewmodel, onLoginClick: () -> Unit, onRegisterClick: () -> Unit){
    val uiState by noteViewmodel.registerUIState.collectAsState()  // Collect Flow
    val isButtonEnabled by noteViewmodel.isRegisterButtonEnabled.collectAsState()
    val isLoading by noteViewmodel.isLoading.collectAsState()


    // launch a side-effect to collect one-time UI events
    LaunchedEffect(Unit) {
        noteViewmodel.registerUIEvent.collectLatest { event ->
            when (event) {
                // handle the navigate to note screen event
                is RegisterScreenUiEvent.RegistrationSuccessful -> { onRegisterClick() }
            }
        }
    }


    Utils.ShowCircularProgress(isLoading)



    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, ArmyGreen),
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
                text = stringResource(R.string.register),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.background)

        }

        RegisterCard(modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
            isButtonEnabled = isButtonEnabled,
            uiState = uiState,
            onLoginClick = onLoginClick,
            noteViewmodel = noteViewmodel
        )
    }
}

@Composable
fun RegisterCard(
    modifier :Modifier = Modifier,
    isButtonEnabled: Boolean,
    uiState: RegistrationUIState,
    onLoginClick: () -> Unit,
    noteViewmodel: NoteViewmodel) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
                .padding(16.dp)
                .verticalScroll(scrollState)){

            RegisterTextFields(uiState = uiState, noteViewmodel = noteViewmodel, showPassword = showPassword)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { noteViewmodel.registrationScreenEvent(RegisterScreenEvent.RegisterButtonClicked) },
                modifier = Modifier
                    .fillMaxWidth() // Ensure it fills its allocated space
                    .height(50.dp),
                //.shadow(8.dp, shape = RoundedCornerShape(8.dp))
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArmyGreen, // Background color
                    contentColor = Color.White   // Text color
                ),
                enabled = isButtonEnabled,

                ) {
                Text(text = stringResource(R.string.register), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val txt = stringResource(R.string.login_in)

            val annotatedString = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append(stringResource(R.string.already_have_an_account))
                }
                append(" ")
                pushStringAnnotation(tag = "LOGIN", annotation = txt) // Add string annotation
                withStyle(SpanStyle(color = ArmyGreen, fontWeight = FontWeight.Bold)) {
                    append(txt)
                }
                pop() // Close the annotation scope
            }

             ClickableText(
                text = annotatedString,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { offset ->
                    // Get the annotation at the clicked offset
                    annotatedString.getStringAnnotations(tag = "LOGIN", start = offset, end = offset).firstOrNull()?.let {
                        onLoginClick()
                    }
                }
            )

        }

    }
}

@Composable
fun RegisterTextFields(uiState: RegistrationUIState, noteViewmodel: NoteViewmodel, showPassword: MutableState<Boolean>){
    val focusManager = LocalFocusManager.current // To control focus movement

    Utils.TextFields(
        value = uiState.firstName,
        onValueChanged = { noteViewmodel.registrationScreenEvent(RegisterScreenEvent.FirstNameTextChange(it)) },
        textLabel = stringResource(R.string.first_name),
        modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
        leadingIcon =  Icons.Default.Person,
        iconTint = ArmyGreen,
        keyboardType = KeyboardType.Text,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) } // Move to the next TextField
        ),
        errorStatus = uiState.firstNameError,
    )

    Utils.TextFields(
        value = uiState.lastName,
        onValueChanged = { noteViewmodel.registrationScreenEvent(RegisterScreenEvent.LastNameTextChange(it)) },
        textLabel = stringResource(R.string.last_name),
        modifier = Modifier.padding(bottom = 16.dp),
        leadingIcon =  Icons.Default.Person,
        iconTint = ArmyGreen,
        keyboardType = KeyboardType.Text,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) } // Move to the next TextField
        ),
        errorStatus = uiState.lastNameError,
    )

    Utils.TextFields(
        value = uiState.email,
        onValueChanged = { noteViewmodel.registrationScreenEvent(RegisterScreenEvent.EmailTextChange(it)) },
        textLabel =  stringResource(R.string.email_address),
        modifier = Modifier.padding(bottom = 16.dp),
        leadingIcon =  Icons.Default.Email,
        iconTint = ArmyGreen,
        keyboardType = KeyboardType.Email,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) } // Move to the next TextField
        ),
        errorStatus = uiState.emailError,
    )

    Utils.TextFields(
        value = uiState.password,
        onValueChanged = { noteViewmodel.registrationScreenEvent(RegisterScreenEvent.PasswordTextChange(it)) },
        textLabel = stringResource(R.string.password),
        leadingIcon =  Icons.Default.Lock,
        iconTint = ArmyGreen,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Utils.TrailingPasswordIcon(
                showPassword = showPassword,
                visibleIcon = R.drawable.ic_visibility,
                hiddenIcon = R.drawable.ic_visibility_off,
                iconTint = ArmyGreen
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