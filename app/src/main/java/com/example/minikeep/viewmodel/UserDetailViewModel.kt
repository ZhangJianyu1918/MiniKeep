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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
                    "age" to userDetail.age,
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


    suspend fun queryUserDetailFromCloudDatabase(): UserDetail? {
        val currentUser = Firebase.auth.currentUser
        val email = currentUser?.email ?: return null

        return try {
            val document = cloudDatabase
                .collection("cloud-user-detail")
                .document(email)
                .get()
                .await()

            if (document != null && document.exists()) {
                UserDetail(
                    userId = -1,
                    birthday = document.getString("birthday") ?: "",
                    gender = document.getString("gender") ?: "",
                    goal = document.getString("goal") ?: "",
                    height = document.getDouble("height")?.toInt() ?: 0,
                    weight = document.getDouble("weight")?.toFloat() ?: 0f,
                    age = document.getLong("age")?.toInt() ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error getting user detail: $e")
            null
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
    fun getUserDetailByUserId(userId: Int, onLoaded: (UserDetail?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val detail = userDetailRepository.getUserDetailByUserId(userId)
            withContext(Dispatchers.Main) {
                onLoaded(detail)
            }
        }
    }


}