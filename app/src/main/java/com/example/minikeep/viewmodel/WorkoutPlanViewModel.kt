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

    private val _userId = MutableStateFlow<Int?>(null)

    val allWorkoutPlans: StateFlow<List<WorkoutPlan>> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            repository.getAllWorkoutPlansForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun search(keyword: String): Flow<List<WorkoutPlan>> {
        val uid = _userId.value
        return if (uid != null) {
            repository.searchWorkoutPlans(uid, keyword)
        } else {
            flowOf(emptyList())
        }
    }

    fun addWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.insertWorkoutPlan(plan)
    }

    fun updateWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.updateWorkoutPlan(plan)
    }

    fun deleteWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch {
        repository.deleteWorkoutPlan(plan)
    }

    fun deleteAll() = viewModelScope.launch {
        _userId.value?.let {
            repository.deleteAllForUser(it)
        }
    }
}
