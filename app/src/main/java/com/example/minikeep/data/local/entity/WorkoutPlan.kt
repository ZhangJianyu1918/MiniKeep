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

    val userId: Int,             // 外键，关联 User 表

    val content: String,         // 锻炼内容，例如 "Deadlift"

    val targetSets: Int,         // 设定组数

    val completedSets: Int       // 实际完成组数
) {
    val progress: Float          // 自动计算完成比例
        get() = if (targetSets > 0) {
            (completedSets.toFloat() / targetSets).coerceIn(0f, 1f)
        } else 0f
}
