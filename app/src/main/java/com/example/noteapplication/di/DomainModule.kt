package com.example.noteapplication.di

import com.example.noteapplication.domain.useCase.AddNote
import com.example.noteapplication.domain.useCase.ClearDbUseCase
import com.example.noteapplication.domain.useCase.DeleteNote
import com.example.noteapplication.domain.useCase.GetAllNotes
import com.example.noteapplication.domain.useCase.GetAndSaveNotesUseCase
import com.example.noteapplication.domain.useCase.GetNote
import com.example.noteapplication.domain.useCase.GoogleSignInUseCase
import com.example.noteapplication.domain.useCase.LoginWithEmailUseCase
import com.example.noteapplication.domain.useCase.NoteUseCases
import com.example.noteapplication.domain.useCase.RegisterUseCase
import com.example.noteapplication.domain.useCase.UploadMediaUseCase
import com.example.noteapplication.domain.useCase.UploadNoteUseCase
import com.example.noteapplication.domain.useCase.UploadNotesUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * this is the container to define how repository dependencies are provided across the app
 * */
val domainModule = module {
    single { RegisterUseCase( androidApplication(), get()) }

    single { LoginWithEmailUseCase(get(), androidApplication()) }

    single { GoogleSignInUseCase(androidApplication()) }

    single { GetAndSaveNotesUseCase(get()) }

    single { GetAllNotes(get()) }

    single { DeleteNote(get()) }

    single { GetNote(get()) }

    single { AddNote(get()) }

    single { UploadMediaUseCase(get()) }

    single { UploadNoteUseCase(get()) }

    single { UploadNotesUseCase(get()) }

    single { ClearDbUseCase(get()) }

    // Provide a single instance of NoteUseCases (dependency: AddNote)
    single {
        NoteUseCases(
            registerUseCase = get(),
            loginWithEmailUseCase = get(),
            googleSignInUseCase = get(),
            getAndSaveNotesUseCase = get(),
            getAllNotes = get(),
            deleteNote = get(),
            getNote = get(),
            addNote = get(),
            uploadMediaUseCase = get(),
            uploadNoteUseCase = get(),
            uploadNotesUseCase = get(),
            clearDbUseCase = get(),
        )
    }
}
