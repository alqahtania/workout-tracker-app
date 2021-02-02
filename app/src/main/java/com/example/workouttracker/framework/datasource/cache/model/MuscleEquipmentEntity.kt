package com.example.workouttracker.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "muscle_equipment",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = MuscleEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("muscle_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    )
)
data class MuscleEquipmentEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "muscle_id", index = true)
    var muscleId: String,
    var name : String,
    @ColumnInfo(name = "created_at")
    var createdAt : Long

)