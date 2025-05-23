package com.example.minikeep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_plan",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val content: String,

    val targetSets: Int,

    val completedSets: Int
) {
    val progress: Float
        get() = if (targetSets > 0) {
            (completedSets.toFloat() / targetSets).coerceIn(0f, 1f)
        } else 0f
}
