package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.UserDAO
import com.example.minikeep.data.local.dao.UserDetailDAO
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.local.entity.UserDetail
import kotlinx.coroutines.flow.Flow

class UserDetailRepository(application: Application) {
    private var userDetailDAO: UserDetailDAO = MiniKeepDatabase.getDatabase(application).userDetailDao()

    val allUserDetail: Flow<List<UserDetail>> = userDetailDAO.getAllUserDetail()

    suspend fun insert(userDetail: UserDetail) {
        userDetailDAO.insertUserDetail(userDetail)
    }

    suspend fun getUserDetailByUserId(userId: Int): UserDetail? {
        return userDetailDAO.getUserDetailById(userId)
    }


    suspend fun upsert(userDetail: UserDetail) {
        val existing = userDetailDAO.getUserDetailById(userDetail.userId)
        if (existing == null) {
            userDetailDAO.insertUserDetail(userDetail)
        } else {
            userDetailDAO.updateUserDetail(userDetail)
        }
    }

}