package com.example.minikeep.data.local.dao
import androidx.room.*
import com.example.minikeep.data.local.entity.DietPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDietPlanDAO {
    // 插入单条饮食计划
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlan(dietPlan: DietPlan)

    // 插入多条
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlans(plans: List<DietPlan>)

    // 更新
    @Update
    suspend fun updateDietPlan(dietPlan: DietPlan)

    // 删除
    @Delete
    suspend fun deleteDietPlan(dietPlan: DietPlan)

    // 查询某个用户的所有饮食记录（Flow 可观察性更适合 Compose）
    @Query("SELECT * FROM diet_plan WHERE userId = :userId ORDER BY id DESC")
    fun getAllDietPlansForUser(userId: Int): Flow<List<DietPlan>>

    // （可选）按用户和餐段查询当天的记录
    @Query("SELECT * FROM diet_plan WHERE userId = :userId AND mealType = :mealType")
    fun getDietPlansByMealType(userId: Int, mealType: Int): Flow<List<DietPlan>>

    // （可选）删除该用户所有饮食记录
    @Query("DELETE FROM diet_plan WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}