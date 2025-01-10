package com.example.noteapplication.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.domain.models.LoginUIState
import com.example.noteapplication.domain.models.NoteDetailUIState
import com.example.noteapplication.domain.models.RegistrationUIState
import com.example.noteapplication.domain.useCase.NoteUseCases
import com.example.noteapplication.presentation.login.LoginScreenEvent
import com.example.noteapplication.presentation.login.LoginScreenUiEvent
import com.example.noteapplication.presentation.noteDetail.NoteDetailEvent
import com.example.noteapplication.presentation.noteDetail.NoteDetailUiEvent
import com.example.noteapplication.presentation.notes.NoteScreenUIEvent
import com.example.noteapplication.presentation.notes.NotesScreenEvent
import com.example.noteapplication.presentation.register.RegisterScreenEvent
import com.example.noteapplication.presentation.register.RegisterScreenUiEvent
import com.example.noteapplication.utils.Constants
import com.example.noteapplication.utils.Results
import com.example.noteapplication.utils.TextValidator
import com.example.noteapplication.utils.Utils
import com.example.noteapplication.utils.Utils.longToastShow
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoteViewmodel(
    private val application: Application,
    private val noteUseCases: NoteUseCases,
    ): ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    private val _startDestination = MutableStateFlow(Constants.NavGraphs.LOGIN_SCREENS)
    val startDestination: StateFlow<String> = _startDestination

    // mutableStateFlow to show circular progress bar
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginUIState = MutableStateFlow(LoginUIState())
    val loginUIState: StateFlow<LoginUIState> = _loginUIState

    private val _loginUIEvent = MutableSharedFlow<LoginScreenUiEvent>()
    val loginUIEvent = _loginUIEvent.asSharedFlow()

    private val _registerUIState = MutableStateFlow(RegistrationUIState())
    val registerUIState: StateFlow<RegistrationUIState> = _registerUIState

    private val _registerUIEvent = MutableSharedFlow<RegisterScreenUiEvent>()
    val registerUIEvent = _registerUIEvent.asSharedFlow()

    // mutableStateFlow to enable or disable login button
    private val _isLoginButtonEnabled = MutableStateFlow(false)
    val isLoginButtonEnabled: StateFlow<Boolean> = _isLoginButtonEnabled

    // mutableStateFlow to enable or disable register button
    private val _isRegisterButtonEnabled = MutableStateFlow(false)
    val isRegisterButtonEnabled: StateFlow<Boolean> = _isRegisterButtonEnabled

    private val _notesUIEvent = MutableSharedFlow<NoteScreenUIEvent>()
    val notesUIEvent = _notesUIEvent.asSharedFlow()

    // mutableStateFlow for Flow-based state
    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())
    val notesFlow: StateFlow<List<Note>> = _notesFlow

    // mutableState for searchText and category
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _category = MutableStateFlow(Constants.NoteCategory.ALL)

    // mutableSharedFlow to hold one-time UI events
    private val _uiEvent = MutableSharedFlow<NoteDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // list of categories
    val categories = listOf("Important", "Work", "Reading")


    private val _noteDetailUIState = MutableStateFlow(NoteDetailUIState())
    val noteDetailUIState: StateFlow<NoteDetailUIState> = _noteDetailUIState

    // mutableStateFlow to show image
    private val _selectedImageUri = MutableStateFlow<Pair<Uri?, String?>>(null to null)
    val selectedImageUri: StateFlow<Pair<Uri?, String?>> = _selectedImageUri

    // mutableStateFlow to show video
    private val _selectedVideoUri = MutableStateFlow<Pair<Uri?, String?>>(null to null)
    val selectedVideoUri: StateFlow<Pair<Uri?, String?>> = _selectedVideoUri

    init {
        checkStartDestination()
    }

    /**
     * Determines the initial navigation destination of the app based on the user's authentication status.
     */
    private fun checkStartDestination() {
        // Check if there is a currently authenticated user
        if (auth.currentUser == null) {
            // No authenticated user: set the start destination to the login screens
            _startDestination.value = Constants.NavGraphs.LOGIN_SCREENS
        } else {
            // Authenticated user exists: set the start destination to the note screens
            _startDestination.value = Constants.NavGraphs.NOTE_SCREENS
        }
    }

    // updates the state of the login button
    private fun updateLoginButtonState() {
        _isLoginButtonEnabled.value = _loginUIState.value.password.isNotBlank() && _loginUIState.value.email.isNotBlank() &&
            _loginUIState.value.emailError.not() && _loginUIState.value.passwordError.not()
    }

    /**
     * updates the register button state, enabling it only
     * if all fields are valid and non-blank
    */
    private fun updateRegisterButtonState() {
        _isRegisterButtonEnabled.value = listOf(
            _registerUIState.value.emailError,
            _registerUIState.value.firstNameError,
            _registerUIState.value.lastNameError,
            _registerUIState.value.passwordError
        ).none { it } && listOf(
            _registerUIState.value.email,
            _registerUIState.value.firstName,
            _registerUIState.value.lastName,
            _registerUIState.value.password
        ).all { it.isNotBlank() }
    }

    // function to emit registration successful even into the SharedFlow
    private fun emitRegistrationEvent(event: RegisterScreenUiEvent) {
        viewModelScope.launch {
            // emit the event into the flow
            _registerUIEvent.emit(event)
        }
    }

    // function to emit login successful events into the SharedFlow
    private fun emitLoginSuccessfulEvent(event: LoginScreenUiEvent) {
        viewModelScope.launch {
            // emit the event into the flow
            _loginUIEvent.emit(event)
        }
    }

    // function to emit login successful events into the SharedFlow
    private fun emitFinishActivityEvent(event: NoteScreenUIEvent) {
        viewModelScope.launch {
            // emit the event into the flow
            _notesUIEvent.emit(event)
        }
    }

    // function to update the current note id
    fun updateNoteId(noteId: String) {
        _noteDetailUIState.value = _noteDetailUIState.value.copy(
            noteId = noteId.ifBlank { Utils.generateNoteId(auth.currentUser?.uid ?: "") }
        )
    }

    // function to fetch and update the list of notes based on search and category
    fun fetchNotes() {
        // call the execute function in a coroutine scope
        viewModelScope.launch {
            noteUseCases.getAllNotes.execute(_searchText.value, _category.value).collectLatest { notesList ->
                // update the notes list
                _notesFlow.value = notesList
            }
        }
    }

    fun getNoteDetail(noteId: String) {
        if (noteId.isNotBlank()) {
            viewModelScope.launch {
                noteUseCases.getNote(noteId).collect { note ->
                    // check if the note is null
                    if (note != null) {
                        // if title is blank, use a default value ""
                        _noteDetailUIState.value = _noteDetailUIState.value.copy(
                            title = note.title.ifBlank { " " },
                            description = note.description.ifBlank { " " },
                            selectedCategory = note.noteCategory,
                            mediaId = note.mediaId,
                        )
                        _selectedImageUri.value = _selectedImageUri.value.copy(second = note.mediaId)
                        updateMediaUri(note.mediaList)
                        updateDownloadString(note.mediaId)

                    } else {
                        _noteDetailUIState.value = NoteDetailUIState()
                        _selectedImageUri.value = null to null
                        _selectedVideoUri.value = null to null

                    }
                }
            }
        } else {
            _noteDetailUIState.value = NoteDetailUIState()
            _selectedImageUri.value = null to null
            _selectedVideoUri.value = null to null
        }
    }

    private fun updateMediaUri(mediaString: String){
        if (mediaString.isNotBlank()) {
            val mediaList = Utils.getListFromString(mediaString)

            // update selected image URI
            _selectedImageUri.value = _selectedImageUri.value.copy(
                first = mediaList
                    .find { it.type == Constants.MediaTypes.IMAGES }
                    ?.uri
                    ?.let { Uri.parse(it) }
            )


            // update selected video URI
            _selectedVideoUri.value = _selectedVideoUri.value.copy(
                first = mediaList
                    .find { it.type == Constants.MediaTypes.VIDEOS }
                    ?.uri
                    ?.let { Uri.parse(it) }
            )

            /*_selectedVideoUri.value = mediaList
                .find { it.type == Constants.MediaTypes.VIDEOS }
                ?.uri
                ?.let { Uri.parse(it) to null } ?: (null to null)*/
        } else {
            // clear both image and video URIs
            _selectedImageUri.value = _selectedImageUri.value.copy(first = null)
            _selectedVideoUri.value = _selectedVideoUri.value.copy(first = null)
        }

    }

    private fun updateDownloadString(mediaId: String){
        if (mediaId.isNotBlank()) {
            val urlList = mediaId.split(",")

            _selectedImageUri.value = _selectedImageUri.value.copy(
                second = urlList.find { it.contains(".jpg", ignoreCase = true) }
            )
            _selectedVideoUri.value = _selectedVideoUri.value.copy(
                second = urlList.find { it.contains(".mp4", ignoreCase = true) }
            )
        } else {
            _selectedImageUri.value = _selectedImageUri.value.copy(second = null)
            _selectedVideoUri.value = _selectedVideoUri.value.copy(second = null)
        }


    }

    // function to emit events into the SharedFlow
    private fun emitEvent(event: NoteDetailUiEvent) {
        _noteDetailUIState.value = NoteDetailUIState()
        viewModelScope.launch {
            // emit the event into the flow
            _uiEvent.emit(event)
        }
    }

    /**
     * Handles the actions to be performed after a successful login.
     * @param onSuccess A callback function to execute when the operation is successful.
     */
    private fun handleLoginSuccess(onSuccess: () -> Unit) {
        // launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            // set the loading state to true to indicate that a background operation is in progress
            _isLoading.value = true

            // execute the use case to fetch and save notes from Firebase
            when (val results = noteUseCases.getAndSaveNotesUseCase.execute()) {
                // if the operation is successful, invoke the provided success callback
                is Results.Success -> onSuccess()

                // if the operation fails, show a long toast message with the error details
                is Results.Failure -> longToastShow(
                    results.exception.message.orEmpty(),
                    application.applicationContext
                )
            }

            // set the loading state to false to indicate that the background operation has completed
            _isLoading.value = false
        }
    }


    /** Handles user events on the registration screen. */
    fun registrationScreenEvent(event: RegisterScreenEvent){
        when(event) {
            // handles email text changes
            is RegisterScreenEvent.EmailTextChange -> {
                // update the email field and validate the input
                _registerUIState.value = _registerUIState.value.copy(
                    email = event.email,
                    emailError = !TextValidator.validateEmail(event.email) // check if the email is valid
                )
                // update button state based on validation
                updateRegisterButtonState()
            }
            // handles first name text changes
            is RegisterScreenEvent.FirstNameTextChange -> {
                // update the first name field and validate the input
                _registerUIState.value = _registerUIState.value.copy(
                    firstName = event.firstName,
                    // check if the first name is valid
                    firstNameError = !TextValidator.validateFirstName(event.firstName)
                )
                // update button state based on validation
                updateRegisterButtonState()
            }

            // handles last name text changes
            is RegisterScreenEvent.LastNameTextChange -> {
                // update the last name field and validate the input
                _registerUIState.value = _registerUIState.value.copy(
                    lastName = event.lastName,
                    // check if the last name is valid
                    lastNameError = !TextValidator.validateLastName(event.lastName)
                )
                // update button state based on validation
                updateRegisterButtonState()
            }

            // handles password text changes
            is RegisterScreenEvent.PasswordTextChange -> {
                // update the password field and validate the input
                _registerUIState.value = _registerUIState.value.copy(
                    password = event.password,
                    // check if the password is valid
                    passwordError = !TextValidator.validatePassword(event.password)
                )
                // update button state based on validation
                updateRegisterButtonState()
            }

            // handles the registration button click event
            RegisterScreenEvent.RegisterButtonClicked -> {
                viewModelScope.launch {
                    _isLoading.value = true
                    noteUseCases.registerUseCase.register(
                        email = _registerUIState.value.email,
                        password = _registerUIState.value.password,
                        firstName = _registerUIState.value.firstName,
                        onSuccess = {
                            _isLoading.value = false
                            handleLoginSuccess {
                                emitRegistrationEvent(RegisterScreenUiEvent.RegistrationSuccessful)
                            }
                        },
                        onFailure = { error ->
                            _isLoading.value = false
                            longToastShow(
                                "Registration failed: $error",
                                application.applicationContext
                            )

                        }
                    )
                }
            }
        }
    }


    // handles various events triggered on the Login Screen.
    fun loginScreenEvent(event: LoginScreenEvent) {
        when (event) {
            // handle email text change event
            is LoginScreenEvent.EmailTextChange -> {
                _loginUIState.value = _loginUIState.value.copy(
                    email = event.email,
                    // validate the email and update the error state
                    emailError = !TextValidator.validateEmail(event.email)
                )
                // update the login button's enabled state based on current input
                updateLoginButtonState()
            }

            // handle password text change event
            is LoginScreenEvent.PasswordTextChange -> {
                _loginUIState.value = _loginUIState.value.copy(
                    password = event.password,
                    // validate the password and update the error state
                    passwordError = !TextValidator.validatePassword(event.password)
                )
                // update the login button's enabled state based on current input
                updateLoginButtonState()
            }

            // handle login button click event
            LoginScreenEvent.LoginButtonClicked -> {
                _isLoading.value = true
                noteUseCases.loginWithEmailUseCase.execute(
                    email = _loginUIState.value.email,
                    password = _loginUIState.value.password,
                    onSuccess = {
                        _isLoading.value = false
                        handleLoginSuccess { emitLoginSuccessfulEvent(LoginScreenUiEvent.LoginSuccessful) } },
                    onFailure = {
                        _isLoading.value = false
                        longToastShow(it, application.applicationContext)
                    }
                )
            }

            // handle Google login button click event
            LoginScreenEvent.GoogleLogin -> {
                _isLoading.value = true
                viewModelScope.launch {
                    noteUseCases.googleSignInUseCase.signIn(
                        onSuccess = {
                            _isLoading.value = false
                            handleLoginSuccess { emitLoginSuccessfulEvent(LoginScreenUiEvent.LoginSuccessful) }

                        },
                        onFailure = { error ->
                            _isLoading.value = false
                            longToastShow("Sign-in failed: $error", application.applicationContext)
                        }
                    )
                }
            }
        }
    }


    fun noteScreenEvent(event: NotesScreenEvent) {
        when (event) {
            is NotesScreenEvent.CategoryChange -> {
                // update the current category based on the selected index
                _category.value = when (event.categoryIndex) {
                    0 -> Constants.NoteCategory.ALL
                    1 -> Constants.NoteCategory.WORK
                    2 -> Constants.NoteCategory.READING
                    3 -> Constants.NoteCategory.IMPORTANT
                    else -> Constants.NoteCategory.ALL // default to ALL in case of invalid index
                }
            }

            is NotesScreenEvent.SearchTextChange -> {
                // update the current search text value
                _searchText.value = event.text
            }

            is NotesScreenEvent.OnDeleteClicked -> {
                // delete the note with the provided ID using the note use cases
                if (Utils.isConnected(application.applicationContext)) {
                    viewModelScope.launch {
                        noteUseCases.deleteNote(event.noteId)
                    }
                } else{
                    longToastShow("Error deleting note", application.applicationContext)

                }
            }

            is NotesScreenEvent.OnNoteFiltersChange -> {
                // re-fetch notes to apply the new filters
                fetchNotes()
            }

            is NotesScreenEvent.LogOut -> {
                _isLoading.value = true
                // check if there is internet connection
                if (Utils.isConnected(application.applicationContext)) {
                    viewModelScope.launch {
                        when (val result = noteUseCases.uploadNotesUseCase.execute()) {
                            is Results.Success -> {
                                // clear note data from db
                                noteUseCases.clearDbUseCase.execute()
                                // sign out the user from Firebase Authentication
                                auth.signOut()
                                _isLoading.value = true
                                // emit a UI event to finish the activity
                                emitFinishActivityEvent(NoteScreenUIEvent.FinishActivity)
                            }

                            is Results.Failure -> {
                                _isLoading.value = true
                                // handle failure, show error message
                                longToastShow(
                                    "Error uploading note: ${result.exception.message}",
                                    application.applicationContext)

                            }
                        }
                    }
                } else {
                    _isLoading.value = true
                    longToastShow(
                        "Check internet connection",
                        application.applicationContext
                    )
                }

            }
        }
    }


    /**
     * Handles different events related to note details.
     */
    fun noteDetailEvent(event: NoteDetailEvent) {
        when (event) {
            // handles when the user enters or updates the note's title
            is NoteDetailEvent.EnteredTitle -> {
                // update the title state with the provided title
                _noteDetailUIState.value = _noteDetailUIState.value.copy(
                    title = event.title
                )
            }

            // handles when the user enters or updates the note's description
            is NoteDetailEvent.EnteredDescription -> {
                // update the description state with the provided description
                _noteDetailUIState.value = _noteDetailUIState.value.copy(
                    description = event.description,
                )
            }

            is NoteDetailEvent.Category -> {
                _noteDetailUIState.value = _noteDetailUIState.value.copy(
                    selectedCategory = event.category,
                )
            }

            is NoteDetailEvent.OnMediaSelected -> {
                // handle the OnMediaSelected event and update the mediaList
                if (event.media.type == Constants.MediaTypes.IMAGES){
                    _selectedImageUri.value = Uri.parse(event.media.uri) to null
                } else {
                    _selectedVideoUri.value = Uri.parse(event.media.uri) to null
                }
                _noteDetailUIState.value = _noteDetailUIState.value.copy(
                    mediaList = _noteDetailUIState.value.mediaList
                        .filter { it.type != event.media.type } // remove the media with the matching type
                        .plus(event.media) // add the new media to the list
                )
            }

            is NoteDetailEvent.OnMediaRemoved -> {
                if (event.mediaType == Constants.MediaTypes.IMAGES){
                    _selectedImageUri.value = null to null
                } else {
                    _selectedVideoUri.value = null to null
                }
                _noteDetailUIState.value = _noteDetailUIState.value.copy(
                    mediaList = _noteDetailUIState.value.mediaList
                        .filter { it.type != event.mediaType } // remove the media with the matching type
                )
            }

            // handles when the user triggers the "Save Note" action
            is NoteDetailEvent.SaveNote -> {
                viewModelScope.launch {
                    // early return if both title and description are blank
                    if (_noteDetailUIState.value.title.isBlank() && _noteDetailUIState.value.description.isBlank()) {
                        return@launch
                    }

                    // create a new note with provided or default values
                    val newNote = Utils.createNewNote(
                        noteDetail = _noteDetailUIState.value, userId = auth.currentUser?.uid?:"")

                    // check network connectivity and handle the upload accordingly
                    if (Utils.isConnected(application.applicationContext)) {
                        if (_noteDetailUIState.value.mediaList.isNotEmpty()){
                            handleMediaUpload(newNote)
                        } else {
                            newNote.apply { mediaId = "" }
                            uploadNote(newNote)
                        }

                    } else {
                        // if no internet connection, just save the note locally
                        if (_noteDetailUIState.value.mediaList.isNotEmpty()) {
                            noteUseCases.addNote(newNote)
                        } else {
                            newNote.apply { mediaId = "" }
                            noteUseCases.addNote(newNote)
                        }
                        _noteDetailUIState.value = NoteDetailUIState()
                        emitEvent(NoteDetailUiEvent.SaveNote)
                    }
                }
            }
        }
    }

    // function to handle the media upload process
    private suspend fun handleMediaUpload(newNote: Note) {
        // execute the media upload and handle success or failure
        when (val uploadMedia = noteUseCases.uploadMediaUseCase.execute(newNote)) {
            // success case: Media uploaded successfully
            is Results.Success -> {
                // show a success message with the media URLs and update the note's mediaId
                longToastShow("${uploadMedia.data}", application.applicationContext)
                // join the media URLs into a comma-separated string and update the note's mediaId
                newNote.mediaId = uploadMedia.data.joinToString(",")
                // after media is uploaded successfully, upload the note
                uploadNote(newNote)
            }
            // failure case: Media upload failed
            is Results.Failure -> {
                println("Media Upload${uploadMedia.exception.message}")
                // show a toast with the error message from the media upload failure
                longToastShow("Error uploading media: ${uploadMedia.exception.message}", application.applicationContext)
            }
        }
    }

    // function to upload the note after the media upload is complete
    private suspend fun uploadNote(newNote: Note) {
        // execute the note upload and handle success or failure
        when (val result = noteUseCases.uploadNoteUseCase.execute(newNote)) {
            // success case: Note uploaded successfully
            is Results.Success -> {
                // after successful upload, set syncFlag to 1 and save the note locally
                noteUseCases.addNote(newNote.apply { syncFlag = 1 })
                // remove the uris
                _selectedVideoUri.value = null to null
                _selectedImageUri.value = null to null
                _noteDetailUIState.value = NoteDetailUIState()
                // emit event to indicate that the note has been saved
                emitEvent(NoteDetailUiEvent.SaveNote)
            }
            // failure case: Note upload failed
            is Results.Failure -> {
                // show a toast with the error message from the note upload failure
                longToastShow("Error uploading note: ${result.exception.message}", application.applicationContext)
            }
        }
    }


}