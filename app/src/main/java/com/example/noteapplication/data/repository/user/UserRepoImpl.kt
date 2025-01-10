package com.example.noteapplication.data.repository.user

import com.example.noteapplication.data.dao.user.UserDao
import com.example.noteapplication.data.entity.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepoImpl(private val userDao: UserDao): UserRepository {
    override fun insert(user: User) {
        userDao.insert(user)
    }

    override suspend fun insertFromRemote(user: List<User>) {
        withContext(Dispatchers.IO) {
            val updatedData = mutableListOf<User>()
            if (user.isNotEmpty()) {
                user.forEach {
                    val updated = it.copy(synFlag = 1)
                    updatedData.add(updated)
                }
                userDao.insert(updatedData)
            }
        }
    }
}