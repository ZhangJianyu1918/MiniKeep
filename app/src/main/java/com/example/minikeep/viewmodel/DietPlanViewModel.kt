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

    private val _userId = MutableStateFlow<Int?>(null)

    val allDietPlans: StateFlow<List<DietPlan>> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            repository.getAllDietPlansForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun getDietPlansByMealType(mealType: Int): Flow<List<DietPlan>> {
        val uid = _userId.value
        return if (uid != null) {
            repository.getDietPlansByMealType(uid, mealType)
        } else {
            flowOf(emptyList())
        }
    }

    fun addDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.insertDietPlan(plan)
    }

    fun updateDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.updateDietPlan(plan)
    }

    fun deleteDietPlan(plan: DietPlan) = viewModelScope.launch {
        repository.deleteDietPlan(plan)
    }

    fun deleteAll() = viewModelScope.launch {
        _userId.value?.let {
            repository.deleteAllForUser(it)
        }
    }
}
