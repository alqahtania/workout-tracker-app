package com.example.workouttracker.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "weight_history",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = MuscleEquipmentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("muscle_equipment_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    )
)
data class WeightHistoryEntity(
    @PrimaryKey(autoGenerate = false)
    var id : String,
    @ColumnInfo(name = "muscle_equipment_id", index = true)
    var muscleEquipmentId : String,
    var weight : Double,
    var unit : String,
    @ColumnInfo(name = "created_at")
    var createdAt : Long,
    var reps: Long? = null,
    var side: String? = null
)