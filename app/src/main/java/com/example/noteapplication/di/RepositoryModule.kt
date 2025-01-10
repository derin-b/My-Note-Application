package com.example.noteapplication.di

import com.example.noteapplication.data.repository.note.NoteRepoImpl
import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.data.repository.user.UserRepoImpl
import com.example.noteapplication.data.repository.user.UserRepository
import org.koin.dsl.module

/**
 * this is the container to define how repository dependencies are provided across the app
 * */
val repositoryModule = module {
    // provide a single instance of User Repo data type
    single<UserRepository> {
        UserRepoImpl(get())
    }

    // provide a single instance of Note Repo data type
    single<NoteRepository> {
        NoteRepoImpl(get(), get(), get(), get())
    }
}
