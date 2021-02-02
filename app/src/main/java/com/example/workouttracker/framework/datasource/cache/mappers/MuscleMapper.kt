package com.example.workouttracker.framework.datasource.cache.mappers

import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.domain.util.EntityMapper
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleMapper
@Inject
constructor(
private val dateUtil: DateUtil
) : EntityMapper<MuscleEntity, Muscle>{

    fun entityListToDomainList(entities : List<MuscleEntity>) : List<Muscle>{
        val muscleList = ArrayList<Muscle>()
        for(entity in entities){
            muscleList.add(
                mapFromEntity(entity = entity)
            )
        }
        return muscleList
    }

    fun domainListToEntityList(muscleList : List<Muscle>) : List<MuscleEntity>{
        val entityList = ArrayList<MuscleEntity>()
        for (muscle in muscleList){
            entityList.add(
                mapToEntity(muscle)
            )
        }
        return entityList
    }
    override fun mapFromEntity(entity: MuscleEntity): Muscle {
        return Muscle(
            id = entity.id,
            name = entity.name,
            createdAt = dateUtil.convertFromDbEntityTimeToStringDate(entity.createdAt)
        )
    }

    override fun mapToEntity(domainModel: Muscle): MuscleEntity {
        return MuscleEntity(
            id = domainModel.id,
            name = domainModel.name,
            createdAt = dateUtil.convertToDbEntityTimeToMillis(domainModel.createdAt)
        )
    }
}