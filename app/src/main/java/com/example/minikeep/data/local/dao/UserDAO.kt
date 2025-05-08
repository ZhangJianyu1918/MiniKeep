package com.example.minikeep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.minikeep.data.local.entity.User
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDAO {

    @Query("Select * from user")
    fun getAllUser(): Flow<List<User>>

    @Query("Select * from user where id = :id")
    suspend fun getUserById(id: Int): User

    @Query("Select * from user where email = :email and password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User

    @Insert
    suspend fun insertUser(user: User)


}