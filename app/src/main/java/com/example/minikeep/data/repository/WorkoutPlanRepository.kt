package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.WorkoutPlanDAO
import com.example.minikeep.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

class WorkoutPlanRepository(application: Application) {

    private val workoutPlanDao: WorkoutPlanDAO =
        MiniKeepDatabase.getDatabase(application).WorkoutPlanDao()

    suspend fun insertWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.insertWorkoutPlan(plan)
    }

    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>) {
        workoutPlanDao.insertWorkoutPlans(plans)
    }

    suspend fun updateWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.updateWorkoutPlan(plan)
    }

    suspend fun deleteWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.deleteWorkoutPlan(plan)
    }

    fun getAllWorkoutPlansForUser(userId: Int): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.getAllWorkoutPlansForUser(userId)
    }

    fun searchWorkoutPlans(userId: Int, keyword: String): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.searchWorkoutPlans(userId, keyword)
    }

    suspend fun deleteAllForUser(userId: Int) {
        workoutPlanDao.deleteAllForUser(userId)
    }
}
