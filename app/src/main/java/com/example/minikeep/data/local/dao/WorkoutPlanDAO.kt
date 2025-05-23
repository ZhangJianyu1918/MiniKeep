package com.example.minikeep.data.local.dao

import androidx.room.*
import com.example.minikeep.data.local.entity.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>)

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan)

    @Query("SELECT * FROM workout_plan WHERE userId = :userId ORDER BY id DESC")
    fun getAllWorkoutPlansForUser(userId: Int): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plan WHERE userId = :userId AND content LIKE '%' || :keyword || '%'")
    fun searchWorkoutPlans(userId: Int, keyword: String): Flow<List<WorkoutPlan>>

    @Query("DELETE FROM workout_plan WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}