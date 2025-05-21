package com.example.minikeep.data.local.dao

import androidx.room.*
import com.example.minikeep.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutPlanDAO {
    // 插入单条计划
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan)

    // 插入多条计划
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>)

    // 更新记录（例如已完成组数）
    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    // 删除计划
    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan)

    // 获取某个用户的所有训练记录
    @Query("SELECT * FROM workout_plan WHERE userId = :userId ORDER BY id DESC")
    fun getAllWorkoutPlansForUser(userId: Int): Flow<List<WorkoutPlan>>

    // 查询某个用户的指定锻炼项目（模糊搜索）
    @Query("SELECT * FROM workout_plan WHERE userId = :userId AND content LIKE '%' || :keyword || '%'")
    fun searchWorkoutPlans(userId: Int, keyword: String): Flow<List<WorkoutPlan>>

    // 删除某个用户的全部训练记录
    @Query("DELETE FROM workout_plan WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}