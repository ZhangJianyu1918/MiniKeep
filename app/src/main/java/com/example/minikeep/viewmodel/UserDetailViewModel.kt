package com.example.minikeep.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.local.entity.UserDetail
import com.example.minikeep.data.repository.UserDetailRepository
import com.example.minikeep.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserDetailViewModel(application: Application): AndroidViewModel(application) {
    private val userDetailRepository: UserDetailRepository
    init {
        userDetailRepository = UserDetailRepository(application)
    }

    val allUserDetail: Flow<List<UserDetail>> = userDetailRepository.allUserDetail

    fun insertUserDetail(userDetail: UserDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            userDetailRepository.insert(userDetail)
        }
    }
}