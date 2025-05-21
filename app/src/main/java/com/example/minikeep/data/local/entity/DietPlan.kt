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
    val id: Int = 0,             // 主键

    val userId: Int,             // 外键，关联 User 表

    val food: String,            // 吃的食物，例如 "Eggs and Toast"

    val mealType: Int            // 0: 早餐, 1: 午餐, 2: 晚餐
)
