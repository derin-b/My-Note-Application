package com.example.noteapplication.di

/**
 * this is the list of standalone modules across the app
 * */
val appModule = listOf(
    databaseModule,
    repositoryModule,
    firebaseModule,
    domainModule,
    viewmodelModule
)
