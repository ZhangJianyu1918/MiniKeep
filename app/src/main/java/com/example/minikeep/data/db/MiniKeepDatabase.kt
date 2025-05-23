package com.example.minikeep.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minikeep.data.local.dao.CalendarEventDAO
import com.example.minikeep.data.local.dao.UserDAO
import com.example.minikeep.data.local.dao.UserDetailDAO
import com.example.minikeep.data.local.dao.UserDietPlanDAO
import com.example.minikeep.data.local.dao.WorkoutPlanDAO
import com.example.minikeep.data.local.entity.CalendarEvent
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.local.entity.UserDetail
import com.example.minikeep.data.local.entity.DietPlan
import com.example.minikeep.data.local.entity.WorkoutPlan


@Database(entities = [User::class, UserDetail::class, CalendarEvent::class, DietPlan::class, WorkoutPlan::class], version = 6, exportSchema = false)
abstract class MiniKeepDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun userDetailDao(): UserDetailDAO
    abstract fun calendarEventDao(): CalendarEventDAO
    abstract fun DietPlanDao(): UserDietPlanDAO
    abstract fun WorkoutPlanDao(): WorkoutPlanDAO


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