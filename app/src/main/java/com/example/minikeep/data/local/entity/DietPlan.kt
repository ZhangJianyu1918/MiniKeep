package com.example.minikeep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diet_plan",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DietPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val food: String,

    val mealType: Int,

    val isCompleted: Boolean
)
