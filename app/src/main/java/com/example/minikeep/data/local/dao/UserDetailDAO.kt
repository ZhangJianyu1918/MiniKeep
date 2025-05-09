package com.example.minikeep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.minikeep.data.local.entity.UserDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailDAO {

    @Query("Select * from user_detail")
    fun getAllUserDetail(): Flow<List<UserDetail>>

    @Query("Select * from user_detail where user_id = :id")
    fun getUserDetailById(id: Int): UserDetail

    @Insert
    suspend fun insertUserDetail(userDetail: UserDetail)

    @Update
    suspend fun updateUserDetail(userDetail: UserDetail)

}