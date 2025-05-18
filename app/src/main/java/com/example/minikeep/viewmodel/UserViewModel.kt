package com.example.minikeep.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.repository.GoogleAuthenticationRepository
import com.example.minikeep.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: UserRepository

    private val googleAuthenticationRepository: GoogleAuthenticationRepository

    init {
        userRepository = UserRepository(application)
        googleAuthenticationRepository = GoogleAuthenticationRepository(getApplication())
    }

    val allUsers: Flow<List<User>> = userRepository.allUsers

    val googleSignInClient = googleAuthenticationRepository.googleSignInClient

    private var _loginUser = MutableStateFlow<User?>(null)

    val loginUser: StateFlow<User?> = _loginUser.asStateFlow()

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

    fun googleLogin() {
        googleSignInClient.signInIntent
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.queryByEmailAndPassword(email, password)
            _loginUser.value = user
        }
    }


}