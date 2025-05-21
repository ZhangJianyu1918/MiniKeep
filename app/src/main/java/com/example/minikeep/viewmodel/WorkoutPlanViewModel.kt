package com.example.minikeep.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.WorkoutPlan
import com.example.minikeep.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutPlanRepository(application)

    // 当前用户 ID（用于过滤数据）
    private val _userId = MutableStateFlow<Int?>(null)

    // 所有训练计划
    val allWorkoutPlans: StateFlow<List<WorkoutPlan>> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            repository.getAllWorkoutPlansForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 设置当前登录用户 ID
    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    // 关键词搜索
    fun search(keyword: String): Flow<List<WorkoutPlan>> {
        val uid = _userId.value
        return if (uid != null) {
            repository.searchWorkoutPlans(uid, keyword)
        } else {
            flowOf(emptyList())
        }
    }

    // 插入
    fun addWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.insertWorkoutPlan(plan)
    }

    // 更新（组数等变更）
    fun updateWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.updateWorkoutPlan(plan)
    }

    // 删除单条
    fun deleteWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.deleteWorkoutPlan(plan)
    }

    // 删除当前用户全部计划
    fun deleteAll() = viewModelScope.launch {
        _userId.value?.let {
            repository.deleteAllForUser(it)
        }
    }
}
