package com.example.minikeep.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "user_detail", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )
])
data class UserDetail(
    @PrimaryKey @ColumnInfo(name = "user_id") val userId: Int,
    val age: Int,
    val height: Int,
    val weight: Float,
    val birthday: String,
    val gender: String,
    val goal: String
)
