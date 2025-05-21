package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.UserDietPlanDAO
import com.example.minikeep.data.local.entity.DietPlan
import kotlinx.coroutines.flow.Flow

class DietPlanRepository(application: Application) {

    private val userDietPlanDao: UserDietPlanDAO =
        MiniKeepDatabase.getDatabase(application).DietPlanDao()

    // 插入单条计划
    suspend fun insertDietPlan(plan: DietPlan) {
        userDietPlanDao.insertDietPlan(plan)
    }

    // 插入多条
    suspend fun insertDietPlans(plans: List<DietPlan>) {
        userDietPlanDao.insertDietPlans(plans)
    }

    // 更新
    suspend fun updateDietPlan(plan: DietPlan) {
        userDietPlanDao.updateDietPlan(plan)
    }

    // 删除单条
    suspend fun deleteDietPlan(plan: DietPlan) {
        userDietPlanDao.deleteDietPlan(plan)
    }

    // 查询某用户所有饮食记录
    fun getAllDietPlansForUser(userId: Int): Flow<List<DietPlan>> {
        return userDietPlanDao.getAllDietPlansForUser(userId)
    }

    // 查询某用户某一餐段（0=早餐，1=午餐，2=晚餐）
    fun getDietPlansByMealType(userId: Int, mealType: Int): Flow<List<DietPlan>> {
        return userDietPlanDao.getDietPlansByMealType(userId, mealType)
    }

    // 删除该用户所有饮食记录
    suspend fun deleteAllForUser(userId: Int) {
        userDietPlanDao.deleteAllForUser(userId)
    }
}
