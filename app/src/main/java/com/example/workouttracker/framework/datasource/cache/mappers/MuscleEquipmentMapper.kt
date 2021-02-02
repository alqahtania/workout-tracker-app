package com.example.workouttracker.framework.datasource.cache.mappers

import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.domain.util.EntityMapper
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MuscleEquipmentMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<MuscleEquipmentEntity, MuscleEquipment>
{

    fun entityListToDomainList(entities : List<MuscleEquipmentEntity>) : List<MuscleEquipment>{
        val muscleEquipmentList = ArrayList<MuscleEquipment>()
        for(entity in entities){
            muscleEquipmentList.add(
                mapFromEntity(entity = entity)
            )
        }
        return muscleEquipmentList
    }

    fun domainListToEntityList(muscleEquipList : List<MuscleEquipment>) : List<MuscleEquipmentEntity>{
        val entityList = ArrayList<MuscleEquipmentEntity>()
        for (muscle in muscleEquipList){
            entityList.add(
                mapToEntity(muscle)
            )
        }
        return entityList
    }

    override fun mapFromEntity(entity: MuscleEquipmentEntity): MuscleEquipment {
        return MuscleEquipment(
            id = entity.id,
            muscleId = entity.muscleId,
            name = entity.name,
            createdAt = dateUtil.convertFromDbEntityTimeToStringDate(entity.createdAt)
        )
    }

    override fun mapToEntity(domainModel: MuscleEquipment): MuscleEquipmentEntity {
        return MuscleEquipmentEntity(
            id = domainModel.id,
            muscleId = domainModel.muscleId,
            name = domainModel.name,
            createdAt = dateUtil.convertToDbEntityTimeToMillis(domainModel.createdAt)
        )
    }
}