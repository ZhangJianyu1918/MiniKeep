package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.UserDAO
import com.example.minikeep.data.local.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(application: Application) {

    private var userDao: UserDAO = MiniKeepDatabase.getDatabase(application).userDao()

    val allUsers: Flow<List<User>> = userDao.getAllUser()

    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    suspend fun queryById(id: Int): User {
        return userDao.getUserById(id)
    }

    suspend fun queryByEmailAndPassword(email: String, password: String): User {
        return userDao.getUserByEmailAndPassword(email, password)
    }
}