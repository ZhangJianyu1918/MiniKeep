package com.example.minikeep.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.local.entity.UserDetail
import com.example.minikeep.data.repository.UserDetailRepository
import com.example.minikeep.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserDetailViewModel(application: Application): AndroidViewModel(application) {
    private val userDetailRepository: UserDetailRepository
    init {
        userDetailRepository = UserDetailRepository(application)
    }

    val allUserDetail: Flow<List<UserDetail>> = userDetailRepository.allUserDetail
    private val cloudDatabase = FirebaseFirestore.getInstance()

    fun insertUserDetailIntoCloudDatabase(userDetail: UserDetail) {
        println("Before Insert data into cloud")
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = Firebase.auth.currentUser

            currentUser?.email?.let { email ->
                val cloudUserDetail = mapOf(
                    "uid" to currentUser.uid,
                    "email" to email,
                    "birthday" to userDetail.birthday,
                    "gender" to userDetail.gender,
                    "goal" to userDetail.goal,
                    "height" to userDetail.height,
                    "weight" to userDetail.weight,
                )
                println(cloudUserDetail)
                cloudDatabase.collection("cloud-user-detail").document(email).set(cloudUserDetail)
                    .addOnSuccessListener {
                        println("Insert user detail into firestore successfully.")
                    }
                    .addOnFailureListener { e ->
                        println("Error: $e")
                    }
            }
        }
    }

    fun insertUserDetail(userDetail: UserDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            userDetailRepository.upsert(userDetail)
        }
    }
    fun upsertUserDetail(userDetail: UserDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            userDetailRepository.upsert(userDetail)
        }
    }

}