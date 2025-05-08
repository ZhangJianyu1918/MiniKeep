package com.example.minikeep.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minikeep.data.local.dao.UserDAO
import com.example.minikeep.data.local.dao.UserDetailDAO
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.local.entity.UserDetail

@Database(entities = [User::class, UserDetail::class], version = 1, exportSchema = false)
abstract class MiniKeepDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun userDetailDao(): UserDetailDAO

    companion object {
        @Volatile
        private var INSTANCE: MiniKeepDatabase ?= null

        fun getDatabase(context: Context): MiniKeepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MiniKeepDatabase::class.java,
                    "minikeep_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}