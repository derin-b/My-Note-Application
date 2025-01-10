package com.example.noteapplication.domain.useCase

data class NoteUseCases(
    val registerUseCase: RegisterUseCase,
    val loginWithEmailUseCase: LoginWithEmailUseCase,
    val googleSignInUseCase: GoogleSignInUseCase,
    val getAndSaveNotesUseCase: GetAndSaveNotesUseCase,
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val getNote: GetNote,
    val addNote: AddNote,
    val uploadMediaUseCase: UploadMediaUseCase,
    val uploadNoteUseCase: UploadNoteUseCase,
    val uploadNotesUseCase: UploadNotesUseCase,
    val clearDbUseCase: ClearDbUseCase,
)
