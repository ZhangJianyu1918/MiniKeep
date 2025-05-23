package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.UserDietPlanDAO
import com.example.minikeep.data.local.entity.DietPlan
import kotlinx.coroutines.flow.Flow

class DietPlanRepository(application: Application) {

    private val userDietPlanDao: UserDietPlanDAO =
        MiniKeepDatabase.getDatabase(application).DietPlanDao()

    suspend fun insertDietPlan(plan: DietPlan) {
        userDietPlanDao.insertDietPlan(plan)
    }

    suspend fun insertDietPlans(plans: List<DietPlan>) {
        userDietPlanDao.insertDietPlans(plans)
    }

    suspend fun updateDietPlan(plan: DietPlan) {
        userDietPlanDao.updateDietPlan(plan)
    }

    suspend fun deleteDietPlan(plan: DietPlan) {
        userDietPlanDao.deleteDietPlan(plan)
    }

    fun getAllDietPlansForUser(userId: Int): Flow<List<DietPlan>> {
        return userDietPlanDao.getAllDietPlansForUser(userId)
    }

    fun getDietPlansByMealType(userId: Int, mealType: Int): Flow<List<DietPlan>> {
        return userDietPlanDao.getDietPlansByMealType(userId, mealType)
    }

    suspend fun deleteAllForUser(userId: Int) {
        userDietPlanDao.deleteAllForUser(userId)
    }
}
