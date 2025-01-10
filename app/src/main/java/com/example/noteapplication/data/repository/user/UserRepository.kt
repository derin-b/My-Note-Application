package com.example.noteapplication.data.repository.user

import com.example.noteapplication.data.entity.user.User

interface UserRepository {
    fun insert(user: User)

    suspend fun insertFromRemote(user: List<User>)
}