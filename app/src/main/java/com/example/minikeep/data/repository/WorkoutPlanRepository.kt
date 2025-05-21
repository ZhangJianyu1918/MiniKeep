package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.WorkoutPlanDAO
import com.example.minikeep.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

class WorkoutPlanRepository(application: Application) {

    private val workoutPlanDao: WorkoutPlanDAO =
        MiniKeepDatabase.getDatabase(application).WorkoutPlanDao()

    // 插入单条计划
    suspend fun insertWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.insertWorkoutPlan(plan)
    }

    // 插入多条
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>) {
        workoutPlanDao.insertWorkoutPlans(plans)
    }

    // 更新
    suspend fun updateWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.updateWorkoutPlan(plan)
    }

    // 删除单条
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanDao.deleteWorkoutPlan(plan)
    }

    // 查询某用户所有训练记录
    fun getAllWorkoutPlansForUser(userId: Int): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.getAllWorkoutPlansForUser(userId)
    }

    // 关键词模糊搜索
    fun searchWorkoutPlans(userId: Int, keyword: String): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.searchWorkoutPlans(userId, keyword)
    }

    // 删除该用户所有训练记录
    suspend fun deleteAllForUser(userId: Int) {
        workoutPlanDao.deleteAllForUser(userId)
    }
}
