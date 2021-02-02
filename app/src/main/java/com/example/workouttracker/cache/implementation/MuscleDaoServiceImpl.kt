package com.example.workouttracker.cache.implementation

import com.example.workouttracker.cache.CacheResponseHandler
import com.example.workouttracker.cache.abstraction.MuscleDaoService
import com.example.workouttracker.cache.dao.MuscleDao
import com.example.workouttracker.cache.util.safeCacheCall
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.domain.state.ViewState
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleMapper
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MuscleDaoServiceImpl
@Inject
constructor(
    private val muscleDao: MuscleDao,
    private val muscleMapper: MuscleMapper
) : MuscleDaoService {

    override  fun insertMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?> =
        flow {
            val newMuscle = muscleMapper.mapToEntity(muscle)
            val cacheResult = safeCacheCall(IO) {
                muscleDao.insertMuscle(muscle = newMuscle)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleListViewState, Long>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: Long): DataState<MuscleListViewState>? {
                    return if (resultObject > 0) {
                        val viewState = MuscleListViewState(
                            newMuscle = muscle
                        )
                        DataState.data(
                            responseMessage = ResponseMessage(
                                message = INSERT_MUSCLE_SUCCESS,
                                messageType = MessageType.Success()
                            ),
                            data = viewState
                        )
                    } else {
                        DataState.error(
                            responseMessage = ResponseMessage(
                                message = INSERT_MUSCLE_FAILED,
                                messageType = MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()

            emit(cacheResponse)
        }

    override fun searchMuscleById(id: String): Flow<DataState<MuscleListViewState>?> =
        flow {

            val cacheResult = safeCacheCall(IO) {
                muscleDao.searchMuscleById(id)
            }

            val cacheResponse = object : CacheResponseHandler<MuscleListViewState, MuscleEntity>(
                response = cacheResult
            ) {
                override fun handleSuccess(resultObject: MuscleEntity): DataState<MuscleListViewState>? {

                    val muscle = muscleMapper.mapFromEntity(resultObject)
                    val viewState = MuscleListViewState(singleMuscleSearch = muscle)
                    return DataState.data(
                        responseMessage = ResponseMessage(
                            message = SEARCH_FOR_MUSCLE_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = viewState
                    )

                }
            }.getResult()

            emit(cacheResponse)

        }

    override fun getAllMuscles(): Flow<List<MuscleEntity>> {
        return muscleDao.getAllMuscles()
    }

    override fun deleteMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?> = flow {

        val muscleEntity = muscleMapper.mapToEntity(muscle)

        val cacheResult = safeCacheCall(IO){
            muscleDao.deleteMuscle(muscleEntity = muscleEntity)
        }

        val cacheResponse = object : CacheResponseHandler<MuscleListViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<MuscleListViewState>? {
                return if(resultObject > 0){
                    val viewState = MuscleListViewState(deleteMuscle = muscle)
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = DELETE_MUSCLE_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = viewState
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = DELETE_MUSCLE_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)
    }

    override fun deleteMuscleById(muscleId: String): Flow<DataState<MuscleListViewState>?> = flow{

        val cacheResult = safeCacheCall(IO){
            muscleDao.deleteMuscleById(muscleId)
        }

        val cacheResponse = object : CacheResponseHandler<MuscleListViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<MuscleListViewState>? {
                return if(resultObject > 0){
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = DELETE_MUSCLE_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = null
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = DELETE_MUSCLE_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

    }

    override fun updateMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?> = flow {

        val muscleEntity = muscleMapper.mapToEntity(muscle)
        val cacheResult = safeCacheCall(IO){
            muscleDao.updateMuscle(muscleEntity)
        }

        val cacheResponse = object : CacheResponseHandler<MuscleListViewState, Int>(
            response = cacheResult
        ){
            override fun handleSuccess(resultObject: Int): DataState<MuscleListViewState>? {
                return if(resultObject > 0){
                    val viewState = MuscleListViewState(updatedMuscle = muscle)
                    DataState.data(
                        responseMessage = ResponseMessage(
                            message = UPDATE_MUSCLE_SUCCESS,
                            messageType = MessageType.Success()
                        ),
                        data = viewState
                    )
                }else{
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = UPDATE_MUSCLE_FAILED,
                            messageType = MessageType.Error()
                        )
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

    }

    companion object {
        const val INSERT_MUSCLE_SUCCESS = "Successfully inserted new muscle."
        const val INSERT_MUSCLE_FAILED = "Failed to insert new muscle."
        const val SEARCH_FOR_MUSCLE_SUCCESS = "Successfully found the muscle."
        const val DELETE_MUSCLE_SUCCESS = "Successfully deleted the muscle."
        const val DELETE_MUSCLE_FAILED = "Failed to delete muscle."
        const val UPDATE_MUSCLE_SUCCESS = "Successfully updated the muscle."
        const val UPDATE_MUSCLE_FAILED =  "Failed to update the muscle."

    }
}