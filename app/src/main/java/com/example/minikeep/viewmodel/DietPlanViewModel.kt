package com.example.minikeep.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.DietPlan
import com.example.minikeep.data.repository.DietPlanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DietPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DietPlanRepository(application)

    // 当前用户ID（可由登录后设置）
    private val _userId = MutableStateFlow<Int?>(null)

    // 获取全部饮食计划
    val allDietPlans: StateFlow<List<DietPlan>> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            repository.getAllDietPlansForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 设置当前登录用户 ID
    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    // 按餐段获取饮食记录（0=早餐，1=午餐，2=晚餐）
    fun getDietPlansByMealType(mealType: Int): Flow<List<DietPlan>> {
        val uid = _userId.value
        return if (uid != null) {
            repository.getDietPlansByMealType(uid, mealType)
        } else {
            flowOf(emptyList())
        }
    }

    // 插入
    fun addDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.insertDietPlan(plan)
    }

    // 更新
    fun updateDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.updateDietPlan(plan)
    }

    // 删除单条
    fun deleteDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.deleteDietPlan(plan)
    }

    // 删除该用户全部饮食记录
    fun deleteAll() = viewModelScope.launch {
        _userId.value?.let {
            repository.deleteAllForUser(it)
        }
    }
}
