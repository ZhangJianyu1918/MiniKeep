package com.example.minikeep.data.local.dao
import androidx.room.*
import com.example.minikeep.data.local.entity.DietPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDietPlanDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlan(dietPlan: DietPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlans(plans: List<DietPlan>)

    @Update
    suspend fun updateDietPlan(dietPlan: DietPlan)

    @Delete
    suspend fun deleteDietPlan(dietPlan: DietPlan)

    @Query("SELECT * FROM diet_plan WHERE userId = :userId ORDER BY id DESC")
    fun getAllDietPlansForUser(userId: Int): Flow<List<DietPlan>>

    @Query("SELECT * FROM diet_plan WHERE userId = :userId AND mealType = :mealType")
    fun getDietPlansByMealType(userId: Int, mealType: Int): Flow<List<DietPlan>>

    @Query("DELETE FROM diet_plan WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}