package com.example.noteapplication.di

import com.example.noteapplication.data.NoteDatabase
import com.example.noteapplication.data.dao.note.NoteDao
import com.example.noteapplication.data.dao.user.UserDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * this is the container to define how database dependencies are provided across the app
 * */
val databaseModule = module {
    // provide a single instance of database
    single<NoteDatabase> {
        NoteDatabase.getInstance(androidApplication())
    }


    // provide a single instance of UserDao
    single<UserDao> {
        val database = get<NoteDatabase>() // retrieve single instance of db provided
        database.userDao() // return UserDao
    }

    // provide a single instance of NoteDao
    single<NoteDao> {
        val database = get<NoteDatabase>() // retrieve single instance of db provided
        database.noteDao() // return NoteDao
    }
}
