package com.example.workouttracker.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "muscle")
data class MuscleEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,
    var name : String,
    @ColumnInfo(name = "created_at")
    var createdAt : Long
    )