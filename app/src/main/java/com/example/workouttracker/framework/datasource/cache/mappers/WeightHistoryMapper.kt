package com.example.workouttracker.framework.datasource.cache.mappers

import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.domain.util.EntityMapper
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeightHistoryMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<WeightHistoryEntity, WeightHistory>
{

    fun entityListToDomainList(entities : List<WeightHistoryEntity>) : List<WeightHistory>{
        val weightHistory = ArrayList<WeightHistory>()
        for(entity in entities){
            weightHistory.add(
                mapFromEntity(entity = entity)
            )
        }
        return weightHistory
    }

    fun domainListToEntityList(muscleEquipList : List<WeightHistory>) : List<WeightHistoryEntity>{
        val entityList = ArrayList<WeightHistoryEntity>()
        for (muscle in muscleEquipList){
            entityList.add(
                mapToEntity(muscle)
            )
        }
        return entityList
    }
    override fun mapFromEntity(entity: WeightHistoryEntity): WeightHistory {
        return WeightHistory(
            id = entity.id,
            muscleEquipmentId = entity.muscleEquipmentId,
            weight = entity.weight,
            unit = entity.unit,
            createdAt = dateUtil.convertFromDbEntityTimeToStringDate(entity.createdAt),
            reps = entity.reps
        )
    }

    override fun mapToEntity(domainModel: WeightHistory): WeightHistoryEntity {
        return WeightHistoryEntity(
            id = domainModel.id,
            muscleEquipmentId = domainModel.muscleEquipmentId,
            weight = domainModel.weight,
            unit = domainModel.unit,
            createdAt = dateUtil.convertToDbEntityTimeToMillis(domainModel.createdAt),
            reps = domainModel.reps
        )
    }
}