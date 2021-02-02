package com.example.workouttracker.cache.implementation

import com.example.workouttracker.cache.CacheResponseHandler
import com.example.workouttracker.cache.abstraction.MuscleEquipmentDaoService
import com.example.workouttracker.cache.dao.MuscleEquipmentDao
import com.example.workouttracker.cache.util.safeCacheCall
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleEquipmentMapper
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import com.example.workouttracker.framework.presentation.muscleequiplist.state.MuscleEquipmentViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MuscleEquipeDaoServiceImpl
@Inject
constructor(
    private val muscleEquipmentDao: MuscleEquipmentDao,
    private val mapper: MuscleEquipmentMapper
) : MuscleEquipmentDaoService {

    override fun insertMuscleEquipment(muscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?> =
        flow {
            val muscleEquipEntity = mapper.mapToEntity(muscleEquipment)

            val cacheResult = safeCacheCall(IO) {
                muscleEquipmentDao.insertMuscleEquipment(muscleEquipEntity)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleEquipmentViewState, Long>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Long): DataState<MuscleEquipmentViewState>? {
                    return if (resultObject > 0) {
                        val viewState = MuscleEquipmentViewState(newInsertedNote = muscleEquipment)
                        DataState.data(
                            responseMessage = ResponseMessage(
                                message = INSERT_MUSCLE_EQUIPMENT_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = viewState
                        )
                    } else {
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = INSERT_MUSCLE_EQUIPMENT_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()

            emit(cacheResponse)

        }

    override fun getAllMuscleEquipments(): Flow<List<MuscleEquipmentEntity>> {
        return muscleEquipmentDao.getAllMuscleEquipments()
    }

    override fun searchMuscleEquipByMuscleId(muscleId: String): Flow<List<MuscleEquipmentEntity>> {
        return muscleEquipmentDao.searchMuscleEquipByMuscleId(muscleId)
    }

    override fun searchMuscleEquipmentById(muscleEquipId: String): Flow<DataState<MuscleEquipmentViewState>?> =
        flow {

            val cacheResult = safeCacheCall(IO) {
                muscleEquipmentDao.searchMuscleEquipmentById(muscleEquipId)
            }

            val cacheResponse =
                object : CacheResponseHandler<MuscleEquipmentViewState, MuscleEquipmentEntity>(
                    response = cacheResult
                ) {
                    override fun handleSuccess(resultObject: MuscleEquipmentEntity): DataState<MuscleEquipmentViewState>? {
                        val muscleEquip = mapper.mapFromEntity(resultObject)
                        val viewState =
                            MuscleEquipmentViewState(singleMuscleEquipSearch = muscleEquip)
                        return DataState.data(
                            responseMessage = ResponseMessage(
                                message = SEARCH_MUSCLE_EQUIPMENT_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = viewState
                        )
                    }
                }.getResult()
            emit(cacheResponse)
        }

    override fun deleteAMuscleEquipment(muscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?> =
        flow {
            val muscleEquip = mapper.mapToEntity(muscleEquipment)

            val cacheResult = safeCacheCall(IO) {
                muscleEquipmentDao.deleteAMuscleEquipment(muscleEquip)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleEquipmentViewState, Int>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Int): DataState<MuscleEquipmentViewState>? {
                    return if (resultObject > 0) {
                        val viewState =
                            MuscleEquipmentViewState(deletedMuscleEquipment = muscleEquipment)
                        DataState.data(
                            responseMessage = ResponseMessage(
                                message = DELETE_MUSCLE_EQUIPMENT_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = viewState
                        )
                    } else {
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = DELETE_MUSCLE_EQUIPMENT_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()

            emit(cacheResponse)
        }

    override fun deleteAMuscleEquipmentById(muscleEquipmentId: String): Flow<DataState<MuscleEquipmentViewState>?> =
        flow {


            val cacheResult = safeCacheCall(IO) {
                muscleEquipmentDao.deleteAMuscleEquipmentById(muscleEquipmentId)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleEquipmentViewState, Int>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Int): DataState<MuscleEquipmentViewState>? {
                    return if (resultObject > 0) {
                        DataState.data(
                            responseMessage = ResponseMessage(
                                message = DELETE_MUSCLE_EQUIPMENT_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = null
                        )
                    } else {
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = DELETE_MUSCLE_EQUIPMENT_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()

            emit(cacheResponse)
        }

    override fun updateAMuscleEquipment(updatedMuscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?> =
        flow {

            val updatedMuscleEquip = mapper.mapToEntity(updatedMuscleEquipment)

            val cacheResult = safeCacheCall(IO) {
                muscleEquipmentDao.updateAMuscleEquipment(updatedMuscleEquip)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleEquipmentViewState, Int>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Int): DataState<MuscleEquipmentViewState>? {
                    return if (resultObject > 0) {
                        val viewState =
                            MuscleEquipmentViewState(updatedMuscleEquipment = updatedMuscleEquipment)
                        DataState.data(
                            responseMessage = ResponseMessage(
                                messageType = MessageType.Success(),
                                message = UPDATE_MUSCLE_EQUIPMENT_SUCCESS
                            ),
                            data = viewState
                        )
                    } else {
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = UPDATE_MUSCLE_EQUIPMENT_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()

            emit(cacheResponse)
        }

    companion object {
        const val INSERT_MUSCLE_EQUIPMENT_SUCCESS = "Successfully inserted muscle equipment."
        const val INSERT_MUSCLE_EQUIPMENT_FAILED = "Failed to insert muscle equipment."
        const val SEARCH_MUSCLE_EQUIPMENT_SUCCESS = "Successfully found one muscle equipment."
        const val DELETE_MUSCLE_EQUIPMENT_SUCCESS = "Successfully deleted the muscle equipment."
        const val DELETE_MUSCLE_EQUIPMENT_FAILED = "Failed to delete the muscle equipment."
        const val UPDATE_MUSCLE_EQUIPMENT_SUCCESS = "Successfully updated the muscle equipment."
        const val UPDATE_MUSCLE_EQUIPMENT_FAILED = "Failed to update the muscle equipment."
    }
}