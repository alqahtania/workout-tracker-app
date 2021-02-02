package com.example.workouttracker.cache.implementation

import com.example.workouttracker.cache.CacheResponseHandler
import com.example.workouttracker.cache.abstraction.WeightHistoryDaoService
import com.example.workouttracker.cache.dao.WeightHistoryDao
import com.example.workouttracker.cache.util.safeCacheCall
import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.framework.datasource.cache.mappers.WeightHistoryMapper
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity
import com.example.workouttracker.framework.presentation.weighthistorylist.state.WeightHistoryViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeightHistoryDaoServiceImpl
@Inject
constructor(
    private val weightHistoryDao: WeightHistoryDao,
    private val mapper: WeightHistoryMapper
) : WeightHistoryDaoService {

    override fun insertWeightHistory(weightHistory: WeightHistory): Flow<DataState<WeightHistoryViewState>?> =
        flow {
            val weightEntity = mapper.mapToEntity(weightHistory)

            val cacheResult = safeCacheCall(IO) {
                weightHistoryDao.insertWeightHistory(weightEntity)
            }

            val cacheResponse = object : CacheResponseHandler<WeightHistoryViewState, Long>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Long): DataState<WeightHistoryViewState>? {
                    return if (resultObject > 0) {
                        val viewState = WeightHistoryViewState(insertedWeightHistory = weightHistory)
                        DataState.data(
                            responseMessage = ResponseMessage(
                                message = INSERT_NEW_MUSCLE_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = viewState
                        )
                    }else{
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = INSERT_NEW_MUSCLE_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }

            }.getResult()

            emit(cacheResponse)
        }

    override fun getAllWeightHistory(): Flow<List<WeightHistoryEntity>> {
        return weightHistoryDao.getAllWeightHistory()
    }

    override fun searchHistoryByMuscleEquipmentId(muscleEquipmentId: String): Flow<List<WeightHistoryEntity>> {
        return weightHistoryDao.searchHistoryByMuscleEquipmentId(muscleEquipmentId)
    }

    override fun deleteAWeightHistory(weightHistory: WeightHistory): Flow<DataState<WeightHistoryViewState>?>
    = flow{
        val weightEntity = mapper.mapToEntity(weightHistory)

        val cacheResult = safeCacheCall(IO){
            weightHistoryDao.deleteAWeightHistory(weightEntity)
        }

        val cacheResponse = object : CacheResponseHandler<WeightHistoryViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<WeightHistoryViewState>? {
                return if(resultObject > 0){
                    val viewState = WeightHistoryViewState(deletedWeightHistory = weightHistory)
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = DELETE_WEIGHT_HISTORY_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = viewState
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = DELETE_WEIGHT_HISTORY_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)
    }

    override fun deleteAWeightHistoryById(weightHistoryId: String): Flow<DataState<WeightHistoryViewState>?>
    = flow{

        val cacheResult = safeCacheCall(IO){
            weightHistoryDao.deleteAWeightHistoryById(weightHistoryId)
        }

        val cacheResponse = object : CacheResponseHandler<WeightHistoryViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<WeightHistoryViewState>? {
                return if(resultObject > 0){
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = DELETE_WEIGHT_HISTORY_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = null
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = DELETE_WEIGHT_HISTORY_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)
    }

    override fun updateWeightHistory(weightHistory: WeightHistory): Flow<DataState<WeightHistoryViewState>?>
    = flow{
        val weightEntity = mapper.mapToEntity(weightHistory)

        val cacheResult = safeCacheCall(IO){
            weightHistoryDao.updateWeightHistory(weightEntity)
        }

        val cacheResponse = object : CacheResponseHandler<WeightHistoryViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<WeightHistoryViewState>? {
                return if(resultObject > 0){
                    val viewState = WeightHistoryViewState(updatedWeightHistory = weightHistory)
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = UPDATE_WEIGHT_HISTORY_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = viewState
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = UPDATE_WEIGHT_HISTORY_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

    }

    companion object{
        const val INSERT_NEW_MUSCLE_SUCCESS = "Successfully inserted the new weight history."
        const val INSERT_NEW_MUSCLE_FAILED = "Failed to insert the new weight history."
        const val DELETE_WEIGHT_HISTORY_SUCCESS = "Successfully deleted weight history."
        const val DELETE_WEIGHT_HISTORY_FAILED = "Failed to delete the weight history."
        const val UPDATE_WEIGHT_HISTORY_SUCCESS = "Successfully updated the weight history."
        const val UPDATE_WEIGHT_HISTORY_FAILED = "Failed to update the weight history"

    }
}