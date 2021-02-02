package com.example.workouttracker.domain.state

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class DataChannelManager<ViewState> {

    private var channelScope: CoroutineScope? = null


    fun setupChannel() {
        cancelJobs()

    }


    abstract fun handleNewData(data: ViewState)

    abstract fun handleNewResponseMessage(responseMessage: ResponseMessage)

    fun launchJob(
        jobFunction: Flow<DataState<ViewState>?>
    ) {
        jobFunction
            .onEach { dataState ->
                dataState?.let { dState ->
                    withContext(Main) {
                        dState.data?.let {
                            handleNewData(it)
                        }
                        dState.responseMessage?.let {
                            handleNewResponseMessage(it)
                        }
                    }

                }

            }
            .launchIn(getChannelScope())
    }


    fun getChannelScope(): CoroutineScope {
        return channelScope ?: setupNewChannelScope(CoroutineScope(Dispatchers.IO))
    }

    private fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope {
        channelScope = coroutineScope
        return channelScope as CoroutineScope
    }

    fun cancelJobs() {
        if (channelScope != null) {
            if (channelScope?.isActive == true) {
                channelScope?.cancel()
            }
            channelScope = null
        }
    }

}