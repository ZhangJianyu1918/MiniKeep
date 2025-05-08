package com.example.minikeep.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(application)
    }

    val allUsers: Flow<List<User>> = userRepository.allUsers

    var loginUser by mutableStateOf<User?>(null)
        private set

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insert(user)
        }
    }

    suspend fun queryUser(id: Int): User {
        return withContext(Dispatchers.IO) {
            userRepository.queryById(id)
        }
    }

    fun queryUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.queryByEmailAndPassword(email, password)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.queryByEmailAndPassword(email, password)
            if (user != null) {
                loginUser = user
            }
        }
    }
}