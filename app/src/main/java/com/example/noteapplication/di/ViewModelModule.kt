package com.example.noteapplication.di

import com.example.noteapplication.presentation.NoteViewmodel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * this is the container to define how viewmodel dependencies are provided across the app
 * */

val viewmodelModule = module {
    // provide view model instance of NoteViewmodel
    viewModel {
        NoteViewmodel(
            androidApplication(),
            get()
        )
    }
}
