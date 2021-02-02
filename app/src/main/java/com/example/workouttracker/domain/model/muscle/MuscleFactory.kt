package com.example.workouttracker.domain.model.muscle

import com.example.workouttracker.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleFactory
@Inject
constructor(
    private val dateUtil: DateUtil
) {


    fun createSingleMuscle(
        id: String? = null,
        name: String,
    ): Muscle {
        return Muscle(
            id = id ?: UUID.randomUUID().toString(),
            name = name,
            createdAt = dateUtil.getCurrentTimestamp()
        )
    }
}